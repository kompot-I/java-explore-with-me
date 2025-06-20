package ru.practicum.explorewithme.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.enums.State;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.request.dal.RequestRepository;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.dto.RequestMapper;
import ru.practicum.explorewithme.main.request.enums.RequestState;
import ru.practicum.explorewithme.main.request.model.Request;
import ru.practicum.explorewithme.main.user.dal.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("User cannot create a request for their own event");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event has not been published");
        }
        if (requestRepository.existsByEventAndRequester(eventId, userId)) {
            throw new ConflictException("Request already exists");
        }
        int confirmedCount = requestRepository.countByEventAndStatus(eventId, RequestState.CONFIRMED);
        boolean autoConfirm = event.getParticipantLimit() == 0 || !event.getRequestModeration();

        if (!autoConfirm && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Limit on participants has been reached");
        }

        RequestState status = autoConfirm ? RequestState.CONFIRMED : RequestState.PENDING;
        Request request = Request.builder()
                .event(eventId)
                .requester(userId)
                .created(LocalDateTime.now())
                .status(status)
                .build();
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        if (!request.getRequester().equals(userId)) {
            throw new NotFoundException("User not authorized to request");
        }
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.toDto(request);
    }

    public List<RequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return RequestMapper.toDto(requestRepository.findAllByRequester(userId));
    }
}
