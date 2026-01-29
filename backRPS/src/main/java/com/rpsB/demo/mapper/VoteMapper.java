package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.VoteDto;
import com.rpsB.demo.entity.Vote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    VoteDto toDto(Vote vote);
}
