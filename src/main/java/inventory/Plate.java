package inventory;

import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;
import item.Item;
import item.Dish;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Plate extends KitchenUtensils {
    public boolean isClean;
    public Dish dish;

    public Plate(GamePanel gp) {
        super(gp);
        this.name = "Plate";
        this.isClean = true;
        this.dish = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Plate.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPortable() {
        return true;
    }

    public int capacity() {
        return 1; 
    }

    public boolean canAccept(Item ingredient) {
        return isClean; 
    }

    public void addIngredient(Item ingredient) {
        if (canAccept(ingredient)) {
            this.contents.add(ingredient);
        }
    }

    public void clean() {
        this.isClean = true;
        clearContents();
    }

    public void makeDirty() {
        this.isClean = false;
        clearContents();
    }

    public void clearContents() {
        this.dish = null;
    }

    public void updateImage() {
        if (isClean) {
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/objects/Plate.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/objects/Plate_DIRTY.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        if (dish != null && dish instanceof Dish) {
            Dish d = (Dish) dish;
            int offset = 5;
            int margin = 6;
            int size = gp.tileSize - (margin * 2);

            for (Preparable p : d.getComponents()) {
                Item item = (Item) p;
                if (item.image != null) {
                    g2.drawImage(item.image, x + margin, y + margin - offset, size, size, null);

                    offset += 5;
                }
            }
        }
    }
}