package com.rpsB.demo.repository;

import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("""
            SELECT r FROM Recipe r 
            WHERE r.creator.id = :userId           
            """)
    Page<Recipe> findAllUserRecipe(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM recipes WHERE uuid = ANY(:uuids)", nativeQuery = true)
    List<Recipe> findByUuids(@Param("uuids") UUID[] uuids);

    @Query("""
            SELECT r FROM Recipe  r
            WHERE r.status = 'PENDING'            
            """)
    List<Recipe> findByStatusPending();

    @Modifying
    @Query("""
            UPDATE Recipe r 
            SET r.status = :status
            WHERE r.uuid = :uuid                               
            """)
    int updateStaus(@Param("uuid") UUID uuid,
                    @Param("status") SendStatus status);
}

