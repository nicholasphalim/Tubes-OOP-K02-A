package station;

import entity.Chef;
import ingredient.Ingredient;
import ingredient.State;
import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import inventory.Preparable

public class AssemblyStation extends Station {
    private List<Preparable> ingredients;

    public AssemblyStation(GamePanel gp) {
        super(gp);
        name = "Assembly Station";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/tile163.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        ingredients = new ArrayList<>();
    }

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    public boolean canAccept(Item item) {
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
    }

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
            return null;
        }

        if(ingredients.size() == 1) {
            Ingredient temp  = (Ingredient) ingredients.get(0);
            ingredients.clear();
            this.itemOnStation = null;
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
        if (ingredients.isEmpty()) {
            gp.ui.showMessage("Empty Plate");
        } else {
            StringBuilder sb = new StringBuilder("Plate: ");
            for (Preparable p : ingredients) {
                sb.append(((Item)p).name).append(" ");
            }
            gp.ui.showMessage(sb.toString());
        }
    }
}