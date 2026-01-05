package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.service.CheckinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
public class CheckinControllerTest {

    private MockMvc mockMvc;
    private CheckinService checkinService;

    @BeforeEach
    void setUp() {
        checkinService = Mockito.mock(CheckinService.class);
}}


