package ru.practicum.explorewithme.main.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.comment.model.Comment;
import ru.practicum.explorewithme.main.event.dto.EventSummaryDto;
import ru.practicum.explorewithme.main.user.dto.UserShortDto;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment, UserShortDto user, EventSummaryDto event) {
        return CommentDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .modifyDate(comment.getModifyDate())
                .userMessage(comment.getUserMessage())
                .adminMessage(comment.getAdminMessage())
                .accepted(comment.getAccepted())
                .user(user)
                .event(event)
                .build();
    }

    public static CommentShortResponseDto toCommentShortResponseDto(Comment comment, UserShortDto user) {
        return CommentShortResponseDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .message(comment.getUserMessage())
                .user(user)
                .build();
    }

    public static CommentFullResponseDto toCommentFullResponseDto(Comment comment, UserShortDto user) {
        return CommentFullResponseDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .modifyDate(comment.getModifyDate())
                .userMessage(comment.getUserMessage())
                .adminMessage(comment.getAdminMessage())
                .accepted(comment.getAccepted())
                .user(user)
                .build();
    }

    public static Comment toCommentModel(CommentUserRequest request) {
        return Comment.builder()
                .userMessage(request.getMessage())
                .build();
    }
}
