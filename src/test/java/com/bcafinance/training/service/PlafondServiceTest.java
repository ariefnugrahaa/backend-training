package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.PlafondRequest;
import com.bcafinance.training.dto.response.PlafondResponse;
import com.bcafinance.training.entity.Plafond;
import com.bcafinance.training.repository.PlafondRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlafondServiceTest {

    @Mock
    private PlafondRepository plafondRepository;

    @InjectMocks
    private PlafondService plafondService;

    @Test
    void testGetAll_Success() {
        // Arrange
        Plafond plafond = new Plafond(1L, "Plafond 1", "Desc", "A", BigDecimal.TEN, BigDecimal.ONE);
        when(plafondRepository.findAll()).thenReturn(List.of(plafond));

        // Act
        List<PlafondResponse> responses = plafondService.getAll();

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Plafond 1", responses.get(0).getName());
    }

    @Test
    void testCreate_Success() {
        // Arrange
        PlafondRequest request = new PlafondRequest();
        request.setName("New Plafond");
        request.setDescription("New Desc");
        request.setType("B");
        request.setMaxAmount(BigDecimal.valueOf(100));
        request.setInterestRate(BigDecimal.valueOf(5));

        Plafond saved = new Plafond(1L, "New Plafond", "New Desc", "B", BigDecimal.valueOf(100), BigDecimal.valueOf(5));
        when(plafondRepository.save(any(Plafond.class))).thenReturn(saved);

        // Act
        PlafondResponse response = plafondService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("New Plafond", response.getName());
        assertEquals(BigDecimal.valueOf(100), response.getMaxAmount());
    }
}
