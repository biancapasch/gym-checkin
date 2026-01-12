package com.biancapasch.poc.gym_checkin.exception;

public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException(String message) {
        super(message);
    }
}
