package com.bcafinance.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_disbursements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @Column(nullable = false)
    private LocalDateTime disbursedAt;

    private String method; // e.g., "BANK_TRANSFER"

    private String auditNote;
}
