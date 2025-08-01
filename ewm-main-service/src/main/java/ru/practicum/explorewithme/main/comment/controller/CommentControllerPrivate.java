package ru.practicum.explorewithme.main.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.comment.dto.CommentUserRequest;
import ru.practicum.explorewithme.main.comment.dto.CommentDto;
import ru.practicum.explorewithme.main.comment.dto.EventCommentFullListResponseDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createCommentPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("eventId") Long eventId,
                                           @Valid @RequestBody CommentUserRequest dto) {
        log.info("PRIVATE: Received a request to create a comment on the event: userId={}, eventId={}, dto={}", userId, eventId, dto);
        return commentService.createCommentPrivate(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("eventId") Long eventId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentUserRequest dto) {
        log.info("PRIVATE: Received a request to update a comment on the event: userId={}, eventId={}, commentId={}, dto={}", userId, eventId, commentId, dto);
        return commentService.updateCommentPrivate(userId, eventId, commentId, dto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentPrivate(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @PathVariable("commentId") Long commentId) {
        log.info("PRIVATE: Received a request to extract a comment on the event: userId={}, eventId={}, commentId={}", userId, eventId, commentId);
        return commentService.getCommentPrivate(userId, eventId, commentId);
    }

    @GetMapping()
    public EventCommentFullListResponseDto getCommentsPrivate(@PathVariable("userId") Long userId,
                                                              @PathVariable("eventId") Long eventId,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("PRIVATE: Received a request to extract user comments with the ID: userId={}, eventId={}", userId, eventId);
        return commentService.getCommentsPrivate(userId, eventId, from, size);
    }
}
