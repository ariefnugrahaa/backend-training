package com.bcafinance.training.repository;

import com.bcafinance.training.entity.ELoanStatus;
import com.bcafinance.training.entity.LoanApplication;
import com.bcafinance.training.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUser(User user);

    List<LoanApplication> findByStatus(ELoanStatus status);
}
