package org.example.mappers;

import org.example.configuration.MapperConfiguration;
import org.example.dto.userdto.UserRegistrationRequestDto;
import org.example.dto.userdto.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper {

    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserResponseDto toDto(User entity);

}
