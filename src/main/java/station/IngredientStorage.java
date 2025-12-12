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

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class IngredientStorage extends Station {
    private final String ingredientName;
    private final Ingredient ingredientItem;
    private final Set<Preparable> ingredients;

    private Plate plate;

    public IngredientStorage(GamePanel gp, Ingredient ingredient) {
        super(gp);
        name = "Ingredient Storage";
        this.ingredientItem = ingredient;
        this.ingredientName = ingredient.getName();
        ingredients = new HashSet<>();
    }

    public Ingredient getIngredientItem() throws CloneNotSupportedException {
        gp.ui.showMessage("Took " + this.ingredientName + " from storage.");
        return this.ingredientItem.clone();
    }

    public String getIngredientName() {
        return this.ingredientName;
    }

    @Override
    public boolean canAccept(Item item) {
        if (!(item instanceof Ingredient) && !(item instanceof Dish) && !(item instanceof Plate)) {
            return false;
        }

        if (this.plate != null && !this.plate.isClean) {
            return false;
        }

        if (item instanceof Plate p) {
            return p.isClean || ingredients.isEmpty();
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
        if (this.plate != null && !this.plate.isClean) {
            gp.ui.showMessage("Plate is dirty!");
            return false;
        }
        if (item instanceof Plate && !((Plate)item).isClean && !ingredients.isEmpty()) {
            gp.ui.showMessage("Cannot put dirty plate on food!");
            return false;
        }
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
                placeItem(plate.dish);
            }
            return true;
        }
        if (item instanceof Dish dish) {
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
            else {
                try {
                    gp.ui.showMessage("Picked up ingredient " + ingredientName);
                    return getIngredientItem();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(ingredients.size() == 1) {
            Ingredient temp  = (Ingredient) ingredients.iterator().next();
            ingredients.clear();
            gp.ui.showMessage("Picked up " + temp.name);
            return temp;
        } else {
            Set<Preparable> componentsForDish = new HashSet<>(ingredients);

            Dish newDish = new Dish(componentsForDish, gp);

            ingredients.clear();
            this.itemOnStation = null;

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

    private void reorderIngredients() {
        if (ingredients.isEmpty()) return;

        Preparable dough = null;

        for (Preparable p : ingredients) {
            if (p instanceof Ingredient ing && ing instanceof Dough) {
                dough = p;
                break;
            }
        }

    }


    public Set<Preparable> getIngredients() {
        return ingredients;
    }

    public void clearIngredients() {
        ingredients.clear();
    }

    @Override
    public void interact(Chef chef) {
        gp.ui.showMessage("Ini adalah ingredient storage " + ingredientName);
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        g2.drawImage(ingredientItem.image, x+gp.tileSize/4, y+gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, null);

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
                    int itemMargin = 4;
                    int itemSize = gp.tileSize - (itemMargin * 2);

                    int drawX = x + itemMargin;
                    int drawY = y + itemMargin - stackOffset;

                    if (plate != null) {
                        drawY -= 5;
                    }

                    g2.drawImage(item.image, drawX, drawY, itemSize, itemSize, null);

                    stackOffset += 2;
                }
            }
        }
    }
}
