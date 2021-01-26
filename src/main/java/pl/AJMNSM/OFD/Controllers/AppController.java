package pl.AJMNSM.OFD.Controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.AJMNSM.OFD.categories.Categories;
import pl.AJMNSM.OFD.categories.CategoriesRepository;
import pl.AJMNSM.OFD.categories.DefaultCategories;
import pl.AJMNSM.OFD.core.users.User;
import pl.AJMNSM.OFD.core.users.UserRepository;
import pl.AJMNSM.OFD.finances.Finances;
import pl.AJMNSM.OFD.finances.FinancesGrouped;
import pl.AJMNSM.OFD.finances.FinancesRepository;

@Controller
public class AppController {
		
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FinancesRepository financesRepository;
    
    @Autowired
    private CategoriesRepository categoriesRepository;
    
    public String getUserEmail() {
		UserDetails loggedUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loggedUserName = loggedUser.getUsername();
		String loggedUserEmail = userRepository.getEmailByName(loggedUserName);
		return loggedUserEmail;
	}

    @GetMapping("")
    public String viewHomePage(){return "index";}

    @GetMapping("/signup")
    public String viewRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/processSignUp")
    public String processRegistration(User user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "registration_succes";
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
    	String email = getUserEmail();
    	String income30 = financesRepository.getIncomeByDays(email, 30);
    	model.addAttribute("income30",income30);
    	String expenditure30 = financesRepository.getExpenditureByDays(email, 30);
    	model.addAttribute("expenditure30",expenditure30);
    	ArrayList<Finances> criticalExpenditure = financesRepository.getCriticalExpenditure(new BigDecimal(String.valueOf(-1000.00)), email, 21);
    	model.addAttribute("criticalExpenditure",criticalExpenditure);
    	return "main";
    }

    @GetMapping("/login")
    public String wiewLoginPage(){
        return "login";
    }
    
    @GetMapping("/input")
    public String inputPage(Model model) {
    	LocalDate now = LocalDate.now();
    	String today = now.toString();
    	model.addAttribute("today",today);
    	ArrayList<String> categories = new ArrayList<String>();
    	categories.addAll(DefaultCategories.getDefaultCategories());
    	ArrayList<String> userCategories = categoriesRepository.getCategoriesByEmail(getUserEmail());
    	if(!userCategories.isEmpty()) {
    		categories.addAll(userCategories);
    	}
    	Collections.sort(categories);
    	model.addAttribute("categories",categories);
    	return "input";
    }
    
    @PostMapping("/input_process")
    public String processInput(@RequestParam("inputDate") String inputDate,
    		@RequestParam("type") String type,
    		@RequestParam("categories") String category,
    		@RequestParam("amount") String amount,
    		@RequestParam("description") String description,
    		Model model) {
    	Finances finances = new Finances();
    	finances.setEmail(getUserEmail());
    	finances.setDate(LocalDate.parse(inputDate));
    	finances.setCategory(category);
    	finances.setDescription(description);
    	amount = amount.replace(',', '.');
    	if(type.equals("exp")) {
    		amount = "-" + amount;
    	}
    	finances.setAmount(new BigDecimal(String.valueOf(amount)));
    	financesRepository.save(finances);
    	model.addAttribute("finances",finances);
    	return "input_process";
    }
    
    @GetMapping("/delete_entry")
    public String deleteEntry(@RequestParam("id") String id){
    	if(financesRepository.findById(Long.parseLong(id)).isPresent()) {
    		Finances temp = financesRepository.findById(Long.parseLong(id)).get();
    		if(temp.getEmail().equals(getUserEmail())) {
    			financesRepository.deleteById(Long.parseLong(id));
    		}
    	}
    	return "redirect:main";
    }
    
    @GetMapping("/view")
    public String viewPage(Model model) {
    	LocalDate now = LocalDate.now();
    	String today = now.toString();
    	model.addAttribute("today",today);
    	return "view";
    }
    
