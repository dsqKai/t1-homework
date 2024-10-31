package ru.kai.homework.client.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.kai.homework.client.model.enums.AccountType;

import java.util.UUID;

@Data
@Builder
public class AccountRequest {
    @NotNull(message = "Client ID cannot be null")
    @JsonProperty("client_id")
    private UUID clientId;

    @NotNull(message = "Type is required")
    private AccountType type;
}
