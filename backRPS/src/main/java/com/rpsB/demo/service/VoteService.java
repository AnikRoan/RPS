package com.rpsB.demo.service;

import com.rpsB.demo.dto.VoteRequest;
import com.rpsB.demo.dto.VoteResponse;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.entity.Vote;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.mapper.VoteMapper;
import com.rpsB.demo.repository.VoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final EntityManager entityManager;


    @Transactional
    public VoteResponse createVote(VoteRequest request, Long recipeId, Long userId) {
        if (request == null) {
            throw new AppException(HttpStatus.CONFLICT, "Vote request cannot be null");
        }
        Vote vote = voteMapper.toEntity(request);
        vote.setUserVote(
                entityManager.getReference(User.class, userId)
        );
        vote.setRecipe(
                entityManager.getReference(Recipe.class, recipeId)
        );

        try {
            return voteMapper.toDto(voteRepository.save(vote));
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(HttpStatus.FORBIDDEN, "Recipe not found," +
                    " or you can not write more votes to this recipe");
        }
    }

    @Transactional
    public VoteResponse updateVote(VoteRequest request, Long recipeId, Long vote_id, Long userId) {
        if (request == null) {
            throw new IllegalArgumentException("Vote request cannot be null");
        }
        Vote vote = voteRepository.findById(vote_id).orElseThrow(() ->
                new AppException(HttpStatus.NOT_FOUND, "Vote not found")
        );

        if (!vote.getRecipe().getUuid().equals(recipeId)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Vote does not belong to this recipe");
        }

        if (!Objects.equals(vote.getUserVote().getId(), userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not vote owner");
        }
        Optional.ofNullable(request.value()).ifPresent(vote::setValue);
        Optional.ofNullable(request.note()).ifPresent(vote::setNote);
        return voteMapper.toDto(vote);
    }

    public Page<VoteResponse> getVotes(Long recipeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voteRepository.findVotesByRecipeId(recipeId, pageable)
                .map(voteMapper::toDto);
    }

    @Transactional
    public String deleteVote(Long recipeId, Long voteId, Long userId) {
        Vote vote = voteRepository
                .findByIdAndRecipeId(recipeId, voteId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Vote not found"));

        if (!Objects.equals(vote.getUserVote().getId(), userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
        }

        voteRepository.delete(vote);

        return "Vote was deleted";
    }
}
