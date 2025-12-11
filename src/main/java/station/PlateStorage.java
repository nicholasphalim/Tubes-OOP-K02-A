package station;

import inventory.Plate;
import entity.Chef;
import item.Item;
import main.GamePanel;
import object.SuperObject;

import java.util.Stack;

public class PlateStorage extends Station {
    private Stack<Plate> plateStack;

    public PlateStorage(GamePanel gp) {
        super(gp);
        try {
             this.plateStack = new Stack<>();
             Plate plate1 = new Plate(gp);
             Plate plate2 = new Plate(gp);
             Plate plate3 = new Plate(gp);
             plateStack.push(plate1);
             plateStack.push(plate2);
             plateStack.push(plate3);
        } catch (OutOfMemoryError e) {
            System.err.println("Failed to initialize PlateStorage: " + e.getMessage());
        }
    }

    @Override
    public boolean canAccept(Item plate) {
        return false; // PlateStorage does not accept plates from chefs
    }

    @Override
    public void interact(Chef chef) {
//        if (chef.getInventory() == null) {
//            chef.setInventory(this.takeItem());
//        }
    }

    @Override
    public Plate takeItem() {
        if (plateStack.isEmpty()) {
            System.out.println("Plate stack is empty");
            return null;
        }
        gp.ui.showMessage("Picked up Plate");
        return plateStack.pop();
    }
}
