package com.rpsB.demo.service;

import com.rpsB.demo.dto.VoteRequest;
import com.rpsB.demo.dto.VoteResponse;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.entity.Vote;
import com.rpsB.demo.mapper.VoteMapper;
import com.rpsB.demo.repository.VoteRepository;
import com.rpsB.demo.security.UserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final AuthService authService;
    private final EntityManager entityManager;

    //TODO: realize this
    @Transactional
    public VoteResponse createVote(VoteRequest request, UUID recipeId) {

        if (request == null) {
            throw new IllegalArgumentException("Vote request cannot be null");
        }
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        Vote vote = voteMapper.toEntity(request);
        vote.setUserVote(
                entityManager.getReference(User.class, principal.getId())
        );
        vote.setRecipe(
                entityManager.getReference(Recipe.class, recipeId)
        );

        try {
            return voteMapper.toDto(voteRepository.save(vote));
        } catch (DataIntegrityViolationException ex) {
            throw new EntityNotFoundException("Recipe not found");
        }
    }

    @Transactional
    public VoteResponse updateVote(VoteRequest request, UUID recipeId, Long vote_id) {
        if (request == null) {
            throw new IllegalArgumentException("Vote request cannot be null");
        }
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        Vote vote = voteRepository.findById(vote_id).orElseThrow(() ->
                new EntityNotFoundException("Vote not found")
        );

        if (!vote.getRecipe().getUuid().equals(recipeId)) {
            throw new IllegalArgumentException("Vote does not belong to this recipe");
        }

        if (!Objects.equals(vote.getUserVote().getId(), principal.getId())) {
            throw new IllegalArgumentException("You are not vote owner");
        }
        Optional.ofNullable(request.value()).ifPresent(vote::setValue);
        Optional.ofNullable(request.note()).ifPresent(vote::setNote);
        return voteMapper.toDto(vote);
    }

    public Page<VoteResponse> getVotes(UUID recipeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voteRepository.findVotesByRecipeId(recipeId, pageable)
                .map(voteMapper::toDto);

    }

    public String deleteVote(Long voteId) {
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        Vote vote = voteRepository.findById(voteId).orElseThrow();
        if (Objects.equals(vote.getUserVote().getId(), principal.getId())) {
            voteRepository.deleteById(voteId);
            return "Vote was deleted";
        }
        return "Vote was not deleted";
    }
}
