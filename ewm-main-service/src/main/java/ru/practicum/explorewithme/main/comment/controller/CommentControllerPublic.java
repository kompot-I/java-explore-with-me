package ru.practicum.explorewithme.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.comment.dto.EventShortCommentsResponseDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("events/{eventId}/comments")
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping()
    public EventShortCommentsResponseDto getEventComments(@PathVariable("eventId") Long eventId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("PUBLIC: Received a request to select all published comments on the event: eventId={}", eventId);
        return commentService.getEventComments(eventId, from, size);
    }
}
