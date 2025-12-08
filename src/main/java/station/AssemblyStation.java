package station;

import java.util.ArrayList;
import java.util.List;
// import inventory.Preparable

public class AssemblyStation extends Station {
    private List<Preparable> ingredients;

    public AssemblyStation() {
        this.ingredients = new ArrayList<>();
    }

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    public boolean addIngredient(Preparable ingredient) {
        return ingredients.add(ingredient);
    }

    @Override
    public void interact(Chef chef) {
    }
}