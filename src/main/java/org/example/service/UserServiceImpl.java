package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.RegistrationException;
import org.example.mappers.UserMapper;
import org.example.model.User;
import org.example.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {

            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toEntity(userRegistrationRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
