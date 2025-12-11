package station;

import ingredient.Ingredient;
import inventory.Plate;
import entity.*;
import item.Dish;
import item.Item;
import order.*;
import main.GamePanel;
import object.SuperObject;
import station.PlateStorage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ServingCounter extends Station {
    private static int penalty = 20;
    private OrderList orderList;
    private Thread servingThread;

    //instantiate with order list
    public ServingCounter(GamePanel gp, OrderList orderList) {
        super(gp);
        name = "Serving Counter";
        this.orderList = orderList;
    public ServingCounter(GamePanel gp) {
        super(gp);
        name = "Serving Counter";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/serve.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    //covariant parameter type
    public boolean canAccept(Item item) {
//        return this.itemOnStation == null && ((Plate) plate).getContents() != null;
        //menerima Plate tak kosong
        if (!(item instanceof Plate)) return false;
        return ((Plate) item).dish != null;
    }

    @Override
    public boolean placeItem(Item item) {
        // Hanya terima Dish (Masakan Jadi)
        if (canAccept(item)) {
            if (((Plate) item).dish == null) {
                return false;
            }
            Dish dish = (Dish) ((Plate) item).dish;

            // Panggil fungsi validasi di OrderManager
            boolean isCorrect = gp.orderList.validateOrder(dish);

            if (isCorrect) {
                gp.ui.showMessage("Order Delivered! +Points");
                // Item diterima dan hilang (disajikan)
                return true;
            } else {
                gp.ui.showMessage("Wrong Order!");
                // Item ditolak, tetap di tangan chef
                return false;
            }
        }
        return false;
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() != null && chef.getInventory() instanceof Plate) {
            Plate plate = (Plate) chef.getInventory();
            if (this.canAccept(plate)) {
                this.placeItem(chef.getInventory());
                chef.setInventory(null);
                this.serve(plate);
            } else {
                gp.ui.showMessage("Cannot serve empty plate!");
            }
        }
    }

    @Override
    public Plate takeItem() {
        gp.ui.showMessage("Cannot take items from Serving Counter!");
        return null;
    }

    public void serve(Plate plate) {
        if (!(plate.getContents().get(0) instanceof Dish)) {
            consume(plate);
            gp.ui.showMessage("Plate does not contain a dish!");
            GamePanel.addScore(-penalty);
            return;
        }

        boolean correct = orderList.validateOrder((Dish) plate.getContents().get(0));
        consume(plate);
        if (correct) {
            gp.ui.showMessage("Served dish correctly! +" + orderList.getOrders().get(0).getReward() + " points.");
            GamePanel.addScore(orderList.getOrders().get(0).getReward());
        } else {
            gp.ui.showMessage("Served dish incorrectly! -" + penalty + " points.");
            GamePanel.addScore(-penalty);
        }
    }

    public void consume(Plate plate) {
        servingThread = new Thread(() -> {
            try {
                if(gp != null) {
                    gp.ui.showMessage("Serving...");
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                plate.clearContents();
                PlateStorage plateStorage = PlateStorage.getInstance(gp);
                plateStorage.addPlate(plate);
            }
            
        });
        servingThread.start();
    }
}