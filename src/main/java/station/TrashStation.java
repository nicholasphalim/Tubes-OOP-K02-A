package station;

import item.*;
import entity.Chef;
import inventory.Plate;
import main.GamePanel;
import object.SuperObject;
import item.Dish;
import ingredient.Ingredient;

public class TrashStation extends Station {
    
    public TrashStation() {
        //super gp?
    }

    @Override
    public boolean canAccept(SuperObject item) {
        //menerima dish atau ingredient
        boolean isAcceptable = (item instanceof Dish) || (item instanceof Ingredient);
        return isAcceptable;
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            return; //nothing to trash
        } else {
            if (this.canAccept(chef.getInventory())) {
                this.placeItem(chef.getInventory());
                chef.setInventory(null);
            } else if (chef.getInventory() instanceof Plate) {
                Plate plate = (Plate) chef.getInventory();
                if (plate.getContents() != null) {
                    //this.placeItem(plate.getContents());
                    plate.clearContents();
                }
            }
        }
        this.itemOnStation = null; //langsung hilang setelah dibuang
    }

    @Override
    public Item takeItem() {
        return null; //tidak bisa diambil kembali
    }
}
