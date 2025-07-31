package ru.practicum.explorewithme.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private Long id;
    @Size(min = 1, max = 50, message = "The name must contain from 1 to 50 characters.")
    @NotBlank(message = "The name should not be empty or consist of spaces.")
    private String name;
}
