package station;

import entity.Action;
import entity.Chef;
import ingredient.Ingredient;
import ingredient.State;
import item.Dish;
import item.Item;
import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CuttingStation extends Station{

    private Thread cuttingThread;
    private volatile int currentCuttingProgress;
    private final int TOTAL_CUTTING_DURATION_MS = 2000;
    private final int PROGRESS_UPDATE_INTERVAL_MS = 100;
    public volatile boolean cutting = false;
    private Chef currentChef;

    private List<Preparable> ingredientsStack;
    private final int MAX_CAPACITY = 5;

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
        ingredientsStack = new ArrayList<>();
    }

    public int getCurrentCuttingProgress() {
        return currentCuttingProgress;
    }

    public boolean canAccept(Item item) {
        if (!(item instanceof Ingredient) && !(item instanceof Dish)) {
            return false;
        }

        int incomingSize = (item instanceof Dish) ? ((Dish) item).getComponents().size() : 1;
        if (ingredientsStack.size() + incomingSize > 5) {
            return false;
        }

        if (ingredientsStack.isEmpty()) {
            return true;
        } else {
            if (item instanceof Ingredient) {
                if (((Ingredient) item).getState() == State.RAW) {
                    return false;
                }
            }

            for (Preparable p : ingredientsStack) {
                if (p instanceof Ingredient) {
                    if (((Ingredient) p).getState() == State.RAW) {
                        return false; // Ditolak
                    }
                }
            }
            return true;
        }
    }

    @Override
    public boolean placeItem(Item item) {
        if (cutting) {
            gp.ui.showMessage("Wait for cutting to finish!");
            return false;
        }

        if (ingredientsStack.isEmpty()) {
            if (item instanceof Dish) {
                ingredientsStack.addAll(((Dish) item).getComponents());
            } else if (item instanceof Ingredient) {
                ingredientsStack.add((Preparable) item);
            }
            updateVisualItem();
            gp.ui.showMessage("Placed " + item.name);
            return true;
        } else {
            if (!canAccept(item)) {
                gp.ui.showMessage("Cannot assemble/place here!");
                return false;
            }

            if (item instanceof Dish) {
                ingredientsStack.addAll(((Dish) item).getComponents());
            } else {
                ingredientsStack.add((Preparable) item);
            }
            updateVisualItem();
            gp.ui.showMessage("Added " + item.name);
            return true;
        }
    }

    @Override
    public Item takeItem() {
        if (cutting) {
            gp.ui.showMessage("Finish cutting first!");
            return null;
        }

        if (ingredientsStack.isEmpty()) return null;

        if (ingredientsStack.size() == 1) {
            Ingredient temp = (Ingredient) ingredientsStack.get(0);
            ingredientsStack.clear();
            this.itemOnStation = null;
            return temp;
        }
        else {
            List<Preparable> componentsForDish = new ArrayList<>(ingredientsStack);
            Dish newDish = new Dish(componentsForDish, gp);
            ingredientsStack.clear();
            this.itemOnStation = null;
            gp.ui.showMessage("Picked up " + newDish.getDishName());
            return newDish;
        }
    }

    private void updateVisualItem() {
        if (!ingredientsStack.isEmpty()) {
            this.itemOnStation = (Item) ingredientsStack.get(ingredientsStack.size() - 1);
        } else {
            this.itemOnStation = null;
        }
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
                    ing.updateImage();
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

    public void draw(Graphics2D g2, GamePanel gp) {
        super.draw(g2, gp);

        if (cutting && currentCuttingProgress < 100) {
            int barWidth = 36;
            int barHeight = 6;
            int barX = x + (gp.tileSize - barWidth) / 2;
            int barY = y + 10;

            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            g2.setColor(Color.GREEN);
            int currentProgress = currentCuttingProgress;
            int greenWidth = (int)((currentProgress / 100.0) * barWidth);
            g2.fillRect(barX, barY, greenWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}