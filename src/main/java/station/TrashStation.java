package station;

import entity.Chef;
import ingredient.Ingredient;
import inventory.Plate;
import item.Dish;
import item.Item;
import main.GamePanel;

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

    @Override
    public void interact(Chef chef) {
        // Trash station doesn't need interaction logic
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
