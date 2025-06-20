package ru.practicum.explorewithme.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UpdateCompilationDto {
    @Size(min = 1, max = 50, message = "The title must contain from 1 to 50 characters.")
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}
