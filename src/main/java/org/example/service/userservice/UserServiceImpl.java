package org.example.service.userservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.RegistrationException;
import org.example.mappers.UserMapper;
import org.example.model.Role;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.model.enums.RoleName;
import org.example.repository.role.RoleRepository;
import org.example.repository.shoppingcart.ShoppingCartRepository;
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
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartFactory shoppingCartFactory;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        isUserExist(userRegistrationRequestDto);
        User savedUser = registerUser(userRegistrationRequestDto);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto getByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RegistrationException(
                        "Can't find user"));
    }

    private void isUserExist(UserRegistrationRequestDto userRegistrationRequestDto) {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {

            throw new RegistrationException("Can't register user, cause "
                    + "email already exists");
        }
    }

    private User registerUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userMapper.toEntity(userRegistrationRequestDto);
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find ROLE_USER in database."));
        user.getRoles().add(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ShoppingCart shoppingCart = shoppingCartFactory.createShoppingCart(user);
        userRepository.save(user);
        shoppingCartRepository.save(shoppingCart);
        return user;
    }

}
