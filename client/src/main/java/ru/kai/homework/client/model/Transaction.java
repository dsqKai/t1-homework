package ru.kai.homework.client.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kai.homework.client.model.enums.TransactionStatus;
import ru.kai.homework.client.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction{
    @Id
    private UUID id;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OperationType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}
