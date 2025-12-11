package inventory;

import main.GamePanel;
import preparable.Preparable;
import item.Item;
import item.Dish;

import java.util.ArrayList;

public class Plate extends KitchenUtensils {
    private boolean isClean;

    public Plate(GamePanel gp) {
        super(gp);
        this.isClean = true;
    }

    public boolean isPortable() {
        return true;
    }

    public int capacity() {
        return 1; 
    }

    public boolean canAccept(Item ingredient) {
        return isClean; 
    }

    public void addIngredient(Item ingredient) {
        if (canAccept(ingredient)) {
            this.contents.add(ingredient);
        }
    }

    public void clean() {
        this.isClean = true;
        clearContents();
    }

    public void makeDirty() {
        this.isClean = false;
        clearContents();
    }

    public void clearContents() {
        this.contents.clear();
    }
}