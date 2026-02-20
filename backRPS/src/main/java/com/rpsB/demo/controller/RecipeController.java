package com.rpsB.demo.controller;


import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.security.oauth2.CurrentUserProvider;
import com.rpsB.demo.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final CurrentUserProvider userProvider;

    @PostMapping()
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody RecipeRequest recipeRequest) {

        return ResponseEntity.ok().body(recipeService.createRecipe(
                recipeRequest,
                userProvider.getAuthUserPrincipalId()));
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable UUID recipeId,
                                                       @RequestBody RecipeUpdateDto updateDto) {
        return ResponseEntity.ok().body(recipeService.updateRecipe(
                updateDto,
                recipeId,
                userProvider.getAuthUserPrincipalId()));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable UUID recipeId) {
        return ResponseEntity.ok().body(recipeService.getRecipeById(recipeId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<RecipeResponse>> getUserRecipes(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(recipeService.getMyRecipes(
                page,
                size,
                userProvider.getAuthUserPrincipalId()));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable UUID recipeId) {
        recipeService.deliteRecipeById(recipeId, userProvider.getAuthUserPrincipalId());
        return ResponseEntity.ok().body(recipeId);
    }

    //    Actions with ingredient
    @PutMapping("/{recipeId}/ingredient/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(@PathVariable UUID recipeId,
                                                               @PathVariable Long ingredientId,
                                                               @RequestBody IngredientRequest ingredientRequest) {
        return ResponseEntity.ok().body(recipeService.updateIngredient(
                recipeId,
                ingredientId,
                ingredientRequest,
                userProvider.getAuthUserPrincipalId()));
    }

    @DeleteMapping("/{recipeId}/ingredient/{ingredientId}")
    public void deleteIngredient(@PathVariable UUID recipeId,
                                 @PathVariable Long ingredientId) {
        recipeService.deleteIngredient(recipeId, ingredientId, userProvider.getAuthUserPrincipalId());
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<IngredientResponse> addIngredient(@PathVariable UUID recipeId,
                                                            @RequestBody IngredientRequest ingredientRequest) {
        return ResponseEntity.ok().body(recipeService.addIngredient(
                recipeId,
                ingredientRequest,
                userProvider.getAuthUserPrincipalId()));
    }
}
