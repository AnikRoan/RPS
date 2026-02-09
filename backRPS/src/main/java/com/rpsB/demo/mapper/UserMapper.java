package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "string"
)
public interface UserMapper {

    @Mapping(target = "recipes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserDto toDto(User user);
}
