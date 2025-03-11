package com.sera.refund.infrastructure;

import com.sera.refund.domain.UserIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserIncomeRepository extends JpaRepository<UserIncome, Long> {
    List<UserIncome> findByUserId(String userId);

    Optional<UserIncome> findByUserIdAndTaxYear(String userId, int taxYear);
}
