package pl.AJMNSM.OFD.categories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
	
	@Query(value = "select category from categories where email = ?1", nativeQuery = true)
	ArrayList<String> getCategoriesByEmail(String email);
	
	@Query(value = "select * from categories where email = ?1", nativeQuery = true)
	ArrayList<Categories> getUserCategoriesByEmailFull(String email);
}
