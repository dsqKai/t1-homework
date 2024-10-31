package ru.kai.homework.transaction.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.kai.homework.transaction.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequest {
        @NotNull(message = "Type is required")
        private TransactionType type;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        private BigDecimal amount;

        @NotNull(message = "Account ID cannot be blank")
        @JsonProperty("account_id")
        private UUID accountId;
}

