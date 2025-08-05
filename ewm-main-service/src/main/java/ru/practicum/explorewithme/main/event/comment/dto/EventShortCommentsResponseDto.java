package ru.practicum.explorewithme.main.event.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.dto.EventSummaryDto;

import java.util.List;

@Data
@Builder
public class EventShortCommentsResponseDto {
    private EventSummaryDto eventInfo;
    private List<CommentShortResponseDto> comments;
}
