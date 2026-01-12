package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    Optional<InvoiceEntity> findByIdAndCustomerId(Long invoiceId, Long customerId);

    boolean existsByCustomerIdAndPaidAtIsNull(Long customerId);

    Optional<InvoiceEntity> findTopByCustomerIdAndStatusOrderByExpectedPaymentDateDesc(
            Long customerId,
            InvoiceStatus status
    );

}
