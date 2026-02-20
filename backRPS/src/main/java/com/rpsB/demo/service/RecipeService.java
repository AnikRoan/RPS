package com.rpsB.demo.service;

import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.entity.Ingredient;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.mapper.IngredientMapper;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final IngredientMapper ingredientMapper;
    private final RecipeRepository recipeRepository;
    private final EntityManager entityManager;

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest recipeRequest, Long userId) {
        Recipe recipe = recipeMapper.toEntity(recipeRequest);
        recipe.setCreator(entityManager.getReference(User.class, userId));

        recipe.getIngredientList()
                .forEach(i -> i.setRecipe(recipe));

        return recipeMapper.toDto(recipeRepository.save(recipe));

    }

    @Transactional
    public RecipeResponse updateRecipe(RecipeUpdateDto updateRecipe, UUID recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new AppException(HttpStatus.NO_CONTENT, "Recipe not found"));

        if (!recipe.getCreator().getId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not the owner");
        }

        Optional.ofNullable(updateRecipe.name()).ifPresent(recipe::setName);
        Optional.ofNullable(updateRecipe.description()).ifPresent(recipe::setDescription);
        Optional.ofNullable(updateRecipe.timeToCookMinutes()).ifPresent(recipe::setTimeToCookMinutes);
        Optional.ofNullable(updateRecipe.category()).ifPresent(recipe::setCategory);

        return recipeMapper.toDto(recipe);
    }

    public RecipeResponse getRecipeById(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new AppException(HttpStatus.NO_CONTENT, "Recipe not found")
        );

        return recipeMapper.toDto(recipe);
    }

    public Page<RecipeResponse> getMyRecipes(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);

        return recipeRepository.findAllUserRecipe(userId, pageable)
                .map(recipeMapper::toDto);
    }

    @Transactional
    public void deliteRecipeById(UUID recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new AppException(HttpStatus.NO_CONTENT, "Recipe not found")
        );
        if (Objects.equals(recipe.getCreator().getId(), userId)) {
            recipeRepository.deleteById(recipeId);
        }
    }

    //    Actions with ingredient
    @Transactional
    public IngredientResponse updateIngredient(UUID recipeId, Long ingredientId, IngredientRequest ingredientRequest, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new AppException(HttpStatus.NO_CONTENT, "Recipe not found"));
        if (!Objects.equals(userId, recipe.getCreator().getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, "this recipe is not your");
        }
        Ingredient ingredient = recipe.getIngredientList()
                .stream()
                .filter(i -> i.getId().equals(ingredientId))
                .findFirst()
                .orElseThrow(() ->
                        new AppException(HttpStatus.NO_CONTENT, "Ingredient not found"));

        Optional.ofNullable(ingredientRequest.name()).ifPresent(ingredient::setName);
        Optional.ofNullable(ingredientRequest.amount()).ifPresent(ingredient::setAmount);
        Optional.ofNullable(ingredientRequest.unit()).ifPresent(ingredient::setUnit);
        Optional.ofNullable(ingredientRequest.note()).ifPresent(ingredient::setNote);
        Optional.ofNullable(ingredientRequest.calories()).ifPresent(ingredient::setCalories);
        Optional.ofNullable(ingredientRequest.fats()).ifPresent(ingredient::setFats);
        Optional.ofNullable(ingredientRequest.carbs()).ifPresent(ingredient::setCarbs);

        return ingredientMapper.toDto(ingredient);
    }

    public void deleteIngredient(UUID recipeId, Long ingredientId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new AppException(HttpStatus.NO_CONTENT, "recipe not found"));
        if (!Objects.equals(userId, recipe.getCreator().getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, "this recipe is not your");
        }
        Ingredient ingredient = recipe.getIngredientList()
                .stream()
                .filter(i -> i.getId().equals(ingredientId))
                .findFirst()
                .orElseThrow(() ->
                        new AppException(HttpStatus.NO_CONTENT, "Ingredient not found"));

        recipe.getIngredientList().remove(ingredient);
    }

    @Transactional
    public IngredientResponse addIngredient(UUID recipeId, IngredientRequest ingredientRequest, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new AppException(HttpStatus.NO_CONTENT, "recipe not found"));
        if (!Objects.equals(userId, recipe.getCreator().getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, "this recipe is not your");
        }
        Ingredient ingredient = ingredientMapper.toEntity(ingredientRequest);

        recipe.addIngredient(ingredient);
        return ingredientMapper.toDto(ingredient);
    }
}
