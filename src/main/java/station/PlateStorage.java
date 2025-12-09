package station;

import inventory.Plate;
import entity.Chef;
import main.GamePanel;
import object.SuperObject;

import java.util.Stack;

public class PlateStorage extends Station {
    private Stack<Plate> plateStack;

    public PlateStorage() {
        try {
            //super gp?
             this.plateStack = new Stack<>();
        } catch (OutOfMemoryError e) {
            System.err.println("Failed to initialize PlateStorage: " + e.getMessage());
        }
    }

    @Override
    public boolean canAccept(SuperObject plate) {
        return false; // PlateStorage does not accept plates from chefs
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            chef.setInventory(this.takeItem());
        }
    }

    @Override
    public Plate takeItem() {
        if (plateStack.isEmpty()) {
            return null;
        }
        return plateStack.pop();
    }
}
