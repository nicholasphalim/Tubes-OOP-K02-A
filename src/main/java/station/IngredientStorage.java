package station;

import ingredient.*;
import item.*;
import entity.*;
import main.GamePanel;
import object.SuperObject;
import inventory.Plate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class IngredientStorage extends Station {
    private String ingredientName;
    private Ingredient ingredientItem;

    //instantiate with specific ingredient
    public IngredientStorage(GamePanel gp, Ingredient ingredient) {
        super(gp);
        name = "Ingredient Storage";
        this.ingredientItem = ingredient;
        this.ingredientName = ingredient.getName();
    }

    public Ingredient getIngredientItem() {
        gp.ui.showMessage("Took " + this.ingredientName + " from storage.");
        return this.ingredientItem.copy();
    }

    public String getIngredientName() {
        return this.ingredientName;
    }

    @Override
    public boolean canAccept(SuperObject item) {
        return itemOnStation == null && item != null;
    } //bagaimana kalau plating?

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            if (this.itemOnStation != null) {
                chef.setInventory(this.takeItem());
                gp.ui.showMessage("You took the item on storage.");
                return;
            } else {
                chef.setInventory(this.getIngredientItem());
                return;
            }
        } else {
            chef.pickDrop();
        }
    }
}
