package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByPaymentDay(int paymentDay);
}
