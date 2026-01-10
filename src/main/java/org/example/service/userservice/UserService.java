package org.example.service.userservice;

import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.RegistrationException;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    UserResponseDto getByEmail(String email);
}
