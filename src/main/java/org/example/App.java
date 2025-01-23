package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.Arrays;

public class App {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JpaExampleUnit");
        EntityManager em = emf.createEntityManager();

        // Create a meal
        Meal meal = new Meal();
        meal.setName("Pizza");

        // Create ingredients
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Cheese");
        ingredient1.setMeal(meal);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Tomato Sauce");
        ingredient2.setMeal(meal);

        // Set ingredients to meal
        meal.setIngredients(Arrays.asList(ingredient1, ingredient2));

        // Persist meal and ingredients
        em.getTransaction().begin();
        em.persist(meal);  // Meal will automatically persist ingredients due to cascading
        em.getTransaction().commit();

        // Fetch all meals
        TypedQuery<Meal> query = em.createQuery("SELECT m FROM Meal m", Meal.class);
        for (Meal m : query.getResultList()) {
            System.out.println("Meal Name: " + m.getName());
            for (Ingredient ing : m.getIngredients()) {
                System.out.println("  Ingredient: " + ing.getName());
            }
        }

        em.close();
        emf.close();
    }
}