package pl.AJMNSM.OFD.finances;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FinancesRepository extends JpaRepository<Finances, Long> {
	@Query(value = "select coalesce((select sum(amount) from finances where amount >= 0 and email = ?1 and date between date_sub(curdate(), interval ?2 day) and curdate()), 0)", nativeQuery = true)
	String getIncomeByDays(String email, Integer days);
	
	@Query(value = "select coalesce((select sum(amount) from finances where amount < 0 and email = ?1 and date between date_sub(curdate(), interval ?2 day) and curdate()), 0)", nativeQuery = true)
	String getExpenditureByDays(String email, Integer days);
	
	@Query(value = "select * from finances where amount <= ?1 and email = ?2 and date between curdate() and date_add(curdate(), interval ?3 day)", nativeQuery = true)
	ArrayList<Finances> getCriticalExpenditure(BigDecimal amount, String email, Integer days);
	
	@Query(value = "select * from finances where email = ?1 and date between ?2 and ?3", nativeQuery = true)
	ArrayList<Finances> getDataByPeriod(String email, String start, String end);
	
	@Query(value = "select * from finances where amount >= 0 and email = ?1 and date between ?2 and ?3", nativeQuery = true)
	ArrayList<Finances> getIncomeByPeriod(String email, String start, String end);
	
	@Query(value = "select * from finances where amount < 0 and email = ?1 and date between ?2 and ?3", nativeQuery = true)
	ArrayList<Finances> getExpenditureByPeriod(String email, String start, String end);
	
	@Query(value = "select coalesce((select sum(amount) from finances where amount >= 0 and email = ?1 and date between ?2 and ?3), 0)", nativeQuery = true)
	String getTotalIncomeByPeriod(String email, String start, String end);
	
	@Query(value = "select coalesce((select sum(amount) from finances where amount < 0 and email = ?1 and date between ?2 and ?3), 0)", nativeQuery = true)
	String getTotalExpenditureByPeriod(String email, String start, String end);
	
	@Query(value = "select category,sum(amount) from finances where email = ?1 and date between ?2 and ?3 group by category;", nativeQuery = true)
	ArrayList<String[]> getGroupedDataByPeriod (String email, String start, String end);
	
	@Query(value = "select category,sum(amount) from finances where amount >= 0 and email = ?1 and date between ?2 and ?3 group by category;", nativeQuery = true)
	ArrayList<String[]> getGroupedIncomeByPeriod (String email, String start, String end);
	
	@Query(value = "select category,sum(amount) from finances where amount < 0 and email = ?1 and date between ?2 and ?3 group by category;", nativeQuery = true)
	ArrayList<String[]> getGroupedExpenditureByPeriod (String email, String start, String end);
}
