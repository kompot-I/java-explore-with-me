package ru.practicum.explorewithme.main.event.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.event.comment.dal.CommentRepository;
import ru.practicum.explorewithme.main.event.comment.dto.*;
import ru.practicum.explorewithme.main.event.comment.model.Comment;
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
        User user = findUserById(userId);
        Event event = findEventById(eventId);

        Comment comment = CommentMapper.toCommentModel(dto);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setAccepted(false);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setModifyDate(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    @Transactional
    public CommentDto updateCommentPrivate(Long userId, Long eventId, Long commentId, CommentUserRequest dto) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
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
        User user = findUserById(userId);
        Event event = findEventById(eventId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Cannot access other users' comments.");
        }

        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    public EventCommentFullListResponseDto getCommentsPrivate(Long userId, Long eventId, Integer from, Integer size) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);

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
        Event event = findEventById(eventId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<CommentShortResponseDto> comments = commentRepository.getEventComments(eventId, pageable).stream()
                .map(comment -> {
                    Long userId = comment.getUser().getId();
                    User user = findUserById(userId);
                    return CommentMapper.toCommentShortResponseDto(comment, UserMapper.toUserShortDto(user));
                }).toList();
        return EventShortCommentsResponseDto.builder()
                .event(EventMapper.toEventSummaryDto(event))
                .comments(comments)
                .build();
    }

    @Transactional
    public CommentDto updateCommentByAdmin(Long commentId, CommentAdminRequest dto) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(comment.getUser().getId());
        Event event = findEventById(comment.getEvent().getId());

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
    public void deleteCommentByAdmin(Long commentId) {
        findCommentById(commentId);
        commentRepository.deleteById(commentId);
    }

    public CommentDto getCommentByAdmin(Long commentId) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(comment.getUser().getId());
        Event event = findEventById(comment.getEvent().getId());

        return CommentMapper
                .toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
    }

    public Collection<CommentDto> getCommentsByAdmin(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return commentRepository.getCommentsAdmin(pageable).stream()
                .map(comment -> {
                    User user = findUserById(comment.getUser().getId());
                    Event event = findEventById(comment.getEvent().getId());
                    return CommentMapper.toCommentDto(comment, UserMapper.toUserShortDto(user), EventMapper.toEventSummaryDto(event));
                }).collect(Collectors.toList());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with Id " + userId));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event with Id " + eventId));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with Id " + commentId));
    }
}
