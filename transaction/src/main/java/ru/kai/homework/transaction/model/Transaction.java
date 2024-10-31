package ru.kai.homework.transaction.model;

import lombok.*;
import ru.kai.homework.transaction.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    private UUID id;
    private TransactionType type;
    private BigDecimal amount;
    private UUID accountId;
}
