package com.utkarsh.moviebooking;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RefundPolicyCalculator {

    public BigDecimal calculate(BigDecimal totalAmount, LocalDateTime showStartTime) {
        long hoursUntilShow = ChronoUnit.HOURS.between(LocalDateTime.now(), showStartTime);

        if (hoursUntilShow >= 24) {
            return totalAmount;                                      // 100%
        } else if (hoursUntilShow >= 2) {
            return totalAmount.multiply(BigDecimal.valueOf(0.5));    // 50%
        } else {
            return BigDecimal.ZERO;                                  // 0%
        }
    }
}
