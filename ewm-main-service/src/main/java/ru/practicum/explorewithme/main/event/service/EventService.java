package ru.practicum.explorewithme.main.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.category.dal.CategoryRepository;
import ru.practicum.explorewithme.main.category.dto.CategoryDto;
import ru.practicum.explorewithme.main.category.dto.CategoryMapper;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.dto.*;
import ru.practicum.explorewithme.main.event.enums.Sorting;
import ru.practicum.explorewithme.main.event.enums.State;
import ru.practicum.explorewithme.main.event.enums.StateAction;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.exception.BadRequestException;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.request.dal.RequestRepository;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.dto.RequestMapper;
import ru.practicum.explorewithme.main.request.enums.RequestState;
import ru.practicum.explorewithme.main.request.model.Request;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.dto.UserMapper;
import ru.practicum.explorewithme.main.user.dto.UserShortDto;
import ru.practicum.explorewithme.main.user.model.User;
import ru.practicum.explorewithme.statclient.StatClient;
import ru.practicum.explorewithme.statdto.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatClient statisticsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Collection<EventFullDto> getEventsAdmin(List<Long> users, List<Long> categories, List<String> states, String rangeStart, String rangeEnd, Integer from, Integer size) {

        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = LocalDateTime.parse(rangeStart, formatter);
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new BadRequestException("Filtering interval is set incorrectly");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return eventRepository.getEventsAdmin(users, categories, states, dateFrom, dateTo, pageable).stream()
                .map(event -> {
                    if (event.getConfirmedRequests() == null) {
                        int confirmed = requestRepository.countByEventAndStatus(event.getId(), RequestState.CONFIRMED);
                        event.setConfirmedRequests(confirmed);
                    }
                    Long catId = event.getCategory();
                    Long userId = event.getInitiator();
                    CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
                    UserShortDto initiatorDto = UserMapper.toUserShortDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found")));
                    return EventMapper.toEventFullDto(event, categoryDto, initiatorDto);
                })
                .toList();
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest entity) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id:" + eventId + " not found"));
        if (entity.getAnnotation() != null) {
            event.setAnnotation(entity.getAnnotation());
        }
        if (entity.getCategory() != null) {
            event.setCategory(entity.getCategory());
        }
        if (entity.getDescription() != null) {
            event.setDescription(entity.getDescription());
        }
        if (entity.getEventDate() != null) {
            LocalDateTime date = LocalDateTime.parse(entity.getEventDate(), formatter);
            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Minimum time interval of 2 hours before the start of the event must be observed.");
            }
            if (event.getPublishedOn() != null && date.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new ConflictException("Date of the event must be at least 1 hour later than the publication date.");
            }
            event.setEventDate(date);
        }
        if (entity.getPaid() != null) {
            event.setPaid(entity.getPaid());
        }
        if (entity.getParticipantLimit() != null) {
            event.setParticipantLimit(entity.getParticipantLimit());
        }
        if (entity.getRequestModeration() != null) {
            event.setRequestModeration(entity.getRequestModeration());
        }
        if (entity.getTitle() != null) {
            event.setTitle(entity.getTitle());
        }
        if (entity.getLocation() != null) {
            event.setLocationLat(entity.getLocation().lat);
            event.setLocationLon(entity.getLocation().lon);
        }
        if (entity.getStateAction() != null) {
            switch (entity.getStateAction()) {
                case StateAction.PUBLISH_EVENT:
                    if (event.getState() != State.PENDING) {
                        throw new ConflictException("An event can only be published if it is in the waiting state for publication");
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case StateAction.REJECT_EVENT:
                    if (event.getState() == State.PUBLISHED) {
                        throw new ConflictException("An event can only be rejected if it has not been published yet");
                    }
                    event.setState(State.CANCELED);
                    event.setPublishedOn(null);
                    break;
            }
        }
        event = eventRepository.save(event);
        Long catId = event.getCategory();
        Long userId = event.getInitiator();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
        UserShortDto initiatorDto = UserMapper.toUserShortDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found")));
        return EventMapper.toEventFullDto(event, categoryDto, initiatorDto);
    }

    @Transactional
    public EventFullDto createEventPrivate(Long userId, NewEventDto reqDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Event event = EventMapper.fromNewEventDto(reqDto);
        event.setInitiator(userId);
        if (LocalDateTime.parse(reqDto.getEventDate(), formatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Minimum time interval of 2 hours before the start of the event must be observed");
        }
        if (reqDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (reqDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (reqDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setPublishedOn(null);
        event = eventRepository.save(event);

        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
        UserShortDto userDto = UserMapper.toUserShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    public Collection<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.getEventsPrivate(userId, pageRequest);

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        CategoryMapper.toDto(categoryRepository.findById(event.getCategory())
                                .orElseThrow(() -> new NotFoundException("Category with id:" + event.getCategory() + " not found"))),
                        UserMapper.toUserShortDto(user)))
                .toList();
    }

    public EventFullDto getEventPrivate(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Event event = eventRepository.getEventPrivate(userId, eventId);
        if (event == null) {
            throw new NotFoundException("Event with id:" + eventId + " not found");
        }
        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
        UserShortDto userDto = UserMapper.toUserShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest entity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id:" + eventId + " not found"));
        if (event.getState() != State.PENDING && event.getState() != State.CANCELED) {
            throw new ConflictException("Can only change events in statuses.: PENDING, CANCELED");
        }
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("The user " + userId + " does not have access to the event " + eventId);
        }
        if (entity.getAnnotation() != null) {
            event.setAnnotation(entity.getAnnotation());
        }
        if (entity.getCategory() != null) {
            event.setCategory(entity.getCategory());
        }
        if (entity.getDescription() != null) {
            event.setDescription(entity.getDescription());
        }
        if (entity.getEventDate() != null) {
            LocalDateTime date = LocalDateTime.parse(entity.getEventDate(), formatter);
            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Minimum time interval of 2 hours before the start of the event must be observed.");
            }
            event.setEventDate(date);
        }
        if (entity.getPaid() != null) {
            event.setPaid(entity.getPaid());
        }
        if (entity.getParticipantLimit() != null) {
            event.setParticipantLimit(entity.getParticipantLimit());
        }
        if (entity.getRequestModeration() != null) {
            event.setRequestModeration(entity.getRequestModeration());
        }
        if (entity.getTitle() != null) {
            event.setTitle(entity.getTitle());
        }
        if (entity.getLocation() != null) {
            event.setLocationLat(entity.getLocation().lat);
            event.setLocationLon(entity.getLocation().lon);
        }
        if (entity.getStateAction() != null && entity.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        }
        if (entity.getStateAction() != null && entity.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }
        event = eventRepository.save(event);

        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
        UserShortDto userDto = UserMapper.toUserShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    public Collection<RequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Category with id:" + eventId + " not found"));

        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Access is allowed only to the initiator of the application");
        }

        return RequestMapper.toDto(requestRepository.findByEvent(eventId));
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id:" + eventId + " not found"));

        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Application can only be changed by the initiator");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("No need to confirm this application.");
        }
        List<Request> requests = requestRepository.findByIdIn(entity.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        int confirmedCount = requestRepository.countByEventAndStatus(eventId, RequestState.CONFIRMED);

        for (Request req : requests) {
            if (!req.getStatus().equals(RequestState.PENDING)) {
                throw new ConflictException("Can only update applications with the PENDING status.");
            }
        }
        for (Request req : requests) {
            if (entity.getStatus() == RequestState.CONFIRMED) {
                if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
                    req.setStatus(RequestState.REJECTED);
                    rejected.add(req);
                } else {
                    req.setStatus(RequestState.CONFIRMED);
                    confirmed.add(req);
                    confirmedCount++;
                }
            } else if (entity.getStatus() == RequestState.REJECTED) {
                req.setStatus(RequestState.REJECTED);
                rejected.add(req);
            }
        }
        requestRepository.saveAll(requests);

        if (entity.getStatus() == RequestState.CONFIRMED && event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            List<Request> pending = requestRepository.findByEventAndStatus(eventId, RequestState.PENDING);
            for (Request req : pending) {
                req.setStatus(RequestState.REJECTED);
            }
            requestRepository.saveAll(pending);
            rejected.addAll(pending);
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(RequestMapper.toDto(confirmed))
                .rejectedRequests(RequestMapper.toDto(rejected))
                .build();
    }

    public List<EventFullDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, Sorting sort, Integer from, Integer size) {
        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = LocalDateTime.parse(rangeStart, formatter);
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (rangeStart == null && rangeEnd == null) {
            dateFrom = LocalDateTime.now();
        }
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new BadRequestException("Date range is set incorrectly");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return switch (sort) {
            case Sorting.EVENT_DATE ->
                    eventRepository.getEventsPublicOrderByEventDate(text, categories, paid, onlyAvailable, dateFrom, dateTo, pageable).stream()
                            .map(this::buildFullDto)
                            .toList();
            case Sorting.VIEWS ->
                    eventRepository.getEventsPublicOrderByViews(text, categories, paid, onlyAvailable, dateFrom, dateTo, pageable).stream()
                            .map(this::buildFullDto)
                            .toList();
        };
    }

    public EventFullDto getEventPublic(Long eventId) {

        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id:" + eventId + " not found"));
        Long userId = event.getInitiator();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        UserShortDto userDto = UserMapper.toUserShortDto(user);
        Long catId = event.getCategory();

        LocalDateTime dateFrom = LocalDateTime.parse("1999-01-01 00:00:00", formatter);
        LocalDateTime dateTo = LocalDateTime.now();
        String[] uris = {"/events/" + eventId.toString()};
        ResponseEntity<Object> statistic = statisticsClient.getStats(dateFrom, dateTo, uris, true);

        if (statistic != null && statistic.hasBody()) {
            ObjectMapper mapper = new ObjectMapper();
            StatDto[] dto = mapper.convertValue(statistic.getBody(), StatDto[].class);
            Integer views = 0;
            if (dto != null && dto.length > 0) {
                views = dto[0].getHits();
            }
            event.setViews(views);
        }

        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id:" + catId + " not found")));
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    private EventFullDto buildFullDto(Event event) {
        CategoryDto categoryDto = CategoryMapper.toDto(
                categoryRepository.findById(event.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category with id: " + event.getCategory() + " not found"))
        );
        UserShortDto userDto = UserMapper.toUserShortDto(
                userRepository.findById(event.getInitiator())
                        .orElseThrow(() -> new NotFoundException("User with id: " + event.getInitiator() + " not found"))
        );
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }
}
