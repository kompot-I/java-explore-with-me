package ru.practicum.explorewithme.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.main.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Request was received to get a list of collections with parameters: pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        log.info("Request was received to receive a collection with ID: {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
