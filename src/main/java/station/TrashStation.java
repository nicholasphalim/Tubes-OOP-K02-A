package station;

import item.*;
import entity.Chef;
import inventory.Plate;
import main.GamePanel;
import object.SuperObject;
import item.Dish;
import ingredient.Ingredient;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class TrashStation extends Station {
    
    public TrashStation(GamePanel gp) {
        super(gp);
        name = "Trash Station";
        try {
            // Pastikan Anda memiliki gambar untuk trash bin
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/trash.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return null; //tidak bisa diambil kembali
    }
}
