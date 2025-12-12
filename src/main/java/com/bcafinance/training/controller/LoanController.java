package com.bcafinance.training.controller;

import com.bcafinance.training.dto.request.LoanRequest;
import com.bcafinance.training.dto.response.LoanResponse;
import com.bcafinance.training.entity.ELoanStatus;
import com.bcafinance.training.security.services.UserDetailsImpl;
import com.bcafinance.training.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('NASABAH')")
    public ResponseEntity<LoanResponse> applyLoan(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.applyLoan(userDetails.getId(), request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('NASABAH')")
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(loanService.getMyLoans(userDetails.getId()));
    }

    // Marketing Review
    @GetMapping("/review")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<List<LoanResponse>> getLoansForReview() {
        return ResponseEntity.ok(loanService.getLoansByStatus(ELoanStatus.SUBMITTED));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<LoanResponse> reviewLoan(@PathVariable Long id, @RequestParam boolean approve) {
        return ResponseEntity.ok(loanService.reviewLoan(id, approve));
    }

    // Branch Manager Approval
    @GetMapping("/approval")
    @PreAuthorize("hasRole('BRANCH_MANAGER')")
    public ResponseEntity<List<LoanResponse>> getLoansForApproval() {
        return ResponseEntity.ok(loanService.getLoansByStatus(ELoanStatus.REVIEWED));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('BRANCH_MANAGER')")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long id, @RequestParam boolean approve) {
        return ResponseEntity.ok(loanService.approveLoan(id, approve));
    }

    // Back Office Disbursement
    @GetMapping("/disbursement")
    @PreAuthorize("hasRole('BACK_OFFICE')")
    public ResponseEntity<List<LoanResponse>> getLoansForDisbursement() {
        return ResponseEntity.ok(loanService.getLoansByStatus(ELoanStatus.APPROVED));
    }

    @PostMapping("/{id}/disburse")
    @PreAuthorize("hasRole('BACK_OFFICE')")
    public ResponseEntity<LoanResponse> disburseLoan(@PathVariable Long id, @RequestParam String method) {
        return ResponseEntity.ok(loanService.disburseLoan(id, method));
    }
}
