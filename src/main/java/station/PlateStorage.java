package station;

import inventory.Plate;
import entity.Chef;
import main.GamePanel;
import object.SuperObject;

import java.util.Stack;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class PlateStorage extends Station {
    private Stack<Plate> plateStack;

    //singleton PATTERN
    //use getInstance to get the single instance of PlateStorage
    private static PlateStorage instance;

    private PlateStorage(GamePanel gp) {
        super(gp);
        name = "Plate Storage";
        try {
             this.plateStack = new Stack<>();
        } catch (OutOfMemoryError e) {
            System.err.println("Failed to initialize PlateStorage: " + e.getMessage());
        }
    }

    public static PlateStorage getInstance(GamePanel gp) {
        if (instance == null) {
            instance = new PlateStorage(gp);
        }
        return instance;
    }

    public void addPlate(Plate plate) {
        gp.ui.showMessage("Added a plate to storage.");
        plateStack.push(plate);
    }

    @Override
    public boolean canAccept(SuperObject plate) {
        gp.ui.showMessage("Plate Storage does not accept items!");
        return false;
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
            gp.ui.showMessage("No plates left in storage!");
            return null;
        }
        gp.ui.showMessage("Took a plate from storage.");
        return plateStack.pop();
    }
}
