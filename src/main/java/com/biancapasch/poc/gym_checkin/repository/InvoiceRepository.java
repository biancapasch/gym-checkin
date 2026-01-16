package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    Optional<InvoiceEntity> findByIdAndCustomerId(Long invoiceId, Long customerId);

    boolean existsByCustomerIdAndExpectedPaymentDate(Long customerId, LocalDate expectedPaymentDate);

    Page<InvoiceEntity> findAllByCustomerId(Long customerId, Pageable pageable);

}
