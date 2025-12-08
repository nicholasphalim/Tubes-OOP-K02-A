package inventory;

import java.util.ArrayList;

public class Plate extends KitchenUtensils {
    private boolean isClean;

    public Plate() {
        super();
        this.isClean = true;
    }

    public boolean isPortable() {
        return true;
    }

    public int capacity() {
        return 1; 
    }

    public boolean canAccept(Preparable ingredient) {
        return isClean; 
    }

    public void addIngredient(Preparable ingredient) {
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