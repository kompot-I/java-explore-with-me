package ru.practicum.explorewithme.main.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.user.dto.NewUserDto;
import ru.practicum.explorewithme.main.user.dto.UserDto;
import ru.practicum.explorewithme.main.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Request to create a user has been received: {}", newUserDto);
        return userService.createUser(newUserDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {

        log.info("Request for users has been received: ids={}, from={}, size={}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("Request to delete a user has been received with id={}", userId);
        userService.deleteUserById(userId);
    }

}
