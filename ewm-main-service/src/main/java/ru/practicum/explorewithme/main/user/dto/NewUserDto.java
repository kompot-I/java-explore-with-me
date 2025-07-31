package ru.practicum.explorewithme.main.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserDto {
    @NotBlank(message = "Name should not be empty or consist of spaces.")
    @Size(min = 2, max = 250, message = "Name must be between 2 and 250 characters long")
    private String name;

    @NotBlank(message = "Email must not be empty.")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters long")
    @Email(message = "Incorrect email")
    private String email;
}
