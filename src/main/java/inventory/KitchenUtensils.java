package inventory;

import item.Item;
import main.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class KitchenUtensils extends Item {
    protected List<Item> contents;

    public KitchenUtensils(GamePanel gp) {
        super(gp);
        this.contents = new ArrayList<>();
    }

    public List<Item> getContents() {
        return contents;
    }

    public void addContents(List<Item> content) {
        this.contents.addAll(content);
    }
}