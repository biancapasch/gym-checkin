package com.biancapasch.poc.gym_checkin.service.job;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import com.biancapasch.poc.gym_checkin.repository.InvoiceRepository;
import com.biancapasch.poc.gym_checkin.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingJob {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceService invoiceService;

    // TODO: depois trocar pra cron 1x por dia
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void generateNewInvoicesForExpiredPayments() {
        LocalDate today = LocalDate.now();

        List<CustomerEntity> customers = customerRepository.findAll();

        for (CustomerEntity customer : customers) {
            Long customerId = customer.getId();


        Optional<InvoiceEntity> lastPaidOpt = invoiceRepository.findTopByCustomerIdAndStatusOrderByExpectedPaymentDateDesc(
                customerId,
                InvoiceStatus.PAID
        );

        if (lastPaidOpt.isEmpty()) {
            continue;
        }

        InvoiceEntity lastPaid = lastPaidOpt.get();


        LocalDate lastDueDate = lastPaid.getExpectedPaymentDate().toLocalDate();
        LocalDate nextDueDate = lastDueDate.plusMonths(1);

        LocalDate tenDaysBefore = nextDueDate.minusDays(10);

        if (!today.equals(tenDaysBefore)) {
            continue;
        }

        boolean hasPending = invoiceRepository.existsByCustomerIdAndPaidAtIsNull(customerId);

        if (hasPending) {
            continue;
        }

            OffsetDateTime nextExpected =
                    nextDueDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toOffsetDateTime();

            invoiceService.create(customer, nextExpected);
        }

    }

}
