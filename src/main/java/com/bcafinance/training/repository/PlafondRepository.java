package com.bcafinance.training.repository;

import com.bcafinance.training.entity.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond, Long> {
}
