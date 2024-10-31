package ru.kai.homework.client.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRequest {

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @JsonProperty("middle_name")
    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;

}
