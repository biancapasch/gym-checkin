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

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceService invoiceService;

    @Transactional
    public CreateCustomerResponseDTO create(CreateCustomerRequestDTO request, OffsetDateTime expectedPaymentDate) {
        CustomerEntity entity = toEntity(request);

        CustomerEntity saved = customerRepository.save(entity);

        InvoiceEntity invoiceEntity = invoiceService.create(saved, null);

        return toResponse(saved);
    }

    public CustomerEntity findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
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
