package com.rpsB.demo.service;

import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.dto.RecipeUpdateDto;
import com.rpsB.demo.entity.Ingredient;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.enums.Category;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.mapper.IngredientMapper;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recipe Service Unit Test")
class RecipeServiceTest {

    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private IngredientMapper ingredientMapper;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
    }


    @Test
    void createRecipe_shouldSetCreatorAndBindIngredients() {
        // GIVEN
        Long userId = 1L;

        RecipeRequest request = new RecipeRequest(
                "Recipe",
                "test recipe",
                List.of(new IngredientRequest(
                        "ingredient",
                        0.5,
                        "g",
                        "note",
                        4.4,
                        5.0,
                        8.9,
                        5.5
                )),
                45,
                Category.BREAKFAST
        );

        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        recipe.setIngredientList(List.of(ingredient));

        User user = User.builder()
                .id(userId)
                .build();
        RecipeResponse response = new RecipeResponse(
                1L,
                "Recipe",
                "test recipe",
                List.of(new IngredientResponse(
                        1L,
                        1L,
                        "recipe",
                        4.0,
                        "",
                        "",
                        1,
                        5.5,
                        4.4,
                        5.5,
                        8.8

                )),
                45,
                4,
                Category.BREAKFAST,
                1L,
                LocalDateTime.now()
        );

        when(recipeMapper.toEntity(request)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toDtoWithId(recipe,1L)).thenReturn(response);

        // WHEN
        RecipeResponse result = recipeService.createRecipe(request, userId);

        // THEN
        assertEquals(user.getId(), recipe.getCreator().getId());
        assertEquals(recipe, ingredient.getRecipe());
        verify(recipeRepository).save(recipe);
        verify(recipeMapper).toDtoWithId(recipe,1L);

        assertEquals(response, result);
    }


    @Test
    void updateRecipe_shouldUpdateFields() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(userId)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);

        RecipeUpdateDto dto = new RecipeUpdateDto(
                "New name",
                "New description",
                50,
                Category.DINNER
        );

        RecipeResponse response = new RecipeResponse(
                1L,
                "Recipe",
                "test recipe",
                List.of(new IngredientResponse(
                        1L,
                        1L,
                        "recipe",
                        4.0,
                        "",
                        "",
                        1,
                        5.5,
                        4.4,
                        5.5,
                        8.8

                )),
                45,
                4,
                Category.BREAKFAST,
                1L,
                LocalDateTime.now()
        );

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        when(recipeMapper.toDtoWithId(recipe,1L))
                .thenReturn(response);

        // WHEN
        RecipeResponse result =
                recipeService.updateRecipe(dto, recipeId, userId);

        // THEN
        assertEquals("New name", recipe.getName());
        assertEquals("New description", recipe.getDescription());
        assertEquals(50, recipe.getTimeToCookMinutes());
        assertEquals(Category.DINNER, recipe.getCategory());

        verify(recipeMapper).toDtoWithId(recipe,1L);
        assertEquals(response, result);
    }

    @Test
    void updateRecipe_shouldThrowIfNotFound() {

        Long recipeId = 1L;
        Long userId = 1L;

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> recipeService.updateRecipe(
                        new RecipeUpdateDto(null, null, 0, null),
                        recipeId,
                        userId
                ));
    }

    @Test
    void updateRecipe_shouldThrowIfNotOwner() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(2L)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        assertThrows(AppException.class,
                () -> recipeService.updateRecipe(
                        new RecipeUpdateDto("name", null, 0, null),
                        recipeId,
                        userId
                ));
    }

    @Test
    void getRecipeById_shouldReturnRecipe() {

        Long recipeId = 1L;

        Recipe recipe = new Recipe();
        RecipeResponse response = new RecipeResponse(
                1L,
                "Recipe",
                "test recipe",
                List.of(new IngredientResponse(
                        1L,
                        1L,
                        "recipe",
                        4.0,
                        "",
                        "",
                        1,
                        5.5,
                        4.4,
                        5.5,
                        8.8

                )),
                45,
                4,
                Category.BREAKFAST,
                1L,
                LocalDateTime.now()
        );

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        when(recipeMapper.toDto(recipe))
                .thenReturn(response);

        // WHEN
        RecipeResponse result = recipeService.getRecipeById(recipeId);

        // THEN
        assertEquals(response, result);

        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper).toDto(recipe);
    }

    @Test
    void getRecipeById_shouldThrowIfNotFound() {

        Long recipeId = 1L;

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> recipeService.getRecipeById(recipeId));

        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void getMyRecipes_shouldReturnPage() {

        Long userId = 1L;

        Recipe recipe = new Recipe();
        RecipeResponse response = new RecipeResponse(
                1L,
                "Recipe",
                "test recipe",
                List.of(new IngredientResponse(
                        1L,
                        1L,
                        "recipe",
                        4.0,
                        "",
                        "",
                        1,
                        5.5,
                        4.4,
                        5.5,
                        8.8

                )),
                45,
                4,
                Category.BREAKFAST,
                1L,
                LocalDateTime.now()
        );

        Page<Recipe> recipePage =
                new PageImpl<>(List.of(recipe));

        when(recipeRepository.findAllUserRecipe(eq(userId), any()))
                .thenReturn(recipePage);

        when(recipeMapper.toDto(recipe))
                .thenReturn(response);

        // WHEN
        Page<RecipeResponse> result =
                recipeService.getMyRecipes(0, 10, userId);

        // THEN
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));

        verify(recipeMapper).toDto(recipe);
    }

    @Test
    void getMyRecipes_shouldReturnEmptyPage() {

        Long userId = 1L;

        when(recipeRepository.findAllUserRecipe(eq(userId), any()))
                .thenReturn(Page.empty());

        // WHEN
        Page<RecipeResponse> result =
                recipeService.getMyRecipes(0, 10, userId);

        // THEN
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void deleteRecipeById_shouldDeleteIfOwner() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(userId)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        // WHEN
        recipeService.deleteRecipeById(recipeId, userId);

        // THEN
        verify(recipeRepository).deleteById(recipeId);
    }

    @Test
    void deleteRecipeById_shouldNotDeleteIfNotOwner() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(2L)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        // WHEN
        recipeService.deleteRecipeById(recipeId, userId);

        // THEN
        verify(recipeRepository, never()).deleteById(recipeId);
    }

    @Test
    void updateIngredient_shouldThrowIfNotOwner() {

       // UUID recipeId = UUID.randomUUID();

        User creator = User.builder()
                .id(2L)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        assertThrows(AppException.class,
                () -> recipeService.updateIngredient(
                        1L,
                        1L,
                        new IngredientRequest("", 1.1, "", "", 0.0,
                                0.0, 0.0, 0.0),
                        1L
                ));
    }

    @Test
    void updateIngredient_shouldThrowIfIngredientNotFound() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(userId)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);
        recipe.setIngredientList(List.of()); // пустой список

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        assertThrows(AppException.class,
                () -> recipeService.updateIngredient(
                        recipeId,
                        99L,
                        new IngredientRequest("", 1.1, "", "", 0.0,
                                0.0, 0.0, 0.0),
                        userId
                ));
    }

    @Test
    void updateIngredient_shouldUpdateFields() {

        Long recipeId = 1L;
        Long userId = 1L;
        Long ingredientId = 10L;

        User creator = User.builder()
                .id(userId)
                .build();

        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);
        recipe.setIngredientList(List.of(ingredient));

        IngredientRequest request = new IngredientRequest(
                "Sugar",
                100.0,
                "g",
                "note",
                400.0,
                0.0,
                100.0,
                100.0
        );

        IngredientResponse response = new IngredientResponse(
                1L,
                1L,
                "Sugar",
                100.0,
                "g",
                "note",
                4,
                0.0,
                100.0,
                100.0,
                100.0
        );

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        when(ingredientMapper.toDto(ingredient))
                .thenReturn(response);

        // WHEN
        IngredientResponse result =
                recipeService.updateIngredient(
                        recipeId,
                        ingredientId,
                        request,
                        userId
                );

        // THEN
        assertEquals("Sugar", ingredient.getName());
        assertEquals(100.0, ingredient.getAmount());
        assertEquals("g", ingredient.getUnit());
        assertEquals("note", ingredient.getNote());
        assertEquals(400.0, ingredient.getCalories());
        assertEquals(100.0, ingredient.getFats());
        assertEquals(100.0, ingredient.getCarbs());

        verify(ingredientMapper).toDto(ingredient);
        assertEquals(response, result);
    }

    @Test
    void deleteIngredient_shouldRemoveIngredient() {

        Long recipeId = 1L;
        Long userId = 1L;
        Long ingredientId = 10L;

        User creator = User.builder()
                .id(userId)
                .build();

        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);
        recipe.setIngredientList(ingredients);

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        // WHEN
        recipeService.deleteIngredient(recipeId, ingredientId, userId);

        // THEN
        assertTrue(recipe.getIngredientList().isEmpty());
    }

    @Test
    void addIngredient_shouldAddIngredient() {

        Long recipeId = 1L;
        Long userId = 1L;

        User creator = User.builder()
                .id(userId)
                .build();

        Recipe recipe = new Recipe();
        recipe.setCreator(creator);
        recipe.setIngredientList(new ArrayList<>());

        Ingredient ingredient = new Ingredient();

        IngredientRequest request = new IngredientRequest(
                "Sugar",
                100.0,
                "g",
                "note",
                400.0,
                0.0,
                100.0,
                100.0
        );

        IngredientResponse response = new IngredientResponse(
                1L,
                1L,
                "Sugar",
                100.0,
                "g",
                "note",
                4,
                0.0,
                100.0,
                100.0,
                100.0
        );

        when(recipeRepository.findById(recipeId))
                .thenReturn(Optional.of(recipe));

        when(ingredientMapper.toEntity(request))
                .thenReturn(ingredient);

        when(ingredientMapper.toDto(ingredient))
                .thenReturn(response);

        // WHEN
        IngredientResponse result =
                recipeService.addIngredient(recipeId, request, userId);

        // THEN
        assertEquals(1, recipe.getIngredientList().size());
        assertTrue(recipe.getIngredientList().contains(ingredient));

        verify(ingredientMapper).toEntity(request);
        verify(ingredientMapper).toDto(ingredient);

        assertEquals(response, result);
    }
}