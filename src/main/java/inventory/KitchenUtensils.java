package inventory;

import item.Item;
import main.GamePanel;

import java.util.ArrayList;
import java.util.List;


// menggunakan generic Storage<Item> untuk type-safe storage
public class KitchenUtensils extends Item {
    protected Storage<Item> storage;

    public KitchenUtensils(GamePanel gp) {
        super(gp);
        this.storage = new Storage<>();
    }

    public List<Item> getContents() {
        return storage.getAllItems();
    }

    public void addContents(List<Item> content) {
        for (Item item : content) {
            storage.addItem(item);
        }
    }

    public boolean addItem(Item item) {
        return storage.addItem(item);
    }

    public Item removeItem() {
        return storage.removeItem();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public void clearStorage() {
        storage.clear();
    }
}