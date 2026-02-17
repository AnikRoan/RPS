package com.rpsB.demo.entity;

import com.rpsB.demo.enums.Category;
import com.rpsB.demo.enums.SendStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "recipes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
//@DynamicUpdate optimize queri
public class Recipe {
    @Id
    @GeneratedValue
    private UUID uuid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 2000)
    private String description;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<Ingredient> ingredientList = new ArrayList<>();
    private int timeToCookMinutes;
    private int averageVote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Enumerated(EnumType.STRING)
    private SendStatus status = SendStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private Category category;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;
    @UpdateTimestamp
    private LocalDateTime updated_at;

}
