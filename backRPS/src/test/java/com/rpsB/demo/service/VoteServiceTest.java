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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vote Service Unit Test")
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VoteMapper voteMapper;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private VoteService voteService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createVote_shouldThrowIfRequestNull() {

        assertThrows(AppException.class,
                () -> voteService.createVote(null, 1L, 1L));
    }

    @Test
    void createVote_shouldCreateVote() {

        Long recipeId = 1L;
        Long userId = 1L;

        VoteRequest request = new VoteRequest(5, "great");

        Vote vote = new Vote();
        VoteResponse response = new VoteResponse(1L, 5, "great", "");

        when(voteMapper.toEntity(request)).thenReturn(vote);
        when(entityManager.getReference(User.class, userId)).thenReturn(User.builder().build());
        when(entityManager.getReference(Recipe.class, recipeId)).thenReturn(new Recipe());
        when(voteRepository.save(vote)).thenReturn(vote);
        when(voteMapper.toDto(vote)).thenReturn(response);

        VoteResponse result =
                voteService.createVote(request, recipeId, userId);

        assertEquals(response, result);
    }

    @Test
    void createVote_shouldThrowIfDuplicateOrRecipeNotFound() {

        Long recipeId = 1L;
        Long userId = 1L;

        VoteRequest request = new VoteRequest(5, "great");
        Vote vote = new Vote();

        when(voteMapper.toEntity(request)).thenReturn(vote);
        when(entityManager.getReference(User.class, userId)).thenReturn(User.builder().build());
        when(entityManager.getReference(Recipe.class, recipeId)).thenReturn(new Recipe());
        when(voteRepository.save(vote))
                .thenThrow(new DataIntegrityViolationException("error"));

        assertThrows(AppException.class,
                () -> voteService.createVote(request, recipeId, userId));
    }

    @Test
    void updateVote_shouldThrowIfRequestNull() {

        assertThrows(IllegalArgumentException.class,
                () -> voteService.updateVote(null, 1L, 1L, 1L));
    }

    @Test
    void updateVote_shouldThrowIfNotFound() {

        when(voteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> voteService.updateVote(
                        new VoteRequest(5, "note"),
                        1L,
                        1L,
                        1L
                ));
    }

    @Test
    void updateVote_shouldThrowIfWrongRecipe() {

        Long recipeId = 1L;

        Vote vote = new Vote();
        Recipe recipe = new Recipe();
        recipe.setUuid(2L);

        vote.setRecipe(recipe);

        when(voteRepository.findById(1L))
                .thenReturn(Optional.of(vote));

        assertThrows(AppException.class,
                () -> voteService.updateVote(
                        new VoteRequest(5, "note"),
                        recipeId,
                        1L,
                        1L
                ));
    }

    @Test
    void updateVote_shouldThrowIfNotOwner() {

        Long recipeId = 1L;

        User user = User.builder()
                .id(2L)
                .build();

        Recipe recipe = new Recipe();
        recipe.setUuid(recipeId);

        Vote vote = new Vote();
        vote.setRecipe(recipe);
        vote.setUserVote(user);

        when(voteRepository.findById(1L))
                .thenReturn(Optional.of(vote));

        assertThrows(AppException.class,
                () -> voteService.updateVote(
                        new VoteRequest(5, "note"),
                        recipeId,
                        1L,
                        1L
                ));
    }

    @Test
    void updateVote_shouldUpdateVote() {

        Long recipeId = 1L;
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        Recipe recipe = new Recipe();
        recipe.setUuid(recipeId);

        Vote vote = new Vote();
        vote.setRecipe(recipe);
        vote.setUserVote(user);

        VoteRequest request = new VoteRequest(4, "updated");

        VoteResponse response = new VoteResponse(1L, 4, "updated", "");

        when(voteRepository.findById(1L))
                .thenReturn(Optional.of(vote));
        when(voteMapper.toDto(vote)).thenReturn(response);

        VoteResponse result =
                voteService.updateVote(request, recipeId, 1L, userId);

        assertEquals(4, vote.getValue());
        assertEquals("updated", vote.getNote());
        assertEquals(response, result);
    }

    @Test
    void getVotes_shouldReturnPage() {

        Long recipeId = 1L;

        Vote vote = new Vote();
        VoteResponse response = new VoteResponse(1L, 5, "note", "");

        Page<Vote> page = new PageImpl<>(List.of(vote));

        when(voteRepository.findVotesByRecipeId(eq(recipeId), any(Pageable.class)))
                .thenReturn(page);

        when(voteMapper.toDto(vote)).thenReturn(response);

        Page<VoteResponse> result =
                voteService.getVotes(recipeId, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
    }

    @Test
    void deleteVote_shouldThrowIfNotFound() {

        Long recipeId = 1L;

        when(voteRepository.findByIdAndRecipeId(recipeId, 1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> voteService.deleteVote(recipeId, 1L, 1L));
    }

    @Test
    void deleteVote_shouldThrowIfNotOwner() {

        Long recipeId = 1L;

        User user = User.builder()
                .id(2L)
                .build();

        Vote vote = new Vote();
        vote.setUserVote(user);

        when(voteRepository.findByIdAndRecipeId(recipeId, 1L))
                .thenReturn(Optional.of(vote));

        assertThrows(AppException.class,
                () -> voteService.deleteVote(recipeId, 1L, 1L));
    }

    @Test
    void deleteVote_shouldDelete() {

        Long recipeId = 1L;
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        Vote vote = new Vote();
        vote.setUserVote(user);

        when(voteRepository.findByIdAndRecipeId(recipeId, 1L))
                .thenReturn(Optional.of(vote));

        String result =
                voteService.deleteVote(recipeId, 1L, userId);

        verify(voteRepository).delete(vote);
        assertEquals("Vote was deleted", result);
    }
}