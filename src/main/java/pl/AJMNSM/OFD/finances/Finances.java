package pl.AJMNSM.OFD.finances;

import pl.AJMNSM.OFD.core.users.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "finances")
public class Finances {

    @Id
    private Long id;

    @Column(nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 45)
    private Double money;

    @Column(nullable = false, length = 45)
    private Date data;

    @Column(nullable = false, length = 45)
    private String category;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {
        User user = new User();
        this.email = user.getEmail();
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Double getMoney() {return money;}
    public void setMoney(Double money) {this.money = money;}

    public Date getData() {return data;}
    public void setData(Date data) {this.data = data;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
}
