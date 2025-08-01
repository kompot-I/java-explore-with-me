package ru.practicum.explorewithme.main.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.comment.dto.CommentAdminRequest;
import ru.practicum.explorewithme.main.comment.dto.CommentDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comments")
public class CommentControllerAdmin {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentAdmin(@PathVariable(value = "commentId") Long commentId,
                                         @Valid @RequestBody CommentAdminRequest dto) {
        log.info("ADMIN: Received a request to edit a user comment: commentId={}", commentId);
        return commentService.updateCommentAdmin(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("ADMIN: Received a request to delete a user comment: commentId={}", commentId);
        commentService.deleteCommentAdmin(commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("ADMIN: Received a request to select a user comment: commentId={}", commentId);
        return commentService.getCommentAdmin(commentId);
    }

    @GetMapping()
    public Collection<CommentDto> getCommentsAdmin(@RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("ADMIN: Received a request to select all unpublished user comments");
        return commentService.getCommentsAdmin(from, size);
    }
}
