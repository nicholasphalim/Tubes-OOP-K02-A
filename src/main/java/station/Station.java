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

    public boolean canAccept(Item item) {
        return itemOnStation == null;
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Chef) {
            interact((Chef) entity);
        }
    }

    public abstract void interact(Chef chef);

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
            g2.drawImage(itemOnStation.image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}