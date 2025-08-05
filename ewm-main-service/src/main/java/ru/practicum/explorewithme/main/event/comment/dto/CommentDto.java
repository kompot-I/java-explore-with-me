package ru.practicum.explorewithme.main.event.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.main.event.dto.EventSummaryDto;
import ru.practicum.explorewithme.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifyDate;
    private EventSummaryDto event;
    private UserShortDto user;
    private String userMessage;
    private String adminMessage;
    private Boolean accepted;
}
