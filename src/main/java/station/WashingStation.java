package station;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import entity.Action;
import entity.Chef;
import ingredient.Ingredient;
import ingredient.State;
import inventory.Plate;
import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;

public class WashingStation extends Station {
    private boolean isSink;
    private Stack<Plate> plates;
    private WashingStation cleanStack;

    private Thread washingThread;
    private volatile int currentWashingProgress;
    private final int TOTAL_WASHING_DURATION_MS = 2000;
    private final int PROGRESS_UPDATE_INTERVAL_MS = 100;
    public volatile boolean washing = false;
    private Chef currentChef;

    private Plate plateBeingWashed = null;

    public WashingStation(GamePanel gp, boolean isSink) {
        super(gp);
        this.isSink = isSink;
        this.plates = new Stack<>();
        this.cleanStack = cleanStack;
    }

//    public void addDirtyPlate(Plate p) {
//        dirtyPlates.push(p);
//    }
    public void setCleanStack(WashingStation cleanStack) {
        this.cleanStack = cleanStack;
    }

    @Override
    public void interact(Chef chef) {
//        if (!dirtyPlates.isEmpty()) {
//            Plate p = dirtyPlates.pop();
//            p.clean();
//            cleanPlates.push(p);
//        }
        if (!isSink) {
            return;
        }

        if (plates.isEmpty() && plateBeingWashed == null) {
            chef.gp.ui.showMessage("No dirty plates!");
            return;
        }

        if (washingThread == null || !washingThread.isAlive()) {
            if (plateBeingWashed == null) {
                plateBeingWashed = plates.pop();
            }

            startWashingProcess(chef);
        } else {
            if (isChefInRange(chef)) {
                washing = true;
                currentChef = chef;
                chef.gp.ui.showMessage("Resuming washing...");
            } else {
                chef.gp.ui.showMessage("Too far!");
            }
        }
    }

    public boolean canAccept(Item item) {
        if (!(item instanceof Plate)) return false;

        Plate p = (Plate) item;

        if (isSink) {
            return !p.isClean;
        } else {
            return p.isClean;
        }
    }

    @Override
    public boolean placeItem(Item item) {
        if (!canAccept(item)) {
            if (plates.size() >= 5) {
                gp.ui.showMessage("Station is full!");
            } else {
                gp.ui.showMessage("Cannot place this item!");
            }
            return false;
        }

        Plate plate = (Plate) item;
        plates.push(plate);

        if (!plates.isEmpty()) {
            this.itemOnStation = (Item) plate;
        }

        gp.ui.showMessage("Placed plate");
        return true;
    }

    @Override
    public Item takeItem() {
        if (plates.isEmpty()) {
            return null;
        }
        Plate p = plates.pop();
        updateVisual();
        return p;
    }

    private void startWashingProcess(Chef chef) {
        if (currentWashingProgress >= 100) {
            currentWashingProgress = 0;
        }

        washing = true;
        currentChef = chef;

        washingThread = new Thread(() -> {
            chef.gp.ui.showMessage("Washing started...");

            try {
                while (currentWashingProgress < 100) {

                    if (isChefInRange(currentChef) && washing) {
                        currentChef.busyState = Action.WASHING;
                        currentWashingProgress += (PROGRESS_UPDATE_INTERVAL_MS * 100) / TOTAL_WASHING_DURATION_MS;
                        if (currentWashingProgress > 100) currentWashingProgress = 100;
                        Thread.sleep(PROGRESS_UPDATE_INTERVAL_MS);

                    } else {
                        if (!isChefInRange(currentChef)) {
                            washing = false;
                            if (currentChef != null && currentChef.busyState == Action.WASHING) {
                                currentChef.busyState = null;
                            }
                        }
                        Thread.sleep(PROGRESS_UPDATE_INTERVAL_MS);
                    }
                }

                if (currentWashingProgress >= 100) {
                    plateBeingWashed.clean();
                    plateBeingWashed.updateImage();
                    cleanStack.plates.push(plateBeingWashed);
                    plateBeingWashed = null;
                    updateVisual();
                    cleanStack.updateVisual();
                    chef.gp.ui.showMessage("Washing Finished!");
                }

            } catch (InterruptedException e) {
                chef.gp.ui.showMessage("Washing error!");
            } finally {
                if (currentChef != null && currentChef.busyState == Action.WASHING) {
                    currentChef.busyState = null;
                }
                currentChef = null;
                washing = false;
                currentWashingProgress = 0;
            }
        });


        washingThread.start();
    }

    private void updateVisual() {
        if (!plates.isEmpty()) {
            this.itemOnStation = (Item) plates.peek();
        } else {
            this.itemOnStation = null;
        }
    }

    private boolean isChefInRange(Chef chef) {
        if (chef == null) return false;

        Rectangle stationRect = new Rectangle(
                this.x + this.solidArea.x,
                this.y + this.solidArea.y,
                this.solidArea.width,
                this.solidArea.height
        );

        int chefSolidX = chef.position.x + chef.solidArea.x;
        int chefSolidY = chef.position.y + chef.solidArea.y;

        int reachDistance = chef.gp.tileSize / 2;

        Rectangle interactionRect = new Rectangle(
                chefSolidX,
                chefSolidY,
                chef.solidArea.width,
                chef.solidArea.height
        );

        switch (chef.getDirection()) {
            case UP:
                interactionRect.y -= reachDistance;
                break;
            case DOWN:
                interactionRect.y += reachDistance;
                break;
            case LEFT:
                interactionRect.x -= reachDistance;
                break;
            case RIGHT:
                interactionRect.x += reachDistance;
                break;
        }

        return stationRect.intersects(interactionRect);
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        super.draw(g2, gp);

        if (washing && currentWashingProgress < 100) {
            int barWidth = 36;
            int barHeight = 6;
            int barX = x + (gp.tileSize - barWidth) / 2;
            int barY = y + 10;

            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            g2.setColor(Color.GREEN);
            int currentProgress = currentWashingProgress;
            int greenWidth = (int)((currentProgress / 100.0) * barWidth);
            g2.fillRect(barX, barY, greenWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}