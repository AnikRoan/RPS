package com.rpsB.demo.controller;


import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.repository.UserRepository;
import com.rpsB.demo.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserRepository userRepository;

    @PostMapping()
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody RecipeRequest recipeRequest){
       User user = userRepository.findById(1L).orElseThrow();

        return ResponseEntity.ok().body(recipeService.createRecipe(recipeRequest, user));
    }



}
