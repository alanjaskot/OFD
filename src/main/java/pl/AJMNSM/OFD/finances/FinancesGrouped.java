package pl.AJMNSM.OFD.finances;

import java.math.BigDecimal;

public class FinancesGrouped {

	private String category;
	private BigDecimal amount;
	
	public FinancesGrouped(String category, BigDecimal amount) {
	    this.setCategory(category);
	    this.setAmount(amount);
	  }
	
	public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    
    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}
}
