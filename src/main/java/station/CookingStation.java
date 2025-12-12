package station;

import entity.Chef;
import inventory.CookingDevice;
import inventory.Plate;
import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;



public class CookingStation extends Station {
    private CookingDevice cookingDevice;

    public CookingStation(GamePanel gp, CookingDevice cookingDevice) {
        super(gp);
        name = "Cooking Station";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/oven.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        this.cookingDevice = cookingDevice;
    }

    @Override
    public boolean placeItem(Item item) {
        if (item instanceof Plate) {
            return false;
        }

        boolean success = cookingDevice.addItem(item);

        if (success) {
            gp.ui.showMessage("Put " + item.name + " in Oven");
            return true;
        } else {
            gp.ui.showMessage("Oven can't bake this!");
            return false;
        }
    }

    @Override
    public Item takeItem() {
        Item item = cookingDevice.takeItem();
        if (item != null) {
            gp.ui.showMessage("Took " + item.name);
        }
        return item;
    }

    @Override
    public void interact(Chef chef) {
        cookingDevice.startCooking();
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        super.draw(g2, gp);

        Item itemInOven = cookingDevice.peekItem();
        if (itemInOven != null && itemInOven.image != null) {
            g2.drawImage(itemInOven.image, x + 12, y + 12, 24, 24, null);
        }

        if (cookingDevice.isCooking() || cookingDevice.getProgress() > 0) {

            int barWidth = 36;
            int barHeight = 6;
            int barX = x + (gp.tileSize - barWidth) / 2;
            int barY = y - 10;

            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            g2.setColor(Color.GREEN);
            double currentProgress = cookingDevice.getProgress();
            if (currentProgress >= 100) {currentProgress = 100;}
            int greenWidth = (int)((currentProgress / 100.0) * barWidth);
            g2.fillRect(barX, barY, greenWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}