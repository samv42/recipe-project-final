package com.lab.recipeproject;

import lombok.*;
import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private Integer rating;

    @NotNull
    private String description;

    public void setRating(Integer rating) throws IllegalStateException{
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Rating must be between 0 and 10.");
        }
        this.rating = rating;
    }

    public void validate() throws IllegalStateException {
        if (username == null) {
            throw new IllegalStateException("You have to have a username for your review!");
        } else if (rating == null) {
            throw new IllegalStateException("You have to have a rating for your review!");
        } else if (description == null) {
            throw new IllegalStateException("You have to include a description for your review!");
        }
    }


}
