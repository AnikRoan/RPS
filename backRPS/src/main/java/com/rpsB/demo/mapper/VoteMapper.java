package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.VoteRequest;
import com.rpsB.demo.dto.VoteResponse;
import com.rpsB.demo.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    VoteResponse toDto(Vote vote);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "recipe",ignore = true)
    @Mapping(target = "userVote",ignore = true)
    Vote toEntity(VoteRequest request);
}
