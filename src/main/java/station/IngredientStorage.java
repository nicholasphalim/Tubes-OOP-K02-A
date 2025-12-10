package station;

import ingredient.Ingredient;
import ingredient.State;
import item.Dish;
import item.Item;
import entity.Chef;
import main.GamePanel;
import object.SuperObject;
import preparable.Preparable;

import java.util.ArrayList;
import java.util.List;

public class IngredientStorage extends Station {
    private String ingredientName;
    private Ingredient ingredientItem;
    private List<Preparable> ingredients;

    //instansiasi ingredient terlebih dahulu
    public IngredientStorage(GamePanel gp, Ingredient ingredient) {
        super(gp);
        this.ingredientItem = ingredient;
        this.ingredientName = ingredient.getName();
        ingredients = new ArrayList<>();
    }

    public Ingredient getIngredientItem() throws CloneNotSupportedException {
        try {
            Ingredient newIng = this.ingredientItem.clone();
            newIng.changeState(State.RAW);
            return newIng;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getIngredientName() {
        return this.ingredientName;
    }

    @Override
    public boolean canAccept(SuperObject item) {
        if (!(item instanceof Ingredient) && !(item instanceof Dish)) {
            return false;
        }

        int incomingSize = (item instanceof Dish) ? ((Dish) item).getComponents().size() : 1;
        if (ingredients.size() + incomingSize > 5) {
            return false;
        }

        if (ingredients.isEmpty()) {
            return true;
        }

        else {
            if (item instanceof Ingredient) {
                if (((Ingredient) item).getState() == State.RAW) {
                    return false;
                }
            }

            for (Preparable p : ingredients) {
                if (p instanceof Ingredient) {
                    if (((Ingredient) p).getState() == State.RAW) {
                        return false; // Ditolak
                    }
                }
            }

            return true;
        }
    } //bagaimana kalau plating?


    @Override
    public boolean placeItem(Item item) {
        if (!canAccept(item)) {
            if (ingredients.size() >= 5) {
                gp.ui.showMessage("Station is full!");
            } else if (!ingredients.isEmpty()) {
                gp.ui.showMessage("Cannot assemble RAW items!");
            } else {
                gp.ui.showMessage("Cannot place this item!");
            }
            return false;
        }


        if (item instanceof Dish) {
            Dish dish = (Dish) item;
            ingredients.addAll(dish.getComponents());
            gp.ui.showMessage("Added contents of " + item.name);
        }
        else if (item instanceof Ingredient) {
            ingredients.add((Preparable) item);
            gp.ui.showMessage("Added " + item.name);
        }

        // Update Visual Item terakhir
        if (!ingredients.isEmpty()) {
            this.itemOnStation = (Item) ingredients.get(ingredients.size() - 1);
        }

        return true;
    }

    @Override
    public Item takeItem() {
        if (ingredients.isEmpty()) {
            try {
                gp.ui.showMessage("Picked up ingredient " + ingredientName);
                return getIngredientItem();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        if(ingredients.size() == 1) {
            Ingredient temp  = (Ingredient) ingredients.get(0);
            ingredients.clear();
            this.itemOnStation = null;
            gp.ui.showMessage("Picked up " + temp.name);
            return temp;
        } else {
            List<Preparable> componentsForDish = new ArrayList<>(ingredients);

            Dish newDish = new Dish(componentsForDish, gp);

            ingredients.clear();
            this.itemOnStation = null;

            gp.ui.showMessage("Picked up " + newDish.getDishName());
            return newDish;
        }
    }

    public void showListIngredients() {
        String temp = "";
        for (Preparable ingredient : ingredients) {
            Ingredient ing =  (Ingredient) ingredient;
            temp.concat(ing.getName() + " ");
        }
    }

    public List<Preparable> getIngredients() {
        return ingredients;
    }

    public void clearIngredients() {
        ingredients.clear();
    }

    @Override
    public void interact(Chef chef) {
        gp.ui.showMessage("Ini adalah ingredient storage " + ingredientName);
    }
}
