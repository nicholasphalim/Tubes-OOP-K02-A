package station;

import item.*;
import entity.*;
import inventory.Plate;
import main.GamePanel;
import object.*;
import ingredient.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class TrashStation extends Station {
    
    public TrashStation(GamePanel gp) {
        super(gp);
    }

    @Override
    public boolean canAccept(SuperObject item) {
        //accept dish or ingredient
        return (item instanceof Dish) || (item instanceof Ingredient);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            chef.gp.ui.showMessage("Nothing to trash!");
            return;
        } else {
            if (this.canAccept(chef.getInventory())) {
                this.placeItem(chef.getInventory());
                chef.gp.ui.showMessage("Item trashed!");
                chef.setInventory(null);
            } else if (chef.getInventory() instanceof Plate) {
                Plate plate = (Plate) chef.getInventory();
                if (plate.getContents() != null) {
                    //this.placeItem(plate.getContents());
                    chef.gp.ui.showMessage("Plate contents trashed!");
                    plate.clearContents();
                }
            } else {
                chef.gp.ui.showMessage("Cannot trash this item!");
            }
        }
        this.itemOnStation = null; //disappear the trashed item
    }

    @Override
    public Item takeItem() {
        gp.ui.showMessage("Cannot take items from Trash Station!");
        return null;
    }
}
