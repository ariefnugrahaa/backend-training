package com.bcafinance.training.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plafond_id", nullable = false)
    private Plafond plafond;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer tenor; // In months

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ELoanStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
