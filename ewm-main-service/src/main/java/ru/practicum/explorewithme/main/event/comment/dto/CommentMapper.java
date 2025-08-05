package ru.practicum.explorewithme.main.event.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.event.comment.model.Comment;
import ru.practicum.explorewithme.main.event.dto.EventMapper;
import ru.practicum.explorewithme.main.user.dto.UserMapper;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .modifyDate(comment.getModifyDate())
                .userMessage(comment.getUserMessage())
                .adminMessage(comment.getAdminMessage())
                .accepted(comment.getAccepted())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .event(EventMapper.toEventSummaryDto(comment.getEvent()))
                .build();
    }

    public static CommentShortResponseDto toCommentShortResponseDto(Comment comment) {
        return CommentShortResponseDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .message(comment.getUserMessage())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .build();
    }

    public static CommentFullResponseDto toCommentFullResponseDto(Comment comment) {
        return CommentFullResponseDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .modifyDate(comment.getModifyDate())
                .userMessage(comment.getUserMessage())
                .adminMessage(comment.getAdminMessage())
                .accepted(comment.getAccepted())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .build();
    }

    public static Comment toCommentModel(CommentUserRequest request) {
        return Comment.builder()
                .userMessage(request.getMessage())
                .build();
    }
}
