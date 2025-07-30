package ru.practicum.explorewithme.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.event.dto.EventFullDto;
import ru.practicum.explorewithme.main.event.enums.Sorting;
import ru.practicum.explorewithme.main.event.service.EventService;
import ru.practicum.explorewithme.statclient.StatClient;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventControllerPublic {

    private final EventService eventService;
    private final StatClient statisticsClient;

    @GetMapping
    public Collection<EventFullDto> getEventsPublic(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(defaultValue = "EVENT_DATE") Sorting sort,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    HttpServletRequest request) {
        log.info("PUBLIC: Received a request to search for events with the possibility of filtering");
        if (text != null) {
            text = text.equals("0") ? null : text.toLowerCase();
        }
        if (categories != null && categories.size() == 1 && categories.getFirst() == 0) {
            categories = null;
        }
        statisticsClient.save("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventPublic(@PathVariable("eventId") Long eventId, HttpServletRequest request) {
        log.info("PUBLIC: Request was received to search for detailed information about the published event.: eventId = {}", eventId);
        statisticsClient.save("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        return eventService.getEventPublic(eventId);
    }
}
