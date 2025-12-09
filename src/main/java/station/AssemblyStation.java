package station;

import entity.Chef;
import ingredient.Ingredient;
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

    @Override
    public boolean placeItem(Item item) {
        if (item instanceof Dish) {
            Dish dish = (Dish) item;
            List<Preparable> contents = dish.getComponents();
            if (ingredients.size() + contents.size() > 5) {
                gp.ui.showMessage("Station is too full!");
                return false;
            }

            ingredients.addAll(contents);

            if (!ingredients.isEmpty()) {
                this.itemOnStation = (Item) ingredients.get(ingredients.size() - 1);
            }

            gp.ui.showMessage("Added contents of " + item.name);
            return true;
        }

        else if (item instanceof Preparable) {

            if (ingredients.size() >= 5) {
                gp.ui.showMessage("Station is full!");
                return false;
            }

            ingredients.add((Preparable) item);
            gp.ui.showMessage("Added " + item.name);

            this.itemOnStation = item;
            return true;
        }

        else {
            gp.ui.showMessage("Cannot place this item here!");
        }
        return false;
    }

    @Override
    public Item takeItem() {
        if (ingredients.isEmpty()) {
            return null;
        }

        List<Preparable> componentsForDish = new ArrayList<>(ingredients);

        Dish newDish = new Dish(componentsForDish, gp);

        ingredients.clear();
        this.itemOnStation = null;

        gp.ui.showMessage("Picked up " + newDish.getDishName());
        return newDish;
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