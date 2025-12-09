package item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ingredient.Ingredient;
import ingredient.State;
import main.GamePanel;
import preparable.Preparable;
import recipe.Recipe;

import javax.imageio.ImageIO;

public class Dish extends Item {
    private List<Preparable> components;
    private boolean isCooked;

    public Dish(List<Preparable> components, GamePanel gp) {
        super(gp);
        this.components = components;
        this.name = generateName();
        this.isCooked = checkAllCooked();
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/dough.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateName() {
        if (components == null || components.isEmpty()) {
            return "Empty Dish";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            Item item = (Item) components.get(i);
            sb.append(item.name);

            if (i < components.size() - 1) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    public String getDishName(){
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

    public void setCooked(boolean b){
        isCooked = b;
    }

    public boolean isDone(Recipe recipe) {
        if (recipe == null) return false;

        if (components.size() != recipe.size()) return false;

        List<String> requiredNames = new ArrayList<>(recipe.getIngredientNames());
        List<State> requiredStates = new ArrayList<>(recipe.getIngredientStates());

        for (Preparable p : components) {

            if (!(p instanceof Ingredient)) return false;
            Ingredient ing = (Ingredient) p;

            String name = ing.getName();
            State state = ing.getState();

            int foundIndex = -1;
            for (int i = 0; i < requiredNames.size(); i++) {
                if (name.equals(requiredNames.get(i)) && state == requiredStates.get(i)) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex == -1) return false;

            requiredNames.remove(foundIndex);
            requiredStates.remove(foundIndex);
        }

        return requiredNames.isEmpty();

        
        // if (recipe == null) return false;
        // List<String> requiredNames = recipe.getIngredientNames();
        // List<State> requiredStates = recipe.getIngredientStates();

        // if (components.size() != recipe.size()) return false;
        
        // for (int i = 0; i < components.size(); i++) {

        //     Preparable p = components.get(i);

        //     if (!(p instanceof Ingredient)) return false;

        //     Ingredient ing = (Ingredient) p;

        //     if (!ing.getName().equals(requiredNames.get(i))) return false;
        //     if (ing.getState() != requiredStates.get(i)) return false;
        // }

        // return true;
    }

}
