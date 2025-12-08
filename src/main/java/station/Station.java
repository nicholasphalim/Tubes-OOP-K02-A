package station;

public abstract class Station {
    protected Item itemOnStation;
    protected int capacity;

    public Station() {
        this.capacity = 1; 
    }

    public boolean canAccept(Item item) {
        return itemOnStation == null;
    }

    public abstract void interact(Chef chef);

    public Item takeItem() {
        Item item = this.itemOnStation;
        this.itemOnStation = null;
        return item;
    }

    public void placeItem(Item item) {
        if (canAccept(item)) {
            this.itemOnStation = item;
        }
    }
}