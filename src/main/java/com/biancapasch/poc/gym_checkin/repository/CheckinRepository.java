package com.biancapasch.poc.gym_checkin.repository;

import com.biancapasch.poc.gym_checkin.domain.entity.CheckinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckinRepository extends JpaRepository<CheckinEntity, Long> {

    Optional<CheckinEntity> findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(Long topCustomerId);

}

