package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import com.biancapasch.poc.gym_checkin.dto.InvoicePaymentResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.DuplicateInvoiceException;
import com.biancapasch.poc.gym_checkin.exception.InvoiceAlreadyPaidException;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;


@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private static final BigDecimal DEFAULT_MONTHLY_PRICE = new BigDecimal("99.90");

    @Transactional
    public InvoicePaymentResponseDTO payPendingInvoice(Long customerId, Long invoiceId) {

        InvoiceEntity pendingInvoice = invoiceRepository
                .findByIdAndCustomerId(invoiceId, customerId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (pendingInvoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid");
        }

        pendingInvoice.setStatus(InvoiceStatus.PAID);
        pendingInvoice.setPaidAt(OffsetDateTime.now());
        invoiceRepository.save(pendingInvoice);

        return toDto(pendingInvoice);
    }

    @Transactional
    public InvoiceEntity create(CustomerEntity customerEntity, LocalDate expectedPaymentDate) {
        if (invoiceRepository.existsByCustomerIdAndExpectedPaymentDate(customerEntity.getId(), expectedPaymentDate)) {
            throw new DuplicateInvoiceException("Invoice already generated for this date");
        }

        InvoiceEntity invoiceEntity = new InvoiceEntity();

        invoiceEntity.setCustomer(customerEntity);
        invoiceEntity.setStatus(InvoiceStatus.PENDING);
        invoiceEntity.setAmount(DEFAULT_MONTHLY_PRICE);
        invoiceEntity.setExpectedPaymentDate(
                expectedPaymentDate == null ? LocalDate.now() : expectedPaymentDate
        );

        return invoiceRepository.save(invoiceEntity);
    }

    public Page<InvoicePaymentResponseDTO> getInvoicesByCustomerId(Long customerId, Pageable pageable) {
        return invoiceRepository
                .findAllByCustomerId(customerId, pageable)
                .map(this::toDto);
    }

    private InvoicePaymentResponseDTO toDto(InvoiceEntity entity) {
        return new InvoicePaymentResponseDTO(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getAmount(),
                entity.getPaidAt(),
                entity.getStatus()
        );
    }
}


