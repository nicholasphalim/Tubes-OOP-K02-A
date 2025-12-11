package station;

import inventory.Plate;
import entity.*;
import item.Dish;
import order.*;
import main.GamePanel;
import object.SuperObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class ServingCounter extends Station {
    private static int penalty = 20;
    private OrderList orderList;

    //instantiate with order list
    public ServingCounter(GamePanel gp, OrderList orderList) {
        super(gp);
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
        boolean correct = orderList.validateOrder(plate.getContents());
        if (correct) {
            gp.ui.showMessage("Served dish correctly! +" + orderList.getOrders().get(0).getReward() + " points.");
            GamePanel.addScore(orderList.getOrders().get(0).getReward());
        } else {
            gp.ui.showMessage("Served dish incorrectly! -" + penalty + " points.");
            GamePanel.addScore(-penalty);
        }
    }
}