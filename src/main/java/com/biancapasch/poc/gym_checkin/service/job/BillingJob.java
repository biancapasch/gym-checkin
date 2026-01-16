package com.biancapasch.poc.gym_checkin.service.job;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.exception.DuplicateInvoiceException;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import com.biancapasch.poc.gym_checkin.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingJob {

    private final CustomerRepository customerRepository;
    private final InvoiceService invoiceService;

    @Scheduled(cron = "0 0 0,12 * * ?")
    @Transactional

    public void generateInvoice() {
        LocalDate today = LocalDate.now();
        LocalDate expectedPaymentDay = today.plusDays(10);
        int dayOfMonth = expectedPaymentDay.getDayOfMonth();

        List<CustomerEntity> byPaymentDay = customerRepository.findByPaymentDay(dayOfMonth);

        for(CustomerEntity customerEntity : byPaymentDay) {
            try {
                invoiceService.create(customerEntity, expectedPaymentDay);
            } catch (DuplicateInvoiceException ex) {
                log.error("Exception while creating invoice for customerEntity {}", customerEntity, ex);
                continue;
            }

            log.info("Invoice for customerEntity {} has been created", customerEntity);
        }
    }
}

