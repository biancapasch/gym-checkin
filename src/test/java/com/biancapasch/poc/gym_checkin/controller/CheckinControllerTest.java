package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.CheckinType;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CheckinService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CheckinController.class)
public class CheckinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckinService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCheckinAndReturn201() throws Exception {
        Long customerId = 1L;

        CreateCheckinRequestDTO request = new CreateCheckinRequestDTO(CheckinType.IN);

        CreateCheckinResponseDTO response = new CreateCheckinResponseDTO(10L, 1L, OffsetDateTime.now(), null);

        when(service.createCheckin(eq(customerId), any(CreateCheckinRequestDTO.class))).thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/customers/{customerId}/checkins", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkinId").value(10))
                .andExpect(jsonPath("$.customerId").value(1));

        verify(service).createCheckin(eq(customerId), any(CreateCheckinRequestDTO.class));
    }

    @Test
    void shouldReturn200WhenOpenSessionExists() throws Exception {
        Long customerId = 1L;

        CreateCheckinResponseDTO openDto = new CreateCheckinResponseDTO(
                10L,
                customerId,
                OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                null
        );

        when(service.findOpenSession(customerId)).thenReturn(Optional.of(openDto));

        mockMvc.perform(get("/customers/{customerId}/checkins/open-session", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkinId").value(10))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.checkoutTime").doesNotExist()); // null geralmente não vem como campo dependendo do Jackson config

        verify(service).findOpenSession(eq(customerId));
    }

    @Test
    void shouldReturn204WhenNoOpenSessionExists() throws Exception {
        Long customerId = 1L;

        when(service.findOpenSession(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/{customerId}/checkins/open-session", customerId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).findOpenSession(eq(customerId));
    }

    @Test
    void shouldListCheckinsWithPaging() throws Exception {
        Long customerId = 1L;

        CreateCheckinResponseDTO dto1 = new CreateCheckinResponseDTO(
                11L,
                customerId,
                OffsetDateTime.parse("2026-01-02T10:00:00Z"),
                null
        );

        CreateCheckinResponseDTO dto2 = new CreateCheckinResponseDTO(
                10L,
                customerId,
                OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                OffsetDateTime.parse("2026-01-01T12:00:00Z")
        );

        Page<CreateCheckinResponseDTO> page = new PageImpl<>(
                List.of(dto1, dto2)
        );

        when(service.findAllCheckins(customerId, 0, 10)).thenReturn(page);

        mockMvc.perform(get("/customers/{customerId}/checkins", customerId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].checkinId").value(11))
                .andExpect(jsonPath("$.content[0].customerId").value(1))
                .andExpect(jsonPath("$.content[1].checkinId").value(10))
                .andExpect(jsonPath("$.content[1].customerId").value(1));

        verify(service).findAllCheckins(eq(customerId), eq(0), eq(10));
    }
}
