package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.LoanRequest;
import com.bcafinance.training.dto.response.LoanResponse;
import com.bcafinance.training.entity.*;
import com.bcafinance.training.repository.LoanApplicationRepository;
import com.bcafinance.training.repository.LoanDisbursementRepository;
import com.bcafinance.training.repository.PlafondRepository;
import com.bcafinance.training.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanApplicationRepository loanRepository;
    private final PlafondRepository plafondRepository;
    private final UserRepository userRepository;
    private final LoanDisbursementRepository disbursementRepository;
    private final NotificationService notificationService;

    public LoanResponse applyLoan(UUID userId, LoanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Plafond plafond = plafondRepository.findById(request.getPlafondId())
                .orElseThrow(() -> new RuntimeException("Plafond not found"));

        if (request.getAmount().compareTo(plafond.getMaxAmount()) > 0) {
            throw new RuntimeException("Loan amount exceeds plafond limit");
        }

        LoanApplication loan = LoanApplication.builder()
                .user(user)
                .plafond(plafond)
                .amount(request.getAmount())
                .tenor(request.getTenor())
                .status(ELoanStatus.SUBMITTED)
                .build();

        LoanApplication saved = loanRepository.save(loan);
        notificationService.send(user, "Loan Submitted", "Your loan " + saved.getId()
                + " is submitted.");

        return mapToResponse(saved);
    }

    public List<LoanResponse> getMyLoans(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loanRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LoanResponse> getLoansByStatus(ELoanStatus status) {
        return loanRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanResponse reviewLoan(Long loanId, boolean approve) {
        LoanApplication loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals(ELoanStatus.SUBMITTED)) {
            throw new RuntimeException("Invalid status flow. Current: " + loan.getStatus());
        }

        if (approve) {
            loan.setStatus(ELoanStatus.REVIEWED);
        } else {
            loan.setStatus(ELoanStatus.REJECTED);
        }

        notificationService.send(loan.getUser(), "Loan Update", "Your loan " +
                loan.getId() + " status: " + loan.getStatus());
        return mapToResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId, boolean approve) {
        LoanApplication loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals(ELoanStatus.REVIEWED)) {
            throw new RuntimeException(
                    "Invalid status flow. Only REVIEWED loans can be APPROVED. Current: " + loan.getStatus());
        }

        if (approve) {
            loan.setStatus(ELoanStatus.APPROVED);
        } else {
            loan.setStatus(ELoanStatus.REJECTED);
        }

        notificationService.send(loan.getUser(), "Loan Update", "Your loan " +
                loan.getId() + " status: " + loan.getStatus());
        return mapToResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse disburseLoan(Long loanId, String method) {
        LoanApplication loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals(ELoanStatus.APPROVED)) {
            throw new RuntimeException(
                    "Invalid status flow. Only APPROVED loans can be DISBURSED. Current: " + loan.getStatus());
        }

        loan.setStatus(ELoanStatus.DISBURSED);
        loanRepository.save(loan);

        LoanDisbursement disbursement = LoanDisbursement.builder()
                .loanApplication(loan)
                .disbursedAt(LocalDateTime.now())
                .method(method)
                .auditNote("Disbursed by Back Office")
                .build();
        disbursementRepository.save(disbursement);

        notificationService.send(loan.getUser(), "Loan Disbursed", "Money sent via "
                + method);
        return mapToResponse(loan);
    }

    private LoanResponse mapToResponse(LoanApplication loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .userName(loan.getUser().getFullName())
                .plafondId(loan.getPlafond().getId())
                .plafondName(loan.getPlafond().getName())
                .amount(loan.getAmount())
                .tenor(loan.getTenor())
                .status(loan.getStatus().name())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }
}
