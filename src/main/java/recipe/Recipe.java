package recipe;

import java.util.ArrayList;
import java.util.List;
import ingredient.State;

public class Recipe {
    private String name;
    private List<String> ingredientNames;
    private List<State> ingredientStates;

    public Recipe(String name) {
        this.name = name;
        this.ingredientNames = new ArrayList<>();
        this.ingredientStates = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addIngredientRequirement(String ingredientName, State requiredState) {
        ingredientNames.add(ingredientName);
        ingredientStates.add(requiredState);
    }

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public List<State> getIngredientStates() {
        return ingredientStates;
    }

    public int size() {
        return ingredientNames.size();
    }
}
