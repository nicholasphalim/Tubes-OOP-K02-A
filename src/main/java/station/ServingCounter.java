package station;

import inventory.Plate;
import entity.*;
import item.Dish;
import order.*;
import main.GamePanel;
import object.SuperObject;
import station.PlateStorage;

import javax.imageio.ImageIO;
import java.awt.*;
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
    }

    @Override
    //covariant parameter type
    public boolean canAccept(SuperObject plate) {
        //accept only plates with contents
        return this.itemOnStation == null && ((Plate) plate).getContents() != null;
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