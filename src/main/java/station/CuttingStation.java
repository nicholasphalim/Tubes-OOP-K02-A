package station;

import entity.Action;
import entity.Chef;
import ingredient.Ingredient;
import ingredient.State;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class CuttingStation extends Station{

    private Thread cuttingThread;
    private volatile int currentCuttingProgress;
    private final int TOTAL_CUTTING_DURATION_MS = 2000;
    private final int PROGRESS_UPDATE_INTERVAL_MS = 100;
    public volatile boolean cutting = false;
    private Chef currentChef;

    public CuttingStation(GamePanel gp) {
        super(gp);
        name = "Cutting Station";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/cutting.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        currentCuttingProgress = 0;
    }

    public int getCurrentCuttingProgress() {
        return currentCuttingProgress;
    }

    @Override
    public void interact(Chef chef) {
        if (itemOnStation == null || !(itemOnStation instanceof Ingredient)) {
            chef.gp.ui.showMessage("Nothing to chop here!");
            chef.currentInteractionStation = null;
            return;
        }

        Ingredient ing = (Ingredient) itemOnStation;
        if (!ing.canBeChopped()) {
            chef.gp.ui.showMessage("You can't chop that!");
            chef.currentInteractionStation = null;
            return;
        }

        if (cuttingThread == null || !cuttingThread.isAlive()) {
            startCuttingProcess(chef, ing);
        }
        else {
            if (isChefInRange(chef)) {
                cutting = true;
                currentChef = chef;
                chef.currentInteractionStation = this;
                chef.gp.ui.showMessage("Resuming...");
            } else {
                chef.gp.ui.showMessage("Too far to cut!");
                chef.currentInteractionStation =  null;
            }
        }
    }

    private void startCuttingProcess(Chef chef, Ingredient ing) {
        if (currentCuttingProgress >= 100) {
            currentCuttingProgress = 0;
        }

        cutting = true;
        currentChef = chef;

        cuttingThread = new Thread(() -> {
            chef.gp.ui.showMessage("Cutting started...");

            try {
                while (currentCuttingProgress < 100) {

                    if (isChefInRange(currentChef) && cutting) {

                        currentChef.busyState = Action.CUTTING;

                        currentCuttingProgress += (PROGRESS_UPDATE_INTERVAL_MS * 100) / TOTAL_CUTTING_DURATION_MS;
                        if (currentCuttingProgress > 100) currentCuttingProgress = 100;


                        Thread.sleep(PROGRESS_UPDATE_INTERVAL_MS);

                    } else {
                        if (!isChefInRange(currentChef)) {
                            cutting = false;

                            if (currentChef != null && currentChef.busyState == Action.CUTTING) {
                                currentChef.busyState = null;
                            }
                        }

                        Thread.sleep(PROGRESS_UPDATE_INTERVAL_MS);
                    }
                }

                if (currentCuttingProgress >= 100) {
                    ing.changeState(State.CHOPPED);
                    itemOnStation.name = "Chopped " + itemOnStation.name;
                    chef.gp.ui.showMessage("Chopping Finished!");
                }

            } catch (InterruptedException e) {
                chef.gp.ui.showMessage("Cutting error!");
            } finally {
                if (currentChef != null && currentChef.busyState == Action.CUTTING) {
                    currentChef.busyState = null;
                }
                currentChef = null;
                cutting = false;
                currentCuttingProgress = 0;
            }
        });


        cuttingThread.start();
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
}