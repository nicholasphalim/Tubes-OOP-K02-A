package item;

import java.util.List;

import ingredient.Ingredient;
import ingredient.State;
import preparable.Preparable;
import recipe.Recipe;

public class Dish extends Item {
    private String name;
    private List<Preparable> components;
    private boolean isCooked;

    public Dish(String name, List<Preparable> components, int x, int y) {
        super(x, y);
        this.name = name;
        this.components = components;
        this.isCooked = checkAllCooked();
    }

    public String getName(){
        return name;
    }

    public List<Preparable> getComponents(){
        return components;
    }

    public boolean isCooked(){
        return isCooked;
    }

    private boolean checkAllCooked(){
        for (Preparable p : components) {
            if (!p.canBePlacedOnPlate()) {
                return false;
            }
        }
        return true;
    }

    public boolean isDone(Recipe recipe) {
        if (recipe == null) return false;

        List<String> requiredNames = recipe.getIngredientNames();
        List<State> requiredStates = recipe.getIngredientStates();

        
        if (components.size() != requiredNames.size()) return false;

        
        for (int i = 0; i < components.size(); i++) {

            Preparable p = components.get(i);

            
            if (!(p instanceof Ingredient)) return false;

            Ingredient ing = (Ingredient) p;

            
            if (!ing.getName().equals(requiredNames.get(i))) return false;
            if (ing.getState() != requiredStates.get(i)) return false;
        }

        return true;
    }

}
