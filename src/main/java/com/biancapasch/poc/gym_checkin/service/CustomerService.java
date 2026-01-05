package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CheckinEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.CheckinRepository;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CheckinRepository checkinRepository;

    private static final long MAX_SESSION_HOURS = 6L;

    public CreateCustomerResponseDTO create(@Valid CreateCustomerRequestDTO request) {
        CustomerEntity entity = toEntity(request);

        customerRepository.save(entity);

        return toResponse(entity);
    }

    private CustomerEntity toEntity(CreateCustomerRequestDTO request) {
        return new CustomerEntity(
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                null,
                null
        );
    }

    private CreateCustomerResponseDTO toResponse(CustomerEntity entity) {
        return new CreateCustomerResponseDTO(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail()
        );
    }
}
