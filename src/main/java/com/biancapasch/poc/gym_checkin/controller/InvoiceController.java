package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.InvoicePaymentResponseDTO;
import com.biancapasch.poc.gym_checkin.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/{customerId}/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PatchMapping("/{invoiceId}/pay")
    public ResponseEntity<InvoicePaymentResponseDTO> pay(@PathVariable Long customerId, @PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.payPendingInvoice(customerId, invoiceId));
    }


}
