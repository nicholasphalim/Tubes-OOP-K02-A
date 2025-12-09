package item;

import main.GamePanel;
import object.SuperObject;


public abstract class Item extends SuperObject {
    public Item(GamePanel gp) {
        super(gp);
        this.type = TYPE_PICKUP;
    }
}