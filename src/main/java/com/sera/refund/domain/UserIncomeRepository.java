package com.sera.refund.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIncomeRepository extends JpaRepository<UserIncome, Long> {
    Optional<UserIncome> findByUserId(String userId);

    Optional<UserIncome> findByUserIdAndTaxYear(String userId, int taxYear);
}
