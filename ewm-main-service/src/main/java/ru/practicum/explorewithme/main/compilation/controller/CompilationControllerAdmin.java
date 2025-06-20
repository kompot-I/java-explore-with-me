package ru.practicum.explorewithme.main.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.main.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.main.compilation.dto.UpdateCompilationDto;
import ru.practicum.explorewithme.main.compilation.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Request was received to create a collection with data: {}", compilationDto);
        return compilationService.createCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationDto compilationDto) {
        log.info("Received a request to update the data collection: {}", compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Received a request to delete a collection with ID={}", compId);
        compilationService.deleteCompilation(compId);
    }
}
