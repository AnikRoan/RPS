package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "string"
)
public interface UserMapper {

    UserDto toDto(User user);
}
