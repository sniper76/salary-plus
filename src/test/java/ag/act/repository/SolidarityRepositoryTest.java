package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.Solidarity;
import ag.act.model.Status;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
@SpringDataJpaTest
public class SolidarityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SolidarityRepository solidarityRepository;

    @Test
    public void shouldFindByStockCode() {
        // Given
        final String stockCode1 = someStockCode();
        final String stockCode2 = someStockCode();
        final String nonexistentStockCode = someStockCode();

        final Solidarity solidarity1 = new Solidarity();
        solidarity1.setStatus(Status.ACTIVE);
        solidarity1.setStockCode(stockCode1);
        entityManager.persist(solidarity1);

        final Solidarity solidarity2 = new Solidarity();
        solidarity2.setStockCode(stockCode2);
        solidarity2.setStatus(Status.ACTIVE);
        entityManager.persist(solidarity2);

        entityManager.flush();

        // When
        final Optional<Solidarity> optionalSolidarity1 = solidarityRepository.findByStockCode(stockCode1);
        final Optional<Solidarity> optionalSolidarity2 = solidarityRepository.findByStockCode(stockCode2);
        final Optional<Solidarity> optionalSolidarity3 = solidarityRepository.findByStockCode(nonexistentStockCode);

        // Then
        assertThat(optionalSolidarity1.isPresent(), is(true));
        assertThat(optionalSolidarity1.get().getStockCode(), is(stockCode1));

        assertThat(optionalSolidarity2.isPresent(), is(true));
        assertThat(optionalSolidarity2.get().getStockCode(), is(stockCode2));

        assertThat(optionalSolidarity3.isEmpty(), is(true));
    }
}

