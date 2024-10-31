package ru.kai.homework.correction.model;

import jakarta.persistence.*;
import lombok.*;
import ru.kai.homework.correction.model.enums.CorrectionStatus;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="corrections")
public class Correction {
    @Id
    @Column(name="transaction_id")
    private UUID transactionId;

    private int attempts = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CorrectionStatus status = CorrectionStatus.PENDING;

    public void incrementAttempts() {
        this.attempts++;
    }
}
