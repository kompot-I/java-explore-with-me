package ru.practicum.explorewithme.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.comment.dal.CommentRepository;
import ru.practicum.explorewithme.main.comment.dto.*;
import ru.practicum.explorewithme.main.comment.model.Comment;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.dto.EventMapper;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.dto.UserMapper;
import ru.practicum.explorewithme.main.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto createCommentPrivate(Long userId, Long eventId, CommentUserRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));

        Comment comment = CommentMapper.toCommentModel(dto);
        comment.setUserId(userId);
        comment.setEventId(eventId);
        comment.setAccepted(false);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setModifyDate(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    @Transactional
    public CommentDto updateCommentPrivate(Long userId, Long eventId, Long commentId, CommentUserRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new ConflictException("Cannot edit other users' comments");
        }
        if (comment.getAccepted().equals(true)) {
            throw new ConflictException("Cannot edit a verified comment.");
        }

        comment.setUserMessage(dto.getMessage());
        comment.setModifyDate(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    public CommentDto getCommentPrivate(Long userId, Long eventId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new ConflictException("Cannot access other users' comments.");
        }

        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    public EventCommentFullListResponseDto getCommentsPrivate(Long userId, Long eventId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<CommentFullResponseDto> comments = commentRepository.getEventUserComments(eventId, userId, pageable).stream()
                .map(comment -> CommentMapper.toCommentFullResponseDto(comment, UserMapper.toUserShortDto(user)))
                .toList();
        return EventCommentFullListResponseDto.builder()
                .event(EventMapper.toEventSummaryDto(event))
                .comments(comments)
                .build();
    }

    public EventShortCommentsResponseDto getEventComments(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<CommentShortResponseDto> comments = commentRepository.getEventComments(eventId, pageable).stream()
                .map(comment -> {
                    Long userId = comment.getUserId();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
                    return CommentMapper.toCommentShortResponseDto(comment, UserMapper.toUserShortDto(user));
                }).toList();
        return EventShortCommentsResponseDto.builder()
                .event(EventMapper.toEventSummaryDto(event))
                .comments(comments)
                .build();
    }

    @Transactional
    public CommentDto updateCommentAdmin(Long commentId, CommentAdminRequest dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));

        Long userId = comment.getUserId();
        Long eventId = comment.getEventId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));

        if (dto.getMessage() != null) {
            comment.setAdminMessage(dto.getMessage());
        }
        if (dto.getAccepted() != null) {
            comment.setAccepted(dto.getAccepted());
        }

        comment.setModifyDate(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));
        commentRepository.deleteById(commentId);
    }

    public CommentDto getCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));
        Long userId = comment.getUserId();
        Long eventId = comment.getEventId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));

        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    public Collection<CommentDto> getCommentsAdmin(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return commentRepository.getCommentsAdmin(pageable).stream()
                .map(comment -> {
                    Long userId = comment.getUserId();
                    Long eventId = comment.getEventId();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
                    Event event = eventRepository.findById(eventId)
                            .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));
                    return CommentMapper.toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
                }).collect(Collectors.toList());
    }
}
