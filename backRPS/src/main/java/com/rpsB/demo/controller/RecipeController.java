package com.rpsB.demo.controller;


import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.repository.UserRepository;
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
    private final UserRepository userRepository;
    User user = userRepository.findById(1L).orElseThrow();

    @PostMapping()
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody RecipeRequest recipeRequest) {

        return ResponseEntity.ok().body(recipeService.createRecipe(recipeRequest, user));
    }

    @PutMapping()
    public ResponseEntity<RecipeResponse> updateRecipe(RecipeUpdateDto updateDto, UUID recipeId) {
        return ResponseEntity.ok().body(recipeService.updateRecipe(updateDto, recipeId, user));
    }

    @GetMapping("/id")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(recipeService.getRecipeById(id, user));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<RecipeResponse>> getUserRecipes(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(recipeService.getRecipes(user, page, size));
    }

    @DeleteMapping("/id")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") UUID id) {
        recipeService.deliteRecipeById(id, user);
        return ResponseEntity.ok().body(id);
    }


}
