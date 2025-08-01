package ru.practicum.explorewithme.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.model.Location;

@Data
@Builder
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    @NotBlank(message = "Annotation must not be blank")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    @NotBlank(message = "Description must not be blank")
    private String description;

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotNull(message = "Event date must not be null")
    private String eventDate;

    @NotNull(message = "Category must not be null")
    private Long category;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Location location;
}
