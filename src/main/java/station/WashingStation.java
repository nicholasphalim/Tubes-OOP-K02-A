package station;

import java.util.Stack;
import inventory.Plate;

public class WashingStation extends Station {
    private boolean isSink;
    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;

    public WashingStation(boolean isSink) {
        this.isSink = isSink;
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
    }

    public void addDirtyPlate(Plate p) {
        dirtyPlates.push(p);
    }

    @Override
    public void interact(Chef chef) {
        if (!dirtyPlates.isEmpty()) {
            Plate p = dirtyPlates.pop();
            p.clean();
            cleanPlates.push(p);
        }
    }
}