package ru.practicum.explorewithme.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.enums.StateAction;
import ru.practicum.explorewithme.main.event.model.Location;

@Data
@Builder
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    private String eventDate;
    private Long category;
    private Boolean paid;
    private Boolean requestModeration;
    private StateAction stateAction;

    @PositiveOrZero
    private Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Location location;
}
