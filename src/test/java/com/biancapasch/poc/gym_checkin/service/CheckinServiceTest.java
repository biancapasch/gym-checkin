package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.CheckinType;
import com.biancapasch.poc.gym_checkin.domain.entity.CheckinEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.CheckinRepository;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckinServiceTest {

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CheckinService checkinService;

    @Test
    public void shouldCreateCheckinWhenCheckinTypeIsIn() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.IN);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId))
                .thenReturn(Optional.empty());

        CheckinEntity saved = new CheckinEntity();
        saved.setId(99L);
        saved.setCustomer(customerEntity);
        saved.setCheckinTime(OffsetDateTime.now());
        saved.setCheckoutTime(null);

        when(checkinRepository.save(any(CheckinEntity.class))).thenReturn(saved);

        CreateCheckinResponseDTO response = checkinService.createCheckin(customerId, request);

        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.checkinTime()).isNotNull();
        assertThat(response.checkoutTime()).isNull();
        assertThat(response.checkinId()).isEqualTo(99L);

        ArgumentCaptor<CheckinEntity> captor = ArgumentCaptor.forClass(CheckinEntity.class);
        verify(checkinRepository).save(captor.capture());

        CheckinEntity toSave = captor.getValue();
        assertThat(toSave.getCustomer().getId()).isEqualTo(1L);
        assertThat(toSave.getCheckinTime()).isNotNull();
        assertThat(toSave.getCheckoutTime()).isNull();
    }

    @Test
    public void shouldCloseOpenSessionAndCreateNewSessionWhenCheckinTypeIsIn() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.IN);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        CheckinEntity openSession = new CheckinEntity();
        openSession.setCustomer(customerEntity);
        openSession.setCheckinTime(OffsetDateTime.now().minusHours(2));
        openSession.setCheckoutTime(null);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId))
                .thenReturn(Optional.of(openSession));

        CheckinEntity closedSessionSaved = new CheckinEntity();
        closedSessionSaved.setId(10L);
        closedSessionSaved.setCustomer(customerEntity);
        closedSessionSaved.setCheckinTime(openSession.getCheckinTime());
        closedSessionSaved.setCheckoutTime(OffsetDateTime.now());

        CheckinEntity newSessionSaved = new CheckinEntity();
        newSessionSaved.setId(11L);
        newSessionSaved.setCustomer(customerEntity);
        newSessionSaved.setCheckinTime(OffsetDateTime.now());
        newSessionSaved.setCheckoutTime(null);

        when(checkinRepository.save(any(CheckinEntity.class)))
                .thenReturn(closedSessionSaved)
                .thenReturn(newSessionSaved);

        CreateCheckinResponseDTO response = checkinService.createCheckin(customerId, request);

        assertThat(response.checkinId()).isEqualTo(11L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.checkinTime()).isNotNull();
        assertThat(response.checkoutTime()).isNull();

        ArgumentCaptor<CheckinEntity> captor = ArgumentCaptor.forClass(CheckinEntity.class);
        verify(checkinRepository, times(2)).save(captor.capture());

        List<CheckinEntity> savedEntities = captor.getAllValues();
        CheckinEntity firstSave = savedEntities.get(0);
        CheckinEntity secondSave = savedEntities.get(1);

        assertThat(firstSave.getCustomer().getId()).isEqualTo(1L);
        assertThat(firstSave.getCheckoutTime()).isNotNull();

        assertThat(secondSave.getCustomer().getId()).isEqualTo(1L);
        assertThat(secondSave.getCheckoutTime()).isNull();
        assertThat(secondSave.getCheckinTime()).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenCheckinTypeIsInAndCustomerNotFound() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.IN);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                checkinService.createCheckin(customerId, request)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenCheckinTypeIsOutAndCustomerNotFound() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.OUT);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                checkinService.createCheckin(customerId, request)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldCloseOpenSessionWhenCheckinTypeIsOut() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.OUT);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        CheckinEntity openSession = new CheckinEntity();
        openSession.setCustomer(customerEntity);
        openSession.setCheckinTime(OffsetDateTime.now().minusHours(2));
        openSession.setCheckoutTime(null);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId))
                .thenReturn(Optional.of(openSession));

        CheckinEntity closedSessionSaved = new CheckinEntity();
        closedSessionSaved.setId(10L);
        closedSessionSaved.setCustomer(customerEntity);
        closedSessionSaved.setCheckinTime(openSession.getCheckinTime());
        closedSessionSaved.setCheckoutTime(OffsetDateTime.now());

        when(checkinRepository.save(any(CheckinEntity.class))).thenReturn(closedSessionSaved);

        CreateCheckinResponseDTO response = checkinService.createCheckin(customerId, request);

        ArgumentCaptor<CheckinEntity> captor = ArgumentCaptor.forClass(CheckinEntity.class);
        verify(checkinRepository, times(1)).save(captor.capture());
        CheckinEntity value = captor.getValue();

        assertThat(response.checkinId()).isEqualTo(10L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.checkoutTime()).isNotNull();

        assertThat(value.getCustomer().getId()).isEqualTo(1L);
        assertThat(value.getCheckoutTime()).isNotNull();
        assertThat(value.getCheckinTime()).isNotNull();
    }

    @Test
    public void shouldCreateCheckoutWhenCheckinTypeIsOutAndNoOpenSessionExists() {
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.OUT);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId))
                .thenReturn(Optional.empty());

        CheckinEntity checkinEntity = new CheckinEntity();
        checkinEntity.setId(10L);
        checkinEntity.setCustomer(customerEntity);
        checkinEntity.setCheckinTime(null);
        checkinEntity.setCheckoutTime(OffsetDateTime.now());

        when(checkinRepository.save(any(CheckinEntity.class))).thenReturn(checkinEntity);

        CreateCheckinResponseDTO response = checkinService.createCheckin(customerId, request);

        ArgumentCaptor<CheckinEntity> captor = ArgumentCaptor.forClass(CheckinEntity.class);
        verify(checkinRepository, times(1)).save(captor.capture());
        CheckinEntity value = captor.getValue();

        assertThat(response.checkinId()).isEqualTo(10L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.checkoutTime()).isNotNull();
        assertThat(response.checkinTime()).isNull();

        assertThat(value.getCustomer().getId()).isEqualTo(1L);
        assertThat(value.getCheckoutTime()).isNotNull();
        assertThat(value.getCheckinTime()).isNull();
    }

    @Test
    public void shouldCloseOldOpenSessionAndCreateNewSessionWhenCheckinTypeIsIn() {
        OffsetDateTime now = OffsetDateTime.now();
        Long customerId = 1L;
        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.IN);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        CheckinEntity openSession = new CheckinEntity();
        openSession.setCustomer(customerEntity);
        openSession.setCheckinTime(now.minusHours(7));
        openSession.setCheckoutTime(null);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId))
                .thenReturn(Optional.of(openSession));

        CheckinEntity closedSessionSaved = new CheckinEntity();
        closedSessionSaved.setId(10L);
        closedSessionSaved.setCustomer(customerEntity);
        closedSessionSaved.setCheckinTime(openSession.getCheckinTime());
        closedSessionSaved.setCheckoutTime(openSession.getCheckinTime().plusHours(6));

        CheckinEntity newSessionSaved = new CheckinEntity();
        newSessionSaved.setId(11L);
        newSessionSaved.setCustomer(customerEntity);
        newSessionSaved.setCheckinTime(now);
        newSessionSaved.setCheckoutTime(null);

        when(checkinRepository.save(any(CheckinEntity.class)))
                .thenReturn(closedSessionSaved)
                .thenReturn(newSessionSaved);

        CreateCheckinResponseDTO response = checkinService.createCheckin(customerId, request);

        assertThat(response.checkinId()).isEqualTo(11L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.checkinTime()).isNotNull();
        assertThat(response.checkoutTime()).isNull();

        ArgumentCaptor<CheckinEntity> captor = ArgumentCaptor.forClass(CheckinEntity.class);
        verify(checkinRepository, times(2)).save(captor.capture());

        var savedEntities = captor.getAllValues();
        CheckinEntity firstSave = savedEntities.get(0);
        CheckinEntity secondSave = savedEntities.get(1);

        assertThat(firstSave.getCustomer().getId()).isEqualTo(1L);
        assertThat(firstSave.getCheckinTime()).isEqualTo(openSession.getCheckinTime());
        assertThat(firstSave.getCheckoutTime()).isEqualTo(openSession.getCheckinTime().plusHours(6));

        assertThat(secondSave.getCustomer().getId()).isEqualTo(1L);
        assertThat(secondSave.getCheckinTime()).isNotNull();
        assertThat(secondSave.getCheckoutTime()).isNull();
    }

}
