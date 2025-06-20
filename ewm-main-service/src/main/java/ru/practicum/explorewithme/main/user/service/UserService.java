package ru.practicum.explorewithme.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.dto.UserMapper;
import ru.practicum.explorewithme.main.user.dto.NewUserDto;
import ru.practicum.explorewithme.main.user.dto.UserDto;
import ru.practicum.explorewithme.main.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(NewUserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("The user with this email already exists");
        }
        User user = UserMapper.toUser(userDto);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids != null && !ids.isEmpty()) {
            return UserMapper.toDto(userRepository.findByIdIn(ids));
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<User> pagedResult = userRepository.findAll(pageable);
        return UserMapper.toDto(pagedResult.getContent());
    }

    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("The user with this id does not exist");
        }
        userRepository.deleteById(userId);
    }
}
