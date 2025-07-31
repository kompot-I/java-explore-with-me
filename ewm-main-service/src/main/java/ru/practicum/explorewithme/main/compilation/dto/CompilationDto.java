package ru.practicum.explorewithme.main.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.model.Event;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private List<Event> events;
}
