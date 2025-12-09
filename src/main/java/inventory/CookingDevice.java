package inventory;

import item.Item;
import preparable.Preparable;

public interface CookingDevice {
    boolean canAccept(Item item);
    boolean addItem(Item item);
    Item takeItem();
    Item peekItem();
    void startCooking();
    boolean isCooking();
    int getProgress();
}