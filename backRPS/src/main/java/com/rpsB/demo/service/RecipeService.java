package com.rpsB.demo.service;

import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.entity.Ingredient;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.mapper.IngredientMapper;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import com.rpsB.demo.security.UserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final AuthService authService;
    private final EntityManager entityManager;

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest recipeRequest) {
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();

        Recipe recipe = recipeMapper.toEntity(recipeRequest);
        recipe.setCreator(entityManager.getReference(User.class, principal.getId()));

        recipe.getIngredientList()
                .forEach(i -> i.setRecipe(recipe));

        return recipeMapper.toDto(recipeRepository.save(recipe));

    }

    @Transactional
    public RecipeResponse updateRecipe(RecipeUpdateDto updateRecipe, UUID recipeId) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new EntityNotFoundException("Recipe not found"));

        if (!recipe.getCreator().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner");
        }

        Optional.ofNullable(updateRecipe.name()).ifPresent(recipe::setName);
        Optional.ofNullable(updateRecipe.description()).ifPresent(recipe::setDescription);
        Optional.ofNullable(updateRecipe.timeToCookMinutes()).ifPresent(recipe::setTimeToCookMinutes);
        Optional.ofNullable(updateRecipe.category()).ifPresent(recipe::setCategory);

        return recipeMapper.toDto(recipe);
    }

    public RecipeResponse getRecipeById(UUID recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new EntityNotFoundException("Recipe not found")
        );

        return recipeMapper.toDto(recipe);
    }

    public Page<RecipeResponse> getMyRecipes(int page, int size) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        return recipeRepository.findAllUserRecipe(user.getId(), pageable)
                .map(recipeMapper::toDto);
    }

    @Transactional
    public void deliteRecipeById(UUID recipeId) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new EntityNotFoundException("Recipe not found")
        );
        if (Objects.equals(recipe.getCreator().getId(), user.getId())) {
            recipeRepository.deleteById(recipeId);
        }
    }

    //    Actions with ingredient

    @Transactional
    public IngredientResponse updateIngredient(UUID recipeId, Long ingredientId, IngredientRequest ingredientRequest) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new EntityNotFoundException("recipe not found"));
        if (!Objects.equals(user.getId(), recipe.getCreator().getId())) {
            throw new UsernameNotFoundException("this recipe is not your");
        }
        Ingredient ingredient = recipe.getIngredientList()
                .stream()
                .filter(i -> i.getId().equals(ingredientId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Ingredient not found"));


        Optional.ofNullable(ingredientRequest.name()).ifPresent(ingredient::setName);
        Optional.ofNullable(ingredientRequest.amount()).ifPresent(ingredient::setAmount);
        Optional.ofNullable(ingredientRequest.unit()).ifPresent(ingredient::setUnit);
        Optional.ofNullable(ingredientRequest.note()).ifPresent(ingredient::setNote);
        Optional.ofNullable(ingredientRequest.calories()).ifPresent(ingredient::setCalories);
        Optional.ofNullable(ingredientRequest.fats()).ifPresent(ingredient::setFats);
        Optional.ofNullable(ingredientRequest.carbs()).ifPresent(ingredient::setCarbs);

        return ingredientMapper.toDto(ingredient);
    }

    public void deleteIngredient(UUID recipeId, Long ingredientId) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new EntityNotFoundException("recipe not found"));
        if (!Objects.equals(user.getId(), recipe.getCreator().getId())) {
            throw new UsernameNotFoundException("this recipe is not your");
        }
        Ingredient ingredient = recipe.getIngredientList()
                .stream()
                .filter(i -> i.getId().equals(ingredientId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Ingredient not found"));

        recipe.getIngredientList().remove(ingredient);
    }

    @Transactional
    public IngredientResponse addIngredient(UUID recipeId, IngredientRequest ingredientRequest) {
        UserPrincipal user = authService.getAuthenticatedUserPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new EntityNotFoundException("recipe not found"));
        if (!Objects.equals(user.getId(), recipe.getCreator().getId())) {
            throw new AccessDeniedException("this recipe is not your");
        }
        Ingredient ingredient = ingredientMapper.toEntity(ingredientRequest);

        recipe.addIngredient(ingredient);
        return ingredientMapper.toDto(ingredient);
    }
}
