package ru.practicum.explorewithme.main.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.request.model.Request;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class RequestMapper {
    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent())
                .requester(request.getRequester())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> toDto(Collection<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }
}
