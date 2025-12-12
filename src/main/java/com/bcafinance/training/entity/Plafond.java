package com.bcafinance.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plafonds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plafond {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // e.g., "MORTGAGE", "VEHICLE"
    private String type;

    @Column(nullable = false)
    private BigDecimal maxAmount;

    @Column(nullable = false)
    private BigDecimal interestRate; // e.g. 5.5 for 5.5%
}
