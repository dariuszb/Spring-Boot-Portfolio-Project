package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.RegisterUserException;
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
            throws RegisterUserException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {

            throw new RegisterUserException("Can't register user");
        }
        User user = new org.example.model.User();
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setPassword(userRegistrationRequestDto.getPassword());
        user.setFirstName(userRegistrationRequestDto.getFirstName());
        user.setLastName(userRegistrationRequestDto.getLastName());
        user.setShippingAddress(userRegistrationRequestDto.getShippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(userRepository.getById(savedUser.getId()));
    }
}
