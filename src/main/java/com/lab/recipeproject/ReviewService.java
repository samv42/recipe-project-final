package com.lab.recipeproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewService {
    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);
        Review reviewCheck = review.orElse(null);
        if (reviewCheck == null) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }
        return reviews;
    }

    public int getAverageReviewRating(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        ArrayList<Review> reviews = new ArrayList<>(getReviewByRecipeId(recipeId));
        int average = 0;
        int count = 0;
        for(Review review : reviews){
            average = average + review.getRating();
            count++;
        }
        average = average/count;
        return average;
    }

    public List<Recipe> getRecipesByMinimumAverageRating(int rating) throws NoSuchRecipeException {
        List<Recipe> recipes = recipeService.getAllRecipes();
        List <Recipe> recipesByRating = new ArrayList<>();
        int averageRating = 0;
        boolean hasReviews;
        for(Recipe recipe : recipes){
            try {
                averageRating = getAverageReviewRating(recipe.getId());
                hasReviews = true;
            }catch (NoSuchReviewException e) {
                System.out.println("There are no reviews for this recipe.");
                hasReviews = false;
            }
            if(averageRating <= rating & hasReviews){
                recipesByRating.add(recipe);
            }
        }

        return recipesByRating;
    }

    public List<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findByUsername(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }

        return reviews;
    }

    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException, IllegalStateException {
        review.validate();
        review.setRating(review.getRating());
        Recipe recipe = recipeService.getRecipeById(recipeId);
        recipe.getReviews().add(review);
        recipeService.updateRecipe(recipe, false);
        return recipe;
    }

    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException("The review you are trying to update. Maybe you meant to create one? If not," +
                    "please double check the ID you passed in.");
        }
        reviewRepo.save(reviewToUpdate);
        return reviewToUpdate;
    }
}
