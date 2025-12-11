package station;

import ingredient.Ingredient;
import inventory.Plate;
import entity.Chef;
import item.Dish;
import item.Item;
import order.*;
import main.GamePanel;
import object.SuperObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ServingCounter extends Station {
    public ServingCounter(GamePanel gp) {
        super(gp);
        name = "Serving Counter";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/serve.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    //covariant parameter type
    public boolean canAccept(Item item) {
//        return this.itemOnStation == null && ((Plate) plate).getContents() != null;
        //menerima Plate tak kosong
        if (!(item instanceof Plate)) return false;
        return ((Plate) item).dish != null;
    }

    @Override
    public boolean placeItem(Item item) {
        // Hanya terima Dish (Masakan Jadi)
        if (canAccept(item)) {
            if (((Plate) item).dish == null) {
                return false;
            }
            Dish dish = (Dish) ((Plate) item).dish;

            // Panggil fungsi validasi di OrderManager
            boolean isCorrect = gp.orderList.validateOrder(dish);

            if (isCorrect) {
                gp.ui.showMessage("Order Delivered! +Points");
                // Item diterima dan hilang (disajikan)
                return true;
            } else {
                gp.ui.showMessage("Wrong Order!");
                // Item ditolak, tetap di tangan chef
                return false;
            }
        }
        return false;
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() != null && chef.getInventory() instanceof Plate) {
            Plate plate = (Plate) chef.getInventory();
            if (this.canAccept(plate)) {
                this.placeItem(chef.getInventory());
                chef.setInventory(null);
                this.serve(plate);
            }
        }
    }

    @Override
    public Plate takeItem() {
        return null; //tidak bisa diambil setelah drop
    }

    public void serve(Plate plate) { //berhubungan dengan OrderList
        return; //implementasi serve ke customer
        //gimana ngasih penalti kalau tidak sesuai order
    }
}