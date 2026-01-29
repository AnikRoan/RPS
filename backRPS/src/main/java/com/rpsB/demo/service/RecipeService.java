package com.rpsB.demo.service;

import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
