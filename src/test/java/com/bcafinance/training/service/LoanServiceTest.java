package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.LoanRequest;
import com.bcafinance.training.dto.response.LoanResponse;
import com.bcafinance.training.entity.*;
import com.bcafinance.training.repository.LoanApplicationRepository;
import com.bcafinance.training.repository.LoanDisbursementRepository;
import com.bcafinance.training.repository.PlafondRepository;
import com.bcafinance.training.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private LoanDisbursementRepository disbursementRepository;

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
                .name("Test Plafond")
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

        verify(notificationService, times(1)).send(eq(user), anyString(), anyString());
    }

    @Test
    void testApplyLoan_ExceedLimit() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        Plafond plafond = new Plafond();
        plafond.setId(1L);
        plafond.setMaxAmount(new BigDecimal("1000"));

        LoanRequest request = new LoanRequest();
        request.setPlafondId(1L);
        request.setAmount(new BigDecimal("1500"));
        request.setTenor(12);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(plafondRepository.findById(1L)).thenReturn(Optional.of(plafond));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.applyLoan(userId, request));
    }

    @Test
    void testGetLoansByStatus_Success() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).fullName("Test").build();
        Plafond plafond = Plafond.builder().id(1L).name("P").build();
        LoanApplication loan = LoanApplication.builder()
                .id(1L)
                .user(user)
                .plafond(plafond)
                .status(ELoanStatus.SUBMITTED)
                .amount(BigDecimal.TEN)
                .build();

        when(loanRepository.findByStatus(ELoanStatus.SUBMITTED)).thenReturn(List.of(loan));

        // Act
        List<LoanResponse> responses = loanService.getLoansByStatus(ELoanStatus.SUBMITTED);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("SUBMITTED", responses.get(0).getStatus());
    }

    @Test
    void testReviewLoan_Approve_Success() {
        // Arrange
        Long loanId = 1L;
        User user = User.builder().id(UUID.randomUUID()).fullName("Test").build();
        Plafond plafond = Plafond.builder().id(1L).name("P").build();
        LoanApplication loan = LoanApplication.builder()
                .id(loanId)
                .user(user)
                .plafond(plafond)
                .status(ELoanStatus.SUBMITTED)
                .amount(BigDecimal.TEN)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        LoanResponse response = loanService.reviewLoan(loanId, true);

        // Assert
        assertEquals("REVIEWED", response.getStatus());
        verify(notificationService).send(eq(user), anyString(), anyString());
    }

    @Test
    void testApproveLoan_Success() {
        // Arrange
        Long loanId = 1L;
        User user = User.builder().id(UUID.randomUUID()).fullName("Test").build();
        Plafond plafond = Plafond.builder().id(1L).name("P").build();
        LoanApplication loan = LoanApplication.builder()
                .id(loanId)
                .user(user)
                .plafond(plafond)
                .status(ELoanStatus.REVIEWED)
                .amount(BigDecimal.TEN)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        LoanResponse response = loanService.approveLoan(loanId, true);

        // Assert
        assertEquals("APPROVED", response.getStatus());
    }

    @Test
    void testDisburseLoan_Success() {
        // Arrange
        Long loanId = 1L;
        User user = User.builder().id(UUID.randomUUID()).fullName("Test").build();
        Plafond plafond = Plafond.builder().id(1L).name("P").build();
        LoanApplication loan = LoanApplication.builder()
                .id(loanId)
                .user(user)
                .plafond(plafond)
                .status(ELoanStatus.APPROVED)
                .amount(BigDecimal.TEN)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer(i -> i.getArgument(0));
        when(disbursementRepository.save(any(LoanDisbursement.class))).thenReturn(new LoanDisbursement());

        // Act
        LoanResponse response = loanService.disburseLoan(loanId, "TRANSFER");

        // Assert
        assertEquals("DISBURSED", response.getStatus());
        verify(disbursementRepository).save(any(LoanDisbursement.class));
    }
}
