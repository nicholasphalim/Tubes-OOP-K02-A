package order;

import java.util.ArrayList;
import java.util.List;
import item.Dish;

public class OrderList {
    private List<Order> orders;
    private int count;

    public OrderList() {
        this.orders = new ArrayList<>();
        this.count = 0;
    }

    public void addOrder(Order order) {
        orders.add(order);
        count++;
    }

    public int getCount() {
        return count;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public boolean validateOrder(Dish dishServed) {
        if (orders.isEmpty()) return false;

        Order first = orders.get(0);

        boolean correct = dishServed.isDone(first.getRecipe());

        if(correct){
            orders.remove(0);
            count--;
            return true;
        }
        return false;
    }
}
