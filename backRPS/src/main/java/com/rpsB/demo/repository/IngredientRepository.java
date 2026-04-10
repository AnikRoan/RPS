package com.rpsB.demo.repository;

import com.rpsB.demo.dto.IngredientFloatDto;
import com.rpsB.demo.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query(
            """
                    SELECT new com.rpsB.demo.dto.IngredientFloatDto(
                                        i.id,
                                        i.recipe.uuid,
                                        i.name
                                )
                    FROM Ingredient  i
                    WHERE i.recipe.uuid IN :recipeIds                                         
                    """
    )
    List<IngredientFloatDto> findByRecipeId(List<Long> recipeIds);
}
