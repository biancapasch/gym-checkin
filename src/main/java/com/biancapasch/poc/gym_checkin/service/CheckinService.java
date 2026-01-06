package com.biancapasch.poc.gym_checkin.service;

import com.biancapasch.poc.gym_checkin.domain.entity.CheckinEntity;
import com.biancapasch.poc.gym_checkin.domain.entity.CustomerEntity;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinResponseDTO;
import com.biancapasch.poc.gym_checkin.exception.NotFoundException;
import com.biancapasch.poc.gym_checkin.repository.CheckinRepository;
import com.biancapasch.poc.gym_checkin.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private static final long MAX_SESSION_HOURS = 6L;

    private final CheckinRepository checkinRepository;
    private final CustomerRepository customerRepository;

    public CreateCheckinResponseDTO createCheckin(
            Long customerId,
            CreateCheckinRequestDTO request
    ) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        return switch (request.type()) {
            case IN -> handleIn(customer);
            case OUT -> handleOut(customer);
        };
    }

    public Page<CreateCheckinResponseDTO> findAllCheckins(Long customerId, int page, int size) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Usuário não encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<CheckinEntity> pageEntities =
                checkinRepository.findAllByCustomerIdOrderByCheckinTimeDesc(customerId, pageable);

        return pageEntities.map(this::toResponse);
    }

    public Optional<CreateCheckinResponseDTO> findOpenSession(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Usuário não encontrado");
        }

        return checkinRepository
                .findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customerId)
                .map(this::toResponse);
    }

    private CreateCheckinResponseDTO handleIn(CustomerEntity customer) {
        OffsetDateTime now = OffsetDateTime.now();

        Optional<CheckinEntity> openOpt =
                checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customer.getId());

        if (openOpt.isPresent()) {
            CheckinEntity open = openOpt.get();

            boolean isOld = open.getCheckinTime() != null &&
                    open.getCheckinTime().isBefore(now.minusHours(MAX_SESSION_HOURS));

            if (isOld) {
                open.setCheckoutTime(open.getCheckinTime().plusHours(MAX_SESSION_HOURS));
            } else {
                open.setCheckoutTime(now);
            }

            checkinRepository.save(open);
        }

        CheckinEntity newSession = new CheckinEntity();
        newSession.setCustomer(customer);
        newSession.setCheckinTime(now);
        newSession.setCheckoutTime(null);

        CheckinEntity saved = checkinRepository.save(newSession);
        return toResponse(saved);
    }

    private CreateCheckinResponseDTO handleOut(CustomerEntity customer) {
        OffsetDateTime now = OffsetDateTime.now();

        Optional<CheckinEntity> openOpt =
                checkinRepository.findTopByCustomerIdAndCheckoutTimeIsNullOrderByCheckinTimeDesc(customer.getId());

        if (openOpt.isPresent()) {
            CheckinEntity open = openOpt.get();
            open.setCheckoutTime(now);
            CheckinEntity saved = checkinRepository.save(open);
            return toResponse(saved);
        }

        CheckinEntity onlyCheckout = new CheckinEntity();
        onlyCheckout.setCustomer(customer);
        onlyCheckout.setCheckinTime(null);
        onlyCheckout.setCheckoutTime(now);

        CheckinEntity saved = checkinRepository.save(onlyCheckout);
        return toResponse(saved);
    }

    private CreateCheckinResponseDTO toResponse(CheckinEntity entity) {
        return new CreateCheckinResponseDTO(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getCheckinTime(),
                entity.getCheckoutTime()
        );
    }
}
