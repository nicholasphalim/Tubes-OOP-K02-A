package item;

import ingredient.Ingredient;
import ingredient.State;
import inventory.CookingDevice;
import inventory.KitchenUtensils;
import inventory.Plate;
import main.GamePanel;
import preparable.Preparable;

import java.util.List;

public class Oven extends KitchenUtensils implements CookingDevice {

    private Item currentItem;
    private GamePanel gp;

    private Thread cookingThread;
    private volatile int progress = 0;
    private volatile boolean isCooking = false;
    private final int DURATION_MS = 5000;

    public Oven(GamePanel gp) {
        super(gp);
        this.name = "Oven";
        this.gp = gp;
    }

    @Override
    public boolean canAccept(Item item) {
        if (currentItem != null) return false;

        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            return ing.canBeCooked() && ing.getName().equalsIgnoreCase("Chopped Dough");
        }
        else if (item instanceof Dish) {
            Dish dish = (Dish) item;
            List<Preparable> dishComponents = dish.getComponents();
            boolean foundDough = false;
            for (Preparable p : dishComponents) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    if (ing.getName().equalsIgnoreCase("Chopped Dough")) {foundDough = true; break;}
                }
            }
            return !dish.isCooked() && foundDough;
        }

        return false;
    }

    @Override
    public boolean addItem(Item item) {
        if (canAccept(item)) {
            this.currentItem = item;
            return true;
        }
        return false;
    }

    @Override
    public Item takeItem() {
        if (isCooking) stopCooking();

        Item temp = currentItem;
        currentItem = null;
        progress = 0;
        return temp;
    }

    @Override
    public Item peekItem() {
        return currentItem;
    }

    @Override
    public void startCooking() {
        if (currentItem == null) {
            if(gp != null) gp.ui.showMessage("Oven is empty!");
            return;
        }

        if ((currentItem instanceof Dish && ((Dish)currentItem).isCooked()) ||
                (currentItem instanceof Ingredient && ((Ingredient)currentItem).getState() == State.COOKED)) {
            if(gp != null) gp.ui.showMessage("Already cooked!");
            return;
        }

        if (isCooking) return;

        isCooking = true;
        cookingThread = new Thread(() -> {
            try {
                if(gp != null) gp.ui.showMessage("Baking started...");

                while (progress < 100 && isCooking) {
                    if (currentItem == null) break;

                    progress += (100 * 100) / DURATION_MS;
                    if (progress > 100) progress = 100;

                    Thread.sleep(100);
                }

                if (progress >= 100 && currentItem != null) {
                    completeCooking();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isCooking = false;
            }
        });
        cookingThread.start();
    }

    private void stopCooking() {
        isCooking = false;
    }

    private void completeCooking() {
        if (currentItem instanceof Ingredient) {
            Ingredient ing = (Ingredient) currentItem;
            ing.changeState(State.COOKED);
            ing.updateImage();
            ing.name = "Baked " + ing.name;
        }
        else if (currentItem instanceof Dish) {
            Dish dish = (Dish) currentItem;
            dish.setCooked(true);
            dish.name = "Baked " + dish.name;
        }

        System.out.println("Oven finished baking!");
    }

    @Override public boolean isCooking() { return isCooking; }
    @Override public int getProgress() { return progress; }
    public boolean isPortable() { return false; }

}

