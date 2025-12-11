package recipe;

import java.util.HashMap;
import java.util.Map;

import ingredient.Ingredient;
import ingredient.State;

public class Recipe {
    private String name;
    private Map<Ingredient, State> ingredientRequirements;

    public Recipe(String name) {
        this.name = name;
        this.ingredientRequirements = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addIngredientRequirement(Ingredient ingredient, State requiredState) {
        ingredientRequirements.put(ingredient, requiredState);
    }

    public Map<Ingredient, State> getIngredientRequirements() {
        return ingredientRequirements;
    }

    public int size() {
        return ingredientRequirements.size();
    }
}