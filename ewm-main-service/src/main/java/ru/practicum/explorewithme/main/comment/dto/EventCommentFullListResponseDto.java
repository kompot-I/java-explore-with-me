package ru.practicum.explorewithme.main.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.dto.EventSummaryDto;

import java.util.List;

@Data
@Builder
public class EventCommentFullListResponseDto {
    private EventSummaryDto event;
    private List<CommentFullResponseDto> comments;
}
