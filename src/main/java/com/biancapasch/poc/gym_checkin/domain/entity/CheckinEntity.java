package com.biancapasch.poc.gym_checkin.domain.entity;

import com.biancapasch.poc.gym_checkin.service.CustomerCheckinStatus;
import com.biancapasch.poc.gym_checkin.service.SessionCheckinStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    private SessionCheckinStatus status;

    private OffsetDateTime checkinTime;

    private OffsetDateTime checkoutTime;
}
