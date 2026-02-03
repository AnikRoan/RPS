package com.rpsB.demo.service;

import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeRepository recipeRepository;


    public RecipeResponse createRecipe(RecipeRequest recipeRequest, User user) {

        Recipe recipe = recipeMapper.toEntity(recipeRequest);
        recipe.setCreator(user);

        recipe.getIngredientList()
                .forEach(i -> i.setRecipe(recipe));

        return recipeMapper.toDto(recipeRepository.save(recipe));

    }

    public RecipeResponse updateRecipe(RecipeUpdateDto updateRecipe, UUID recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new EntityNotFoundException("Recipe not found"));

        Optional.ofNullable(updateRecipe.name())
                .ifPresent(recipe::setName);
        Optional.ofNullable(updateRecipe.description())
                .ifPresent(recipe::setDescription);
        Optional.of(updateRecipe.timeToCookMinutes())
                .ifPresent(recipe::setTimeToCookMinutes);
        Optional.ofNullable(updateRecipe.category())
                .ifPresent(recipe::setCategory);

        return recipeMapper.toDto(recipeRepository.save(recipe));
    }

    public RecipeResponse getRecipeById(UUID recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new EntityNotFoundException("Recipe not found")
        );

        return recipeMapper.toDto(recipe);
    }

    public Page<RecipeResponse> getRecipes(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return recipeRepository.findAllUserRecipe(user.getId(), pageable)
                .map(recipeMapper::toDto);
    }

    public void deliteRecipeById(UUID recipeId, User user) {
        recipeRepository.deleteById(recipeId);
    }
}
