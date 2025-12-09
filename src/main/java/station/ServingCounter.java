package station;

import inventory.Plate;
import entity.Chef;
import item.Dish;
import order.*;
import main.GamePanel;
import object.SuperObject;

public class ServingCounter extends Station {
    public ServingCounter() {
        //super gp?
    }

    @Override
    //covariant parameter type
    public boolean canAccept(SuperObject plate) {
        return this.itemOnStation == null && ((Plate) plate).getContents() != null;
        //menerima Plate tak kosong
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