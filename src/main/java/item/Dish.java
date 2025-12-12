package item;

import ingredient.Ingredient;
import ingredient.State;
import main.GamePanel;
import preparable.Preparable;
import recipe.Recipe;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dish extends Item {
    private Set<Preparable> components;
    private boolean isCooked;
    private boolean isBurned =  false;

    public Dish(Set<Preparable> components, GamePanel gp) {
        super(gp);
        this.components = components;
        this.name = generateName();
        this.isCooked = checkAllCooked();
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Dough_COOKED.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateName() {
        if (components == null || components.isEmpty()) {
            return "Empty Dish";
        }

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Preparable p : components) {
            Item item = (Item) p;
            sb.append(item.name);

            if (count < components.size() - 1) {
                sb.append(" + ");
            }
            count++;
        }
        return sb.toString();
    }

    public String getDishName(){
        return name;
    }

    public Set<Preparable> getComponents(){
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
        if(isCooked){
            for (Preparable p : components) {
                Ingredient ing = (Ingredient) p;
                ing.changeState(State.COOKED);
                ing.updateImage();
            }
        }

    }

    public void setBurned(boolean burned) {
        this.isBurned = burned;
    }

    public boolean isBurned() {
        return isBurned;
    }

    public boolean isDone(Recipe recipe) {
        if (recipe == null) return false;

        if (components.size() != recipe.size()) return false;

        java.util.List<Ingredient> remaining = new java.util.ArrayList<>();
        for (Preparable p : components) {
            if (!(p instanceof Ingredient)) return false;
            remaining.add((Ingredient) p);
        }

        for (Map.Entry<Ingredient, State> entry : recipe.getIngredientRequirements().entrySet()) {
            String reqName = entry.getKey().getName();
            State reqState = entry.getValue();
//            System.out.println(reqName + " " + reqState);

            int foundIndex = -1;
            for (int i = 0; i < remaining.size(); i++) {
                Ingredient ing = remaining.get(i);
//                System.out.println(ing.getIngName() + " " + ing.getState());
                if (reqName.equals(ing.getIngName()) && ing.getState() == reqState) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex == -1) return false;
            remaining.remove(foundIndex);
        }

        return remaining.isEmpty();

        
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

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {

        if (getComponents() != null && !getComponents().isEmpty()) {
            int offset = 0;
            int margin = 4;
            int size = gp.tileSize - (margin * 2);

            for (Preparable p : getComponents()) {
                Item item = (Item) p;

                if (item.image != null) {
                    g2.drawImage(item.image, x + margin, y + margin - offset, size, size, null);

                    offset += 2;
                }
            }
        } else {
            super.draw(g2, gp);
        }
    }

}
