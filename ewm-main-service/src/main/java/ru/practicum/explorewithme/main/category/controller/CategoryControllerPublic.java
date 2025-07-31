package ru.practicum.explorewithme.main.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.category.dto.CategoryDto;
import ru.practicum.explorewithme.main.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request was received to get a list of categories with an offset from {} to {} positions", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long catId) {
        log.info("A request was received to get a category with an ID: {}", catId);
        return categoryService.getCategoryById(catId);
    }
}
