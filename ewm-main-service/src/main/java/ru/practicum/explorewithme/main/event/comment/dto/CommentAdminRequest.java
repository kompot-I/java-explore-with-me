package ru.practicum.explorewithme.main.event.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentAdminRequest {
    private Long id;
    private Boolean accepted;
    private String message;
}
