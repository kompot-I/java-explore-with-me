package ru.practicum.explorewithme.statserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.statdto.HitDto;
import ru.practicum.explorewithme.statdto.StatDto;
import ru.practicum.explorewithme.statserver.exception.BadRequestException;
import ru.practicum.explorewithme.statserver.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody HitDto hitDto) {
        log.info("Received a request to save session statistics with the following data: {}", hitDto);
        statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public Collection<StatDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(value = "uris", required = false) List<String> uris,
                                        @RequestParam(value = "unique", defaultValue = "false") Boolean unique) throws BadRequestException {
        log.info("Received a request for session statistics with parameters: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}