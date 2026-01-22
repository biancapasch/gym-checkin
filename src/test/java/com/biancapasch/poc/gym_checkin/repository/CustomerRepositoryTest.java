package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void shouldFindCustomerByPaymentDaySuccessfully() {
        CustomerEntity customerEntity = new CustomerEntity(
                null, "bianca", "paschoal", "bianca@example.com", 10, null, null);

        CustomerEntity customerEntity2 = new CustomerEntity(
                null, "ze", "paschoal", "ze@example.com", 10, null, null);

        CustomerEntity customerEntity3 = new CustomerEntity(
                null, "romeo", "paschoal", "romeo@example.com", 20, null, null);

        customerRepository.saveAll(List.of(customerEntity, customerEntity2, customerEntity3));

        int paymentDay = 10;

        List<CustomerEntity> byPaymentDay = customerRepository.findByPaymentDay(paymentDay);

        assertThat(byPaymentDay.size()).isEqualTo(2);
        assertThat(byPaymentDay).allMatch(c -> c.getPaymentDay() == 10);
    }
}
