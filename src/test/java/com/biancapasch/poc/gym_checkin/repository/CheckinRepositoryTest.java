package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.CheckinEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CheckinRepositoryTest {

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldFindTopOpenSessionByCustomerIdOrderedByCheckinTimeDesc() {
        CustomerEntity customer = new CustomerEntity(
                null, "Bianca", "Paschoal", "bianca@example.com", 10, null, null
        );
        customer = customerRepository.save(customer);

        CheckinEntity closedOld = new CheckinEntity();
        closedOld.setCustomer(customer);
        closedOld.setCheckinTime(OffsetDateTime.now().minusHours(10));
        closedOld.setCheckoutTime(OffsetDateTime.now().minusHours(9));
        checkinRepository.save(closedOld);

        CheckinEntity openOld = new CheckinEntity();
        openOld.setCustomer(customer);
        openOld.setCheckinTime(OffsetDateTime.now().minusHours(5));
        openOld.setCheckoutTime(null);
        checkinRepository.save(openOld);

        CheckinEntity openNewest = new CheckinEntity();
        openNewest.setCustomer(customer);
        openNewest.setCheckinTime(OffsetDateTime.now().minusHours(1));
        openNewest.setCheckoutTime(null);
        checkinRepository.save(openNewest);

        Optional<CheckinEntity> result =
                checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customer.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCheckoutTime()).isNull();
        assertThat(result.get().getId()).isEqualTo(openNewest.getId());
        assertThat(result.get().getCheckinTime()).isEqualTo(openNewest.getCheckinTime());
    }

    @Test
    void shouldFindAllByCustomerIdOrderedByCheckinTimeDescWithPaging() {
        CustomerEntity customer = new CustomerEntity(
                null, "Bianca", "Paschoal", "bianca@example.com", 10, null, null
        );
        customer = customerRepository.save(customer);

        CheckinEntity c1 = new CheckinEntity();
        c1.setCustomer(customer);
        c1.setCheckinTime(OffsetDateTime.now().minusHours(3));
        c1.setCheckoutTime(OffsetDateTime.now().minusHours(2));
        checkinRepository.save(c1);

        CheckinEntity c2 = new CheckinEntity();
        c2.setCustomer(customer);
        c2.setCheckinTime(OffsetDateTime.now().minusHours(2));
        c2.setCheckoutTime(OffsetDateTime.now().minusHours(1));
        checkinRepository.save(c2);

        CheckinEntity c3 = new CheckinEntity();
        c3.setCustomer(customer);
        c3.setCheckinTime(OffsetDateTime.now().minusHours(1));
        c3.setCheckoutTime(null);
        checkinRepository.save(c3);

        Page<CheckinEntity> page0 =
                checkinRepository.findAllByCustomerIdOrderByCheckinTimeDesc(customer.getId(), PageRequest.of(0, 2));

        assertThat(page0.getTotalElements()).isEqualTo(3);
        assertThat(page0.getContent()).hasSize(2);

        assertThat(page0.getContent().get(0).getId()).isEqualTo(c3.getId());
        assertThat(page0.getContent().get(1).getId()).isEqualTo(c2.getId());

        Page<CheckinEntity> page1 =
                checkinRepository.findAllByCustomerIdOrderByCheckinTimeDesc(customer.getId(), PageRequest.of(1, 2));

        assertThat(page1.getContent()).hasSize(1);
        assertThat(page1.getContent().get(0).getId()).isEqualTo(c1.getId());
    }
}
