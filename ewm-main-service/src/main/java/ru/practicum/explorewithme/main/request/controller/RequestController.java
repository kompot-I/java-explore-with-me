package ru.practicum.explorewithme.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable("userId") Long userId, @RequestParam("eventId") Long eventId) {
        log.info("Request was received to create a request to participate in an event with the ID: {}", eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable("userId") Long userId, @PathVariable("requestId") Long requestId) {
        log.info("Request was received to cancel a request to participate in an event with an ID: {}", requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@PathVariable("userId") Long userId) {
        log.info("Request was received to get a list of user requests with the ID: {}", userId);
        return requestService.getRequestsByUser(userId);
    }
}
