package station;

import item.*;
import entity.*;
import inventory.Plate;
import main.GamePanel;
import object.SuperObject;
import item.Dish;
import ingredient.Ingredient;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class TrashStation extends Station {
    //singleton PATTERN
    //use getInstance to get the single instance of TrashStation
    private static TrashStation instance;
    
    private TrashStation(GamePanel gp) {
        super(gp);
        name = "Trash Station";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/trash.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TrashStation getInstance(GamePanel gp) {
        if (instance == null) {
            instance = new TrashStation(gp);
        }
        return instance;
    }

//    @Override
//    public boolean canAccept(Item item) {
//        //menerima dish atau ingredient
//        boolean isAcceptable = (item instanceof Dish) || (item instanceof Ingredient);
//        return isAcceptable;
//    }

    @Override
    public void interact(Chef chef) {
//        if (chef.getInventory() == null) {
//            return; //nothing to trash
//        } else {
//            if (this.canAccept(chef.getInventory())) {
//                this.placeItem(chef.getInventory());
//                chef.setInventory(null);
//            } else if (chef.getInventory() instanceof Plate) {
//                Plate plate = (Plate) chef.getInventory();
//                if (plate.getContents() != null) {
//                    //this.placeItem(plate.getContents());
//                    plate.clearContents();
//                }
//            }
//        }
//        this.itemOnStation = null; //langsung hilang setelah dibuang
    }

    @Override
    public boolean placeItem(Item item) {
        if (item instanceof Plate) {
            gp.ui.showMessage("Can't trash plates!");
            return false;
        }

        if (item instanceof Dish || item instanceof Ingredient) {
            gp.ui.showMessage("Trashed " + item.name);
            return true;
        }

        return false;
    }

    @Override
    public Item takeItem() {
        gp.ui.showMessage("Cannot take items from Trash Station!");
        return null;
    }
}
