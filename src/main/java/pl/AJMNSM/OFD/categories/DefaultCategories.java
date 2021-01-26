package pl.AJMNSM.OFD.categories;

import java.util.ArrayList;

public class DefaultCategories {
	
	@SuppressWarnings("serial")
	private static ArrayList<String> defaultCategories = new ArrayList<String>() {{
		add("Zakupy");
		add("Samochód");
		add("Dom");
		add("Hobby");
		add("Praca");
	}};
	
	public static ArrayList<String> getDefaultCategories() {
		return defaultCategories;
	}
}