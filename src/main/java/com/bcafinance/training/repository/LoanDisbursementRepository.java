package com.bcafinance.training.repository;

import com.bcafinance.training.entity.LoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDisbursementRepository extends JpaRepository<LoanDisbursement, Long> {
}
