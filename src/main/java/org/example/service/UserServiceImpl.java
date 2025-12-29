package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.RegistrationException;
import org.example.mappers.UserMapper;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.role.RoleRepository;
import org.example.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {

            throw new RegistrationException("Can't register user");
        }
        User user = new User();
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));
        user.setFirstName(userRegistrationRequestDto.getFirstName());
        user.setLastName(userRegistrationRequestDto.getLastName());
        Role user1 = roleRepository.findByName("USER");
        user.getRoles().add(user1);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto getByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Can't find user"));
    }
}
