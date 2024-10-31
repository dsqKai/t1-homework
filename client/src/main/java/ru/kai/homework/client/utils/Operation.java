package ru.kai.homework.client.utils;

import ru.kai.homework.client.exception.InsufficientFundsException;
import ru.kai.homework.client.model.enums.OperationType;

import java.math.BigDecimal;

public class Operation {
    private Operation() {}

    public static BigDecimal calculateBalance(java.math.BigDecimal currentBalance, BigDecimal amount, OperationType type) {
        return switch (type) {
            case DEBIT -> calculateDebitBalance(currentBalance, amount);
            case ACCRUAL -> currentBalance.add(amount);
        };
    }

    public static BigDecimal calculateDebitBalance(BigDecimal currentBalance, BigDecimal amount) {
        BigDecimal newBalance = currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException();
        }
        return newBalance;
    }
}
