package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.RegisterUserException;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User authentication endpoints", description = "User authentication endpoints")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/registration")
    @Operation(summary = "Registration of user", description = "Registration of user")
    public UserResponseDto registerUser(@RequestBody @Valid
                                            UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegisterUserException {
        return userService.register(userRegistrationRequestDto);
    }
}
