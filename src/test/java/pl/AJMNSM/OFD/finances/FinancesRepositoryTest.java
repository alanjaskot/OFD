package pl.AJMNSM.OFD.finances;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import pl.AJMNSM.OFD.core.users.User;
import pl.AJMNSM.OFD.core.users.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class FinancesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FinancesRepository financesRepository;

    @Test
    public void testInsertIncome() {
        Finances finances = new Finances();
        finances.setEmail("user@aa.a");
        finances.setDescription("wyplata");
        finances.setCategory("praca");
        finances.setAmount(BigDecimal.valueOf(3001));
        finances.setDate(LocalDate.ofEpochDay(2020-12-10));

        Finances savedFinances = financesRepository.save(finances);
        Finances existFincaces = entityManager.find(Finances.class, savedFinances.getId());

        assertThat(finances.getCategory()).isEqualTo(existFincaces.getCategory());

    }
}