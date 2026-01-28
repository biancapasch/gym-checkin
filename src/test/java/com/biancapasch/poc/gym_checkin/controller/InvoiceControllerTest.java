package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import com.biancapasch.poc.gym_checkin.dto.InvoicePaymentResponseDTO;
import com.biancapasch.poc.gym_checkin.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPayInvoiceSuccessfullyAndReturn200() throws Exception {
        Long customerId = 1L;
        Long invoiceId = 1L;
        BigDecimal amount = new BigDecimal("99.90");

        InvoicePaymentResponseDTO response = new InvoicePaymentResponseDTO(
                invoiceId, customerId, amount, null, InvoiceStatus.PAID);

        when(invoiceService.payPendingInvoice(customerId, invoiceId)).thenReturn(response);

        mockMvc.perform(
                        patch("/customers/{customerId}/invoices/{invoiceId}/pay",
                                customerId,
                                invoiceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.invoiceId").value(invoiceId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.amount").value(99.90))
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(invoiceService).payPendingInvoice(customerId, invoiceId);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void shouldReturnInvoicesByCustomerIdWithPaginationAndReturn200() throws Exception {
        Long customerId = 1L;

        InvoicePaymentResponseDTO dto1 = new InvoicePaymentResponseDTO(
                10L, customerId, new BigDecimal("99.90"), null, InvoiceStatus.PENDING
        );

        InvoicePaymentResponseDTO dto2 = new InvoicePaymentResponseDTO(
                11L, customerId, new BigDecimal("99.90"), OffsetDateTime.now(), InvoiceStatus.PAID
        );

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<InvoicePaymentResponseDTO> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(invoiceService.getInvoicesByCustomerId(eq(customerId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(
                        get("/customers/{customerId}/invoices", customerId)
                                .param("page", "0")
                                .param("size", "2")
                                .param("sort", "id,desc")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content[0].invoiceId").value(10))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.content[1].invoiceId").value(11))
                .andExpect(jsonPath("$.content[1].status").value("PAID"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(invoiceService).getInvoicesByCustomerId(eq(customerId), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(0, captured.getPageNumber());
        org.junit.jupiter.api.Assertions.assertEquals(2, captured.getPageSize());
        org.junit.jupiter.api.Assertions.assertTrue(
                captured.getSort().getOrderFor("id").isDescending()
        );

        verifyNoMoreInteractions(invoiceService);
    }

}
