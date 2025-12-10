package station;

import ingredient.Ingredient;
import item.Item;
import entity.Chef;
import main.GamePanel;
import object.SuperObject;

public class IngredientStorage extends Station {
    private String ingredientName;
    private Ingredient ingredientItem;

    //instansiasi ingredient terlebih dahulu
    public IngredientStorage(Ingredient ingredient) {
        //super gp?
        this.ingredientItem = ingredient;
        this.ingredientName = ingredient.getName();
    }

    public Ingredient getIngredientItem() {
        return this.ingredientItem.clone();
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
