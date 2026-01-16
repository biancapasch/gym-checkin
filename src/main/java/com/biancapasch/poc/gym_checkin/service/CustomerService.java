package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceEntity;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceService invoiceService;

    @Transactional
    public CreateCustomerResponseDTO create(CreateCustomerRequestDTO request) {
        CustomerEntity entity = toEntity(request);

        CustomerEntity saved = customerRepository.save(entity);

        invoiceService.create(saved, request.expectedPaymentDate());

        return toResponse(saved);
    }

    public CustomerEntity findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    }

    private CustomerEntity toEntity(CreateCustomerRequestDTO request) {
        int dayOfMonth = request.expectedPaymentDate().getDayOfMonth();
        return new CustomerEntity(
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                dayOfMonth,
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
