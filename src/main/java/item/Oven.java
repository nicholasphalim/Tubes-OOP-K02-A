package item;

import java.util.Set;

import ingredient.Ingredient;
import ingredient.State;
import inventory.CookingDevice;
import inventory.KitchenUtensils;
import main.GamePanel;
import preparable.Preparable;

public class Oven extends KitchenUtensils implements CookingDevice {

    private Item currentItem;
    private GamePanel gp;

    private Thread cookingThread;
    private volatile double progress = 0;
    private volatile boolean isCooking = false;
    private final int COOKING_DURATION_MS = 12000;
    private final int BURNING_DURATION_MS = 12000;

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
            return ing.canBeCooked() && ing.getIngName().equalsIgnoreCase("Dough") && ing.getState() == State.CHOPPED;
        }
        else if (item instanceof Dish) {
            Dish dish = (Dish) item;
            if (dish.isBurned()) return false;
            Set<Preparable> dishComponents = dish.getComponents();
            boolean foundDough = false;
            for (Preparable p : dishComponents) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    if (ing.getIngName().equalsIgnoreCase("Dough") && ing.getState() == State.CHOPPED) {
                        foundDough = true;
                        break;
                    }
                }
            }
            return (!dish.isCooked() || (dish.isCooked() && !dish.isBurned())) && foundDough;}
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

        if (checkIfBurned(currentItem)) {
            if(gp != null) gp.ui.showMessage("It's burned!");
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

                if (!checkIfCooked(currentItem)) {
                    while (progress < 100 && isCooking) {
                        if (currentItem == null) break;

                        progress += (100.0 * 100) / COOKING_DURATION_MS;
                        if (progress >= 100) {
                            progress = 100;
                            completeCooking();
                        }
                        Thread.sleep(100);
                    }
                } else {
                    progress = 100;
                }

                while (progress < 200 && isCooking) {
                    if (currentItem == null) break;

                    progress += (100.0 * 100) / BURNING_DURATION_MS;

                    if (progress > 150) {
                         gp.ui.showMessage("Warning! Burning soon!");
                    }

                    if (progress >= 200) {
                        progress = 200;
                        burnFood();
                        isCooking = false;
                    }
                    Thread.sleep(100);
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
        }
        else if (currentItem instanceof Dish) {
            Dish dish = (Dish) currentItem;
            dish.setCooked(true);
        }
        if(gp != null) {
            gp.ui.showMessage("Oven finished baking!");
        }
        System.out.println("Oven finished baking!");
    }

    private void burnFood() {
        if (currentItem instanceof Ingredient) {
            Ingredient ing = (Ingredient) currentItem;
            ing.changeState(State.BURNED);
            ing.updateImage();
            ing.name = "Burnt Food";
        }
        else if (currentItem instanceof Dish) {
            Dish dish = (Dish) currentItem;
            dish.setBurned(true);
            dish.name = "Burnt Dish";

            for (Preparable p : dish.getComponents()) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    // Ubah status jadi BURNED
                    ing.changeState(State.BURNED);
                    // Update gambar jadi hitam/gosong
                    ing.updateImage(); 
                }
            }
        }
        
        if(gp != null) gp.ui.showMessage("Food is BURNED!");
        System.out.println("Oven food burned!");
    }

    private boolean checkIfCooked(Item item) {
        if (item instanceof Ingredient) {
            return ((Ingredient) item).getState() == State.COOKED;
        } else if (item instanceof Dish) {
            return ((Dish) item).isCooked();
        }
        return false;
    }

    private boolean checkIfBurned(Item item) {
        if (item instanceof Ingredient) {
            return ((Ingredient) item).getState() == State.BURNED;
        } else if (item instanceof Dish) {
            return ((Dish) item).isBurned();
        }
        return false;
    }

    @Override public boolean isCooking() { return isCooking; }
    @Override public int getProgress() { return (int) progress; }
    public boolean isPortable() { return false; }

}

