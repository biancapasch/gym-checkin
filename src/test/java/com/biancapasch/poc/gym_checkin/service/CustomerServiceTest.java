package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void shouldCreateCustomerAndGenerateInvoice() {
        LocalDate expectedPaymentDate = LocalDate.of(2026, 2, 10);

        CreateCustomerRequestDTO request = new CreateCustomerRequestDTO(
                "Bianca",
                "Paschoal",
                expectedPaymentDate,
                "bianca@example.com"
        );

        when(customerRepository.save(any(CustomerEntity.class)))
                .thenAnswer(invocation -> {
                    CustomerEntity entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });

        CreateCustomerResponseDTO response = customerService.create(request);

        ArgumentCaptor<CustomerEntity> customerCaptor = ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).save(customerCaptor.capture());
        CustomerEntity saved = customerCaptor.getValue();

        assertThat(saved.getPaymentDay()).isEqualTo(10);
        assertThat(saved.getFirstName()).isEqualTo("Bianca");
        assertThat(saved.getLastName()).isEqualTo("Paschoal");
        assertThat(saved.getEmail()).isEqualTo("bianca@example.com");

        verify(invoiceService).create(saved, expectedPaymentDate);

        assertThat(response.firstName()).isEqualTo("Bianca");
        assertThat(response.lastName()).isEqualTo("Paschoal");
        assertThat(response.email()).isEqualTo("bianca@example.com");
    }

    @Test
    public void shouldFindCustomerById() {
        Long customerId = 1L;
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));

        assertThat(customerService.findById(customerId)).isEqualTo(customerEntity);
    }

    @Test
    public void shouldThrowExceptionWhenFindCustomerByInvalidId() {
        Long customerId = 1L;
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(customerId))
                .isInstanceOf(NotFoundException.class);
    }



}
