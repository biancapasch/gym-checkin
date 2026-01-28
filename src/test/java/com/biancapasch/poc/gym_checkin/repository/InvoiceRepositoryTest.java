package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldFindByIdAndCustomerId() {
        CustomerEntity customer1 = customerRepository.save(new CustomerEntity(
                null, "Bianca", "Paschoal", "bianca1@example.com", 10, null, null
        ));

        CustomerEntity customer2 = customerRepository.save(new CustomerEntity(
                null, "Ze", "Paschoal", "bianca2@example.com", 10, null, null
        ));

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setCustomer(customer1);
        invoice.setAmount(new BigDecimal("99.90"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setExpectedPaymentDate(LocalDate.now().plusDays(10));

        invoice = invoiceRepository.save(invoice);

        Optional<InvoiceEntity> found =
                invoiceRepository.findByIdAndCustomerId(invoice.getId(), customer1.getId());

        Optional<InvoiceEntity> notFoundWrongCustomer =
                invoiceRepository.findByIdAndCustomerId(invoice.getId(), customer2.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(invoice.getId());
        assertThat(found.get().getCustomer().getId()).isEqualTo(customer1.getId());

        assertThat(notFoundWrongCustomer).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenInvoiceExistsByCustomerIdAndExpectedPaymentDate() {
        CustomerEntity customer = customerRepository.save(new CustomerEntity(
                null, "Bianca", "Paschoal", "bianca3@example.com", 10, null, null
        ));

        LocalDate expectedDate = LocalDate.now().plusDays(20);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setCustomer(customer);
        invoice.setAmount(new BigDecimal("99.90"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setExpectedPaymentDate(expectedDate);

        invoiceRepository.save(invoice);

        boolean exists = invoiceRepository.existsByCustomerIdAndExpectedPaymentDate(customer.getId(), expectedDate);
        boolean notExistsOtherDate = invoiceRepository.existsByCustomerIdAndExpectedPaymentDate(customer.getId(), expectedDate.plusDays(1));

        assertThat(exists).isTrue();
        assertThat(notExistsOtherDate).isFalse();
    }

    @Test
    void shouldFindAllByCustomerIdWithPagination() {
        CustomerEntity customer1 = customerRepository.save(new CustomerEntity(
                null, "Bianca", "Paschoal", "bianca4@example.com", 10, null, null
        ));
        CustomerEntity customer2 = customerRepository.save(new CustomerEntity(
                null, "Romeo", "Paschoal", "bianca5@example.com", 20, null, null
        ));

        invoiceRepository.save(buildInvoice(customer1, LocalDate.now().plusDays(1), InvoiceStatus.PENDING));
        invoiceRepository.save(buildInvoice(customer1, LocalDate.now().plusDays(2), InvoiceStatus.PAID));
        invoiceRepository.save(buildInvoice(customer1, LocalDate.now().plusDays(3), InvoiceStatus.PENDING));

        invoiceRepository.save(buildInvoice(customer2, LocalDate.now().plusDays(1), InvoiceStatus.PENDING));

        Pageable pageable = PageRequest.of(0, 2);

        Page<InvoiceEntity> page = invoiceRepository.findAllByCustomerId(customer1.getId(), pageable);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent())
                .allMatch(inv -> inv.getCustomer().getId().equals(customer1.getId()));
    }

    private InvoiceEntity buildInvoice(CustomerEntity customer, LocalDate expectedDate, InvoiceStatus status) {
        InvoiceEntity inv = new InvoiceEntity();
        inv.setCustomer(customer);
        inv.setAmount(new BigDecimal("99.90"));
        inv.setStatus(status);
        inv.setExpectedPaymentDate(expectedDate);
        return inv;
    }
}
