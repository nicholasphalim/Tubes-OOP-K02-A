package order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import item.Dish;
import main.GamePanel;
import recipe.Recipe;

public class OrderList {

    private GamePanel gp;
    private List<Order> orders;
    private List<Recipe> availableRecipes;

    private int orderCounter = 1;
    private Random random = new Random();

    private final int MAX_ORDERS = 5;
    private final int BASE_REWARD = 100;
    private final int BASE_PENALTY = 20;
    private final int BASE_TIME_LIMIT = 90;
    private double timeSinceLastSpawn = 0.0;
    private final double SPAWN_INTERVAL = 15.0;
    public OrderList(GamePanel gp) {
        this.gp = gp;
        this.orders = new ArrayList<>();
        this.availableRecipes = new ArrayList<>();
    }

    public void addRecipe(Recipe r) {
        availableRecipes.add(r);
    }

    public void update() {
        if(gp.gameState != gp.playState) return;
        double delta = 1.0 / 60.0;

        if (!availableRecipes.isEmpty() && orders.size() < MAX_ORDERS) {
            timeSinceLastSpawn += delta;

            if (orders.isEmpty() && timeSinceLastSpawn > 3.0) {
                spawnOrder();
                timeSinceLastSpawn = 0;
            }
            else if (timeSinceLastSpawn >= SPAWN_INTERVAL) {
                spawnOrder();
                timeSinceLastSpawn = 0.0;
            }
        }

        for (int i = orders.size() - 1; i >= 0; i--) {
            Order o = orders.get(i);
            boolean isExpired = o.update(delta);

            if (isExpired) {
                gp.addFailure();
                GamePanel.addScore(-o.getPenalty());

                System.out.println("Order Expired: " + o.getRecipe().getName());
                orders.remove(i);
            }
        }
    }

    private void spawnOrder() {
        if (availableRecipes.isEmpty()) return;

        int idx = random.nextInt(availableRecipes.size());
        Recipe randomRecipe = availableRecipes.get(idx);

        Order newOrder = new Order(
                orderCounter++,
                randomRecipe,
                BASE_REWARD,
                BASE_PENALTY,
                BASE_TIME_LIMIT
        );

        orders.add(newOrder);
         System.out.println("New Order Generated: " + randomRecipe.getName());
    }

    public Order validateOrder(Dish dishServed) {
        if (orders.isEmpty()) return null;
        if (dishServed == null) return null;

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (dishServed.isDone(order.getRecipe())) {
                System.out.println("Order Fulfilled: " + order.getRecipe().getName());
                orders.remove(i);
                return order;
            }
        }
        return null;
    }

    public List<Order> getActiveOrders() {
        return orders;
    }
}