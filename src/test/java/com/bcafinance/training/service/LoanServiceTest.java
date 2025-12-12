package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.LoanRequest;
import com.bcafinance.training.dto.response.LoanResponse;
import com.bcafinance.training.entity.*;
import com.bcafinance.training.repository.LoanApplicationRepository;
import com.bcafinance.training.repository.PlafondRepository;
import com.bcafinance.training.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanApplicationRepository loanRepository;

    @Mock
    private PlafondRepository plafondRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanService loanService;

    @Test
    void testApplyLoan_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).fullName("Budi").build();

        Plafond plafond = Plafond.builder()
                .id(1L)
                .maxAmount(new BigDecimal("1000"))
                .build();

        LoanRequest request = new LoanRequest();
        request.setPlafondId(1L);
        request.setAmount(new BigDecimal("500"));
        request.setTenor(12);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(plafondRepository.findById(1L)).thenReturn(Optional.of(plafond));

        LoanApplication savedLoan = LoanApplication.builder()
                .id(1L)
                .user(user)
                .plafond(plafond)
                .amount(request.getAmount())
                .tenor(request.getTenor())
                .status(ELoanStatus.SUBMITTED)
                .build();

        when(loanRepository.save(any(LoanApplication.class))).thenReturn(savedLoan);

        // Act
        LoanResponse response = loanService.applyLoan(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(new BigDecimal("500"), response.getAmount());

        verify(notificationService, times(1)).send(any(User.class), anyString(), anyString());
    }
}
