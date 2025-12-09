package station;

import java.util.Stack;

import entity.Chef;
import inventory.Plate;
import main.GamePanel;

public class WashingStation extends Station {
    private boolean isSink;
    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;

    public WashingStation(GamePanel gp, boolean isSink) {
        super(gp);
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