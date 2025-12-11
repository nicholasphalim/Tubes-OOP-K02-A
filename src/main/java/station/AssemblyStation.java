package station;

import entity.Chef;
import ingredient.Dough;
import ingredient.Ingredient;
import ingredient.State;
import inventory.Plate;
import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;
// import inventory.Preparable

public class AssemblyStation extends Station {
    private List<Preparable> ingredients;
    private Plate plate;

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
        if (!(item instanceof Ingredient) && !(item instanceof Dish) && !(item instanceof Plate)) {
            return false;
        }

        int incomingSize = (item instanceof Dish) ? ((Dish) item).getComponents().size() : 1;
        if (ingredients.size() + incomingSize > 5) {
            return false;
        }

        if (ingredients.isEmpty()) {
            return true;
        } else {
            if (item instanceof Ingredient) {
                if (((Ingredient) item).getState() == State.RAW) {
                    return false;
                }
            }

            if (item instanceof Plate) {
                return true;
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

        if (item instanceof Plate) {
            if (ingredients.isEmpty()){
                gp.ui.showMessage("You placed " + item.name);
            }
            plate = (Plate) item;
            if (plate.dish != null) {
                Dish dish = (Dish) plate.dish;
                placeItem(dish);
            }
            return true;
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

        reorderIngredients();
        return true;
    }

    @Override
    public Item takeItem() {
        if (ingredients.isEmpty()) {
            if (plate != null) {
                Plate temp = plate;
                plate = null;
                gp.ui.showMessage("Picked up " + temp.name);
                return temp;
            }
            return null;
        }

        if(ingredients.size() == 1) {
            Ingredient temp  = (Ingredient) ingredients.get(0);
            ingredients.clear();
            gp.ui.showMessage("Picked up " + temp.name);
            return temp;
        } else {
            List<Preparable> componentsForDish = new ArrayList<>(ingredients);

            Dish newDish = new Dish(componentsForDish, gp);

            ingredients.clear();

            if (plate != null) {
                Plate temp = plate;
                temp.dish = newDish;
                plate = null;
                gp.ui.showMessage("Picked up Plate + " + newDish.getDishName());

                return temp;
            }

            gp.ui.showMessage("Picked up " + newDish.getDishName());
            return newDish;
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
            gp.ui.showMessage("Empty Station");
        } else {
            StringBuilder sb = new StringBuilder("Station: ");
            for (Preparable p : ingredients) {
                sb.append(((Item)p).name).append(" ");
            }
            gp.ui.showMessage(sb.toString());
        }
    }

    private void reorderIngredients() {
        if (ingredients.isEmpty()) return;

        Preparable dough = null;

        for (Preparable p : ingredients) {
            if (p instanceof Ingredient) {
                Ingredient ing = (Ingredient) p;
                if (ing instanceof Dough) {
                    dough = p;
                    break;
                }
            }
        }

        if (dough != null) {
            ingredients.remove(dough);
            ingredients.add(0, dough);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        if (plate != null && plate.image != null) {
            int margin = 4;
            int plateSize = gp.tileSize - (margin * 2);
            g2.drawImage(plate.image, x + margin, y + margin, plateSize, plateSize, null);
        }

        if (!ingredients.isEmpty()) {
            int stackOffset = 0;

            for (Preparable p : ingredients) {
                Item item = (Item) p;

                if (item.image != null) {
                    int itemMargin = 10;
                    int itemSize = gp.tileSize - (itemMargin * 2);

                    int drawX = x + itemMargin;
                    int drawY = y + itemMargin - stackOffset;

                    if (plate != null) {
                        drawY -= 5;
                    }

                    g2.drawImage(item.image, drawX, drawY, itemSize, itemSize, null);

                    stackOffset += 6;
                }
            }
        }
    }
}