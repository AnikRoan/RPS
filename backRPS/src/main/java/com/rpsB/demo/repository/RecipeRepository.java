package com.rpsB.demo.repository;

import com.rpsB.demo.dto.RecipeShort;
import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("""
            SELECT r FROM Recipe r 
            WHERE r.creator.id = :userId           
            """)
    Page<Recipe> findAllUserRecipe(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT new com.rpsB.demo.dto.RecipeShort(
                        r.uuid,
                        r.name,
                        r.description, 
                        r.timeToCookMinutes,
                        r.averageVote,
                        r.category,
                        r.creator.id,
                        r.created_at                                                
                                                )
            FROM Recipe r 
            WHERE r.uuid IN :uuids
            """)
    List<RecipeShort> findByLockedUuids(@Param("uuids")List<UUID> uuids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Recipe 
            SET status = :sendStatus
            WHERE uuid IN :uuids                        
            """)
    void updateStatus(@Param("uuids") List<UUID> uuids,
                      @Param("sendStatus") SendStatus sendStatus);

    @Query(value = """
            SELECT uuid
            FROM recipes
            WHERE status = 'PENDING'
            AND updated_at < now() - interval '5 minutes'
            ORDER BY updated_at ASC
            FOR UPDATE SKIP LOCKED
            LIMIT 100
            """, nativeQuery = true)
    List<UUID> lockPendingIds();

    @Modifying
    @Query(value = """
        UPDATE recipe
        SET status = 'PENDING'
        WHERE status = 'SENDING'
        AND updated_at < now() - interval '5 minutes'
        """,
            nativeQuery = true)
    int recoverStuckSending();

    @Query("""
            SELECT r 
            FROM Recipe r 
            WHERE r.uuid IN :recipeIds             
            """)
    Optional<List<Recipe>> findByRecipeIds(List<UUID> recipeIds);
}
