package inventory;

import java.util.ArrayList;
import java.util.List;

public class KitchenUtensils {
    protected List<Preparable> contents;

    public KitchenUtensils() {
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return contents;
    }

    public void addContents(List<Preparable> content) {
        this.contents.addAll(content);
    }
}