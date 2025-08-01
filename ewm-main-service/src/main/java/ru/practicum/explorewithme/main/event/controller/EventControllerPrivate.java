package ru.practicum.explorewithme.main.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.event.dto.*;
import ru.practicum.explorewithme.main.event.service.EventService;
import ru.practicum.explorewithme.main.request.dto.RequestDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class EventControllerPrivate {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@PathVariable("userId") Long userId,
                                           @Valid @RequestBody NewEventDto dto) {
        log.info("PRIVATE: Received a request to create an event: userId={}, dto={}", userId, dto);
        return eventService.createEventPrivate(userId, dto);
    }

    @GetMapping("/{userId}/events")
    public Collection<EventShortDto> getEventsPrivate(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("PRIVATE: Event search request received: userId={}", userId);
        return eventService.getEventsPrivate(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventPrivate(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId) {
        log.info("PRIVATE: Event search request received: userId={}, eventId={}", userId, eventId);
        return eventService.getEventPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("eventId") Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest dto) {
        log.info("PRIVATE: Received a request to change the event: userId={}, eventId={}, dto={}", userId, eventId, dto);
        return eventService.updateEventPrivate(userId, eventId, dto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getEventRequestsPrivate(@PathVariable("userId") Long userId,
                                                          @PathVariable("eventId") Long eventId) {
        log.info("PRIVATE: Request was received to search for information about requests to participate in the event: userId={}, eventId={}", userId, eventId);
        return eventService.getEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(@PathVariable("userId") Long userId,
                                                                     @PathVariable("eventId") Long eventId,
                                                                     @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("PRIVATE: Request has been received to change the applications for participation in the event: userId={}, eventId={}, dto={}", userId, eventId, dto);
        return eventService.updateEventRequestsPrivate(userId, eventId, dto);
    }
}
