package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import com.biancapasch.poc.gym_checkin.dto.InvoicePaymentResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.DuplicateInvoiceException;
import com.biancapasch.poc.gym_checkin.exception.InvoiceAlreadyPaidException;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    //pay pending invoice
    @Test
    void shouldPayPendingInvoiceSuccessfully() {
        Long customerId = 10L;
        Long invoiceId = 20L;

        BigDecimal amount = new BigDecimal("99.90");

        CustomerEntity customer = new CustomerEntity(
                customerId, "bianca", "paschoal", "bianca@example.com", 10, null, null
        );

        InvoiceEntity pendingInvoice = new InvoiceEntity();
        pendingInvoice.setId(invoiceId);
        pendingInvoice.setCustomer(customer);
        pendingInvoice.setAmount(amount);
        pendingInvoice.setStatus(InvoiceStatus.PENDING);
        pendingInvoice.setPaidAt(null);
        pendingInvoice.setExpectedPaymentDate(null);

        when(invoiceRepository.findByIdAndCustomerId(invoiceId, customerId))
                .thenReturn(Optional.of(pendingInvoice));

        assertEquals(InvoiceStatus.PENDING, pendingInvoice.getStatus());

        InvoicePaymentResponseDTO result = invoiceService.payPendingInvoice(customerId, invoiceId);

        assertEquals(InvoiceStatus.PAID, pendingInvoice.getStatus());
        assertNotNull(pendingInvoice.getPaidAt());

        assertNotNull(result);
        assertEquals(invoiceId, result.invoiceId());
        assertEquals(InvoiceStatus.PAID, result.status());
        assertNotNull(result.paidAt());

        verify(invoiceRepository, times(1)).findByIdAndCustomerId(invoiceId, customerId);
        verifyNoMoreInteractions(invoiceRepository);
    }

    // throw NotFoundException
    @Test
    void shouldThrowNotFoundExceptionWhenInvoiceNotFound() {
        Long invoiceId = 10L;
        Long customerId = 1L;

        when(invoiceRepository.findByIdAndCustomerId(invoiceId, customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.payPendingInvoice(customerId, invoiceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Invoice not found");

        verify(invoiceRepository).findByIdAndCustomerId(invoiceId, customerId);
        verifyNoMoreInteractions(invoiceRepository);
    }

    //throw InvoiceAlreadyPaidException
    @Test
    void shouldThrowAlreadyPaidExceptionWhenInvoiceStatusIsPaid() {
        Long invoiceId = 10L;
        Long customerId = 1L;

        CustomerEntity customer = new CustomerEntity(
                customerId, "bianca", "paschoal", "bianca@example.com", 10, null, null);

        InvoiceEntity paidInvoice = new InvoiceEntity();
        paidInvoice.setStatus(InvoiceStatus.PAID);
        paidInvoice.setId(invoiceId);
        paidInvoice.setCustomer(customer);

        when(invoiceRepository.findByIdAndCustomerId(invoiceId, customerId))
                .thenReturn(Optional.of(paidInvoice));

        assertThatThrownBy(() -> invoiceService.payPendingInvoice(customerId, invoiceId))
                .isInstanceOf(InvoiceAlreadyPaidException.class)
                .hasMessage("Invoice is already paid");

        verify(invoiceRepository).findByIdAndCustomerId(invoiceId, customerId);
        verifyNoMoreInteractions(invoiceRepository);
    }

    //create invoice
    @Test
    void shouldCreateInvoiceSuccessfully() {
        Long customerId = 10L;
        LocalDate expectedDate = LocalDate.now().plusDays(20);

        CustomerEntity customer = new CustomerEntity(
                customerId, "bianca", "paschoal",
                "bianca@example.com", 10, null, null);

        when(invoiceRepository.existsByCustomerIdAndExpectedPaymentDate(customerId, expectedDate))
                .thenReturn(false);

        when(invoiceRepository.save(any(InvoiceEntity.class)))
                .thenAnswer(invocation -> {
                    InvoiceEntity entity = invocation.getArgument(0);
                    entity.setId(20L);
                    return entity;
                });

        InvoiceEntity newInvoice = invoiceService.create(customer, expectedDate);

        assertNotNull(newInvoice);
        assertEquals(20L, newInvoice.getId());
        assertEquals(customer, newInvoice.getCustomer());
        assertEquals(expectedDate, newInvoice.getExpectedPaymentDate());
        assertEquals(InvoiceStatus.PENDING, newInvoice.getStatus());
        assertEquals(new BigDecimal("99.90"), newInvoice.getAmount());

        ArgumentCaptor<InvoiceEntity> invoiceCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
        verify(invoiceRepository, times(1)).save(invoiceCaptor.capture());

        InvoiceEntity saved = invoiceCaptor.getValue();
        assertEquals(customer, saved.getCustomer());
        assertEquals(expectedDate, saved.getExpectedPaymentDate());
        assertEquals(InvoiceStatus.PENDING, saved.getStatus());
        assertEquals(new BigDecimal("99.90"), saved.getAmount());

        verify(invoiceRepository, times(1))
                .existsByCustomerIdAndExpectedPaymentDate(customerId, expectedDate);

        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    void shouldThrowDuplicateInvoiceExceptionWhenInvoiceAlreadyExistsForDate() {
        Long customerId = 10L;
        LocalDate expectedDate = LocalDate.now().plusDays(20);

        CustomerEntity customer = new CustomerEntity(
                customerId, "bianca", "paschoal",
                "bianca@example.com", 10, null, null);

        when(invoiceRepository.existsByCustomerIdAndExpectedPaymentDate(customerId, expectedDate))
                .thenReturn(true);

        assertThatThrownBy(() -> invoiceService.create(customer, expectedDate))
                .isInstanceOf(DuplicateInvoiceException.class)
                .hasMessage("Invoice already generated for this date");

        verify(invoiceRepository).existsByCustomerIdAndExpectedPaymentDate(customerId, expectedDate);
        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    void shouldReturnInvoicesByCustomerIdMappedToDto() {
        Long customerId = 10L;
        Pageable pageable = PageRequest.of(0, 2);

        CustomerEntity customer = new CustomerEntity(
                customerId, "bianca", "paschoal", "bianca@example.com", 10, null, null
        );

        InvoiceEntity invoice1 = new InvoiceEntity();
        invoice1.setId(1L);
        invoice1.setCustomer(customer);
        invoice1.setAmount(new BigDecimal("99.90"));
        invoice1.setStatus(InvoiceStatus.PENDING);
        invoice1.setPaidAt(null);

        InvoiceEntity invoice2 = new InvoiceEntity();
        invoice2.setId(2L);
        invoice2.setCustomer(customer);
        invoice2.setAmount(new BigDecimal("99.90"));
        invoice2.setStatus(InvoiceStatus.PAID);
        invoice2.setPaidAt(OffsetDateTime.now());

        Page<InvoiceEntity> pageFromRepo = new PageImpl<>(
                List.of(invoice1, invoice2),
                pageable,
                2
        );

        when(invoiceRepository.findAllByCustomerId(customerId, pageable))
                .thenReturn(pageFromRepo);

        Page<InvoicePaymentResponseDTO> result =
                invoiceService.getInvoicesByCustomerId(customerId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        InvoicePaymentResponseDTO dto1 = result.getContent().get(0);
        assertThat(dto1.invoiceId()).isEqualTo(1L);
        assertThat(dto1.customerId()).isEqualTo(customerId);
        assertThat(dto1.amount()).isEqualByComparingTo("99.90");
        assertThat(dto1.status()).isEqualTo(InvoiceStatus.PENDING);
        assertThat(dto1.paidAt()).isNull();

        InvoicePaymentResponseDTO dto2 = result.getContent().get(1);
        assertThat(dto2.invoiceId()).isEqualTo(2L);
        assertThat(dto2.customerId()).isEqualTo(customerId);
        assertThat(dto2.amount()).isEqualByComparingTo("99.90");
        assertThat(dto2.status()).isEqualTo(InvoiceStatus.PAID);
        assertThat(dto2.paidAt()).isNotNull();

        verify(invoiceRepository, times(1)).findAllByCustomerId(customerId, pageable);
        verifyNoMoreInteractions(invoiceRepository);
    }

}
