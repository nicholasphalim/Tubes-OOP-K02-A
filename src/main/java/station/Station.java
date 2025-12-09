package station;

import entity.Chef;
import entity.Entity;
import item.Item;
import main.GamePanel;
import object.SuperObject;

import java.awt.*;

public abstract class Station extends SuperObject {
    public Item itemOnStation;
    protected int capacity;

    public Station(GamePanel gp) {
        super(gp);
        this.capacity = 1;
        this.type = TYPE_STATION;
    }

    public boolean canAccept(SuperObject item) {
        return itemOnStation == null;
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Chef) {
            interact((Chef) entity);
        }
    }

    public void interact(Chef chef) {

    }

    public Item takeItem() {
        Item item = this.itemOnStation;
        this.itemOnStation = null;
        return item;
    }

    public boolean placeItem(Item item) {
        if (canAccept(item)) {
            this.itemOnStation = item;
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        super.draw(g2, gp);

        if (itemOnStation != null) {
            int itemDrawX = x;
            int itemDrawY = y;

            g2.drawImage(itemOnStation.image, itemDrawX, itemDrawY, gp.tileSize, gp.tileSize, null);

            Font originalFont = g2.getFont();
            g2.setFont(originalFont.deriveFont(Font.PLAIN, 10f));
            g2.setColor(Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(itemOnStation.name);
            int textX = x + (gp.tileSize - textWidth) / 2;
            int textY = y + gp.tileSize;
            g2.drawString(itemOnStation.name, textX, textY);
            g2.setFont(originalFont);
        }
    }
}