    @PostMapping("/view_redir")
    public String viewRedir(@RequestParam("viewScope") String viewScope,
    		@RequestParam("viewType") String viewType,
    		@RequestParam("viewPeriod") String viewPeriod,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate) {
    	if(!viewPeriod.equals("other")) {
    		LocalDate end = LocalDate.now();
    		LocalDate start = end.minusDays(Long.parseLong(viewPeriod));
    		startDate = start.toString();
    		endDate = end.toString();
    	}
    	String url;
    	if(viewType.equals("cat")){
    		url = "redirect:view_category?viewScope=" + viewScope + "&startDate=" + startDate + "&endDate=" + endDate + "&sortParam=category&sortType=asc";
    	}
    	else {
    		url = "redirect:view_detail?viewScope=" + viewScope + "&startDate=" + startDate + "&endDate=" + endDate + "&sortParam=date&sortType=desc";
    	}
    	return url;
    }
    
    @GetMapping("/view_detail")
    public String detailedView(@RequestParam("viewScope") String viewScope,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("sortParam") String sortParam,
    		@RequestParam("sortType") String sortType,
    		Model model) {
    	model.addAttribute("viewScope",viewScope);
    	model.addAttribute("startDate",startDate);
    	model.addAttribute("endDate",endDate);
    	model.addAttribute("sortParam",sortParam);
    	model.addAttribute("sortType",sortType);
    	String totalIncome = financesRepository.getTotalIncomeByPeriod(getUserEmail(), startDate, endDate);
    	String totalExpenditure = financesRepository.getTotalExpenditureByPeriod(getUserEmail(), startDate, endDate);
    	String totalBalance = (new BigDecimal(String.valueOf(totalIncome)).add(new BigDecimal(String.valueOf(totalExpenditure)))).toString();
    	model.addAttribute("totalIncome",totalIncome);
    	model.addAttribute("totalExpenditure",totalExpenditure);
    	model.addAttribute("totalBalance",totalBalance);
    	ArrayList<Finances> data = new ArrayList<Finances>();
    	switch(viewScope) {
    	case "both":
    		data = financesRepository.getDataByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	case "inc":
    		data = financesRepository.getIncomeByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	case "exp":
    		data = financesRepository.getExpenditureByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	}
    	switch(sortParam) {
    	case "date":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (Finances a, Finances b) -> a.getDate().compareTo(b.getDate()));
    		}
    		else {
    			Collections.sort(data, (Finances a, Finances b) -> b.getDate().compareTo(a.getDate()));
    		}
    		break;
    	case "amount":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (Finances a, Finances b) -> a.getAmount().compareTo(b.getAmount()));
    		}
    		else {
    			Collections.sort(data, (Finances a, Finances b) -> b.getAmount().compareTo(a.getAmount()));
    		}
    		break;
    	case "category":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (Finances a, Finances b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
    		}
    		else {
    			Collections.sort(data, (Finances a, Finances b) -> b.getCategory().compareToIgnoreCase(a.getCategory()));
    		}
    		break;
    	case "description":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (Finances a, Finances b) -> a.getDescription().compareToIgnoreCase(b.getDescription()));
    		}
    		else {
    			Collections.sort(data, (Finances a, Finances b) -> b.getDescription().compareToIgnoreCase(a.getDescription()));
    		}
    		break;
    	}
    	model.addAttribute("data", data);
    	return "view_detail";
    }
    
    @GetMapping("/delete_entry_from_view")
    public String deleteEntryFromView(@RequestParam("id") String id,
    		@RequestParam("viewScope") String viewScope,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("sortParam") String sortParam,
    		@RequestParam("sortType") String sortType) {
    	if(financesRepository.findById(Long.parseLong(id)).isPresent()) {
    		Finances temp = financesRepository.findById(Long.parseLong(id)).get();
    		if(temp.getEmail().equals(getUserEmail())) {
    			financesRepository.deleteById(Long.parseLong(id));
    		}
    	}
    	String url = "redirect:view_detail?viewScope=" + viewScope + "&startDate=" + startDate + "&endDate=" + endDate + "&sortParam=" + sortParam + "&sortType=" + sortType;
    	return url;
    }
    
    @GetMapping("/cat_manager")
    public String categoriesManager(Model model) {
    	ArrayList<String> defaultCategories = new ArrayList<String>();
    	defaultCategories.addAll(DefaultCategories.getDefaultCategories());
    	ArrayList<Categories> userCategories = categoriesRepository.getUserCategoriesByEmailFull(getUserEmail());
    	Collections.sort(defaultCategories);
    	Collections.sort(userCategories, (Categories a, Categories b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
    	model.addAttribute("defaultCategories",defaultCategories);
    	model.addAttribute("userCategories",userCategories);
    	return "cat_manager";
    }
    
    @GetMapping("/delete_category")
    public String deleteCategory(@RequestParam("id") String id){
    	if(categoriesRepository.findById(Long.parseLong(id)).isPresent()) {
    		Categories temp = categoriesRepository.findById(Long.parseLong(id)).get();
    		if(temp.getEmail().equals(getUserEmail())) {
    			categoriesRepository.deleteById(Long.parseLong(id));
    		}
    	}
    	return "redirect:cat_manager";
    }
    
    @PostMapping("/add_category")
    public String processInput(@RequestParam("newCategory") String newCategory) {
    	Categories category = new Categories();
    	category.setEmail(getUserEmail());
    	category.setCategory(newCategory);
    	categoriesRepository.save(category);
    	return "redirect:cat_manager";
    }
    
    @GetMapping("/view_category")
    public String categoryView(@RequestParam("viewScope") String viewScope,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("sortParam") String sortParam,
    		@RequestParam("sortType") String sortType,
    		Model model) {
    	model.addAttribute("viewScope",viewScope);
    	model.addAttribute("startDate",startDate);
    	model.addAttribute("endDate",endDate);
    	model.addAttribute("sortParam",sortParam);
    	model.addAttribute("sortType",sortType);
    	String totalIncome = financesRepository.getTotalIncomeByPeriod(getUserEmail(), startDate, endDate);
    	String totalExpenditure = financesRepository.getTotalExpenditureByPeriod(getUserEmail(), startDate, endDate);
    	String totalBalance = (new BigDecimal(String.valueOf(totalIncome)).add(new BigDecimal(String.valueOf(totalExpenditure)))).toString();
    	model.addAttribute("totalIncome",totalIncome);
    	model.addAttribute("totalExpenditure",totalExpenditure);
    	model.addAttribute("totalBalance",totalBalance);
    	ArrayList<String[]> temp = new ArrayList<String[]>();
    	switch(viewScope) {
    	case "both":
    		temp = financesRepository.getGroupedDataByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	case "inc":
    		temp = financesRepository.getGroupedIncomeByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	case "exp":
    		temp = financesRepository.getGroupedExpenditureByPeriod(getUserEmail(), startDate, endDate);
    		break;
    	}
    	ArrayList<FinancesGrouped> data = new ArrayList<FinancesGrouped>();
    	for(String[] i : temp) {
    		FinancesGrouped element = new FinancesGrouped(i[0], new BigDecimal(String.valueOf(i[1])));
    		data.add(element);
    	}
    	switch(sortParam) {
    	case "amount":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (FinancesGrouped a, FinancesGrouped b) -> a.getAmount().compareTo(b.getAmount()));
    		}
    		else {
    			Collections.sort(data, (FinancesGrouped a, FinancesGrouped b) -> b.getAmount().compareTo(a.getAmount()));
    		}
    		break;
    	case "category":
    		if(sortType.equals("asc")) {
    			Collections.sort(data, (FinancesGrouped a, FinancesGrouped b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
    		}
    		else {
    			Collections.sort(data, (FinancesGrouped a, FinancesGrouped b) -> b.getCategory().compareToIgnoreCase(a.getCategory()));
    		}
    		break;
    	}
    	model.addAttribute("data", data);
    	return "view_category";
    }
    
}
