package station;

import entity.Chef;
import inventory.Plate;
import item.Item;
import main.GamePanel;
import order.Order;
import order.OrderList;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ServingCounter extends Station {
    private static final int PENALTY = 20;
    private final OrderList orderList;


    public ServingCounter(GamePanel gp, OrderList orderList) {
        super(gp);
        name = "Serving Counter";
        this.orderList = orderList;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/serve.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0; solidArea.y = 0;
        solidArea.width = 48; solidArea.height = 48;
        solidAreaDefaultX = solidArea.x; solidAreaDefaultY = solidArea.y;
    }

    @Override
    public boolean canAccept(Item item) {
        if (!(item instanceof Plate)) return false;
        Plate plate = (Plate) item;
        return plate.dish != null;
    }

    @Override
    public boolean placeItem(Item item) {
        if (canAccept(item)) {
            serve((Plate) item);

            return true;
        }

        gp.ui.showMessage("Can only serve plates with food!");
        return false;
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() != null) {
            boolean success = this.placeItem(chef.getInventory());

            if (success) {
                chef.setInventory(null);
            }
        }
    }

    @Override
    public Item takeItem() {
        gp.ui.showMessage("Cannot take items from Serving Counter!");
        return null;
    }

    public void serve(Plate plate) {
        if (plate == null || plate.dish == null) return;
        if (gp.gameState != gp.playState) return;

//        System.out.println("\n========== SERVING COUNTER DEBUG ==========");
//        System.out.println("Serving dish: " + plate.dish.getDishName());
//        System.out.println("Dish components:");
//        for (preparable.Preparable p : plate.dish.getComponents()) {
//            if (p instanceof ingredient.Ingredient) {
//                ingredient.Ingredient ing = (ingredient.Ingredient) p;
//                System.out.println("  - " + ing.getIngName() + " [State: " + ing.getState() + "]");
//            }
//        }
//        System.out.println("===========================================\n");

        this.itemOnStation = plate;

        Order matched = orderList.validateOrder(plate.dish);

        if (matched != null) {
            int score = matched.getReward();

            gp.ui.showMessage("Served Correctly! +" + score);
            GamePanel.addScore(matched.getReward());
            gp.resetFailureCount();
        } else {
            gp.ui.showMessage("Wrong Order! -" + PENALTY);
            GamePanel.addScore(-PENALTY);
            gp.addFailure();
        }
        consume(plate);
    }

    public void consume(Plate plate) {
        plate.dish = null;

        this.itemOnStation = null;

        new Thread(() -> {
            try {
                Thread.sleep(5000);

                plate.clearContents();
                plate.makeDirty();
                plate.updateImage();

                PlateStorage plateStorage = PlateStorage.getInstance(gp);
                if (plateStorage != null) {
                    plateStorage.addPlate(plate);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}