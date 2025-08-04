package ru.practicum.explorewithme.main.event.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.event.comment.dto.CommentAdminRequest;
import ru.practicum.explorewithme.main.event.comment.dto.CommentDto;
import ru.practicum.explorewithme.main.event.comment.service.CommentService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody CommentAdminRequest dto) {
        log.info("ADMIN: Received a request to edit a user comment: commentId={}", commentId);
        return commentService.updateCommentByAdmin(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "commentId") Long commentId) {
        log.info("ADMIN: Received a request to delete a user comment: commentId={}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable(value = "commentId") Long commentId) {
        log.info("ADMIN: Received a request to select a user comment: commentId={}", commentId);
        return commentService.getCommentByAdmin(commentId);
    }

    @GetMapping()
    public Collection<CommentDto> getComments(@RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("ADMIN: Received a request to select all unpublished user comments");
        return commentService.getCommentsByAdmin(from, size);
    }
}
