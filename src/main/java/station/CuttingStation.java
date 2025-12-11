package station;

import entity.Action;
import entity.Chef;
import ingredient.Dough;
import ingredient.Ingredient;
import ingredient.State;
import inventory.Plate;
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

    private Plate plate;

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
        if (!(item instanceof Ingredient) && !(item instanceof Dish) && !(item instanceof Plate)) {
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

            if (item instanceof Plate) {
                return true;
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

        if (!canAccept(item)) {
            if (ingredientsStack.size() >= 5) {
                gp.ui.showMessage("Station is full!");
            } else if (!ingredientsStack.isEmpty()) {
                gp.ui.showMessage("Cannot assemble RAW items!");
            } else {
                gp.ui.showMessage("Cannot place this item!");
            }
            return false;
        }

        if (item instanceof Plate) {
            if (ingredientsStack.isEmpty()){
                gp.ui.showMessage("You placed " + item.name);
            }
            plate = (Plate) item;
            if (plate.dish != null) {
                Dish dish = (Dish) plate.dish;
                placeItem(dish);
            }
            return true;
        }

        if (item instanceof Dish) {
            Dish dish = (Dish) item;
            ingredientsStack.addAll(dish.getComponents());
            gp.ui.showMessage("Added contents of " + item.name);
        }
        else if (item instanceof Ingredient) {
            ingredientsStack.add((Preparable) item);
            gp.ui.showMessage("Added " + item.name);
        }

        reorderIngredients();
        return true;
    }

    @Override
    public Item takeItem() {
        if (ingredientsStack.isEmpty()) {
            if (plate != null) {
                Plate temp = plate;
                plate = null;
                gp.ui.showMessage("Picked up " + temp.name);
                return temp;
            }
            return null;
        }

        if(ingredientsStack.size() == 1) {
            Ingredient temp  = (Ingredient) ingredientsStack.get(0);
            ingredientsStack.clear();
            gp.ui.showMessage("Picked up " + temp.name);
            return temp;
        } else {
            List<Preparable> componentsForDish = new ArrayList<>(ingredientsStack);

            Dish newDish = new Dish(componentsForDish, gp);

            ingredientsStack.clear();

            if (plate != null) {
                Plate temp = plate;
                temp.dish = newDish;
                plate = null;
                gp.ui.showMessage("Picked up Plate + " + newDish.getDishName());

                return temp;
            }

            gp.ui.showMessage("Picked up " + newDish.getDishName());
            return newDish;
        }
    }

    private void reorderIngredients() {
        if (ingredientsStack.isEmpty()) return;

        Preparable dough = null;

        for (Preparable p : ingredientsStack) {
            if (p instanceof Ingredient) {
                Ingredient ing = (Ingredient) p;
                if (ing instanceof Dough) {
                    dough = p;
                    break;
                }
            }
        }

        if (dough != null) {
            ingredientsStack.remove(dough);
            ingredientsStack.add(0, dough);
        }
    }

    @Override
    public void interact(Chef chef) {
        if (ingredientsStack.isEmpty()) {
            chef.gp.ui.showMessage("Nothing to chop here!");
            chef.currentInteractionStation = null;
            return;
        }

        if (ingredientsStack.size() == 1 && plate == null) {
            Ingredient ing = (Ingredient) ingredientsStack.get(0);
            if (!ing.canBeChopped()) {
                chef.gp.ui.showMessage("You can't chop that!");
                chef.currentInteractionStation = null;
                return;
            }

            if (cuttingThread == null || !cuttingThread.isAlive()) {
                startCuttingProcess(chef, ing);
            } else {
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
        } else {
            gp.ui.showMessage("Cannot chop dish");
            return;
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
                    ing.name = "Chopped " + ing.name;
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
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        if (plate != null && plate.image != null) {
            int margin = 4;
            int plateSize = gp.tileSize - (margin * 2);
            g2.drawImage(plate.image, x + margin, y + margin, plateSize, plateSize, null);
        }

        if (!ingredientsStack.isEmpty()) {
            int stackOffset = 0;

            for (Preparable p : ingredientsStack) {
                Item item = (Item) p;

                if (item.image != null) {
                    int itemMargin = 10;
                    int itemSize = gp.tileSize - (itemMargin * 2);

                    int drawX = x + itemMargin;
                    int drawY = y + itemMargin - stackOffset;

                    if (plate != null) {
                        drawY -= 5;
                    }

                    g2.drawImage(item.image, drawX, drawY, itemSize, itemSize, null);

                    stackOffset += 6;
                }
            }
        }

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