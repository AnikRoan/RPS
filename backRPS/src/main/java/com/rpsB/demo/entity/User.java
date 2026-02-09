package com.rpsB.demo.entity;

import com.rpsB.demo.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    Long id;

    @Getter
    private String name;
    @Column(nullable = false, unique = true)
    @Getter
    private String email;
    @Column(nullable = false)
    @Getter
    private String password;

    @Getter
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Getter
    private Role role;

    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Recipe> recipes = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    @Getter
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Getter
    private LocalDateTime updatedAt;


    public void changeName(String name) {
        this.name = name;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeRole(Role role) {
        if (role == Role.ADMIN) {
            throw new AccessDeniedException("Cannot promote...user");
        }
        this.role = role;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
        recipe.setCreator(this);
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        recipe.setCreator(null);
    }

    public List<Recipe> getRecipes() {
        return List.copyOf(recipes);
    }


}
