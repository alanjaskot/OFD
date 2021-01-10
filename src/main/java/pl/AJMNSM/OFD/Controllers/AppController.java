package pl.AJMNSM.OFD.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.AJMNSM.OFD.core.users.User;
import pl.AJMNSM.OFD.core.users.UserRepository;

import java.util.List;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepository;

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
    public String listUsers(Model model) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);

        return "main";
    }
}
