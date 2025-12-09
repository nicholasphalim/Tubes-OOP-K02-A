package inventory;

import item.Item;
import main.GamePanel;
import preparable.Preparable;

import java.util.ArrayList;
import java.util.List;

public class KitchenUtensils extends Item {
    protected List<Preparable> contents;

    public KitchenUtensils(GamePanel gp) {
        super(gp);
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return contents;
    }

    public void addContents(List<Preparable> content) {
        this.contents.addAll(content);
    }
}