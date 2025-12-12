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
    private final int BASE_TIME_LIMIT = 120;
    private double timeSinceLastSpawn = 0.0;
    private final double SPAWN_INTERVAL = 15.0;
    public OrderList(GamePanel gp) {
        this.gp = gp;
        this.orders = new ArrayList<>();
        this.availableRecipes = new ArrayList<>();
//        System.out.println("OrderList created");
    }

    public void addRecipe(Recipe r) {
        availableRecipes.add(r);
//        System.out.println("Recipe added: " + r.getName() + " (Total recipes: " + availableRecipes.size() + ")");
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
        if (availableRecipes.isEmpty()) {
//            System.out.println("Cannot spawn order - no available recipes!");
            return;
        }

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
//        System.out.println("New Order Generated: " + randomRecipe.getName() + " (Total active orders: " + orders.size() + ")");
    }

    public Order validateOrder(Dish dishServed) {
//        System.out.println("\nvalidateOrder() called");
//        System.out.println("   orders.size() = " + orders.size());
//        System.out.println("   orders.isEmpty() = " + orders.isEmpty());
//        System.out.println("   availableRecipes.size() = " + availableRecipes.size());
//
//        if (orders.isEmpty()) {
//            System.out.println("No active orders!");
//            return null;
//        }
        if (dishServed == null) return null;

//        System.out.println("\n========== VALIDATING ORDER ==========");
//        System.out.println("Active orders (" + orders.size() + "):");
//        for (int i = 0; i < orders.size(); i++) {
//            Order order = orders.get(i);
//            System.out.println("  Order #" + (i + 1) + ": " + order.getRecipe().getName());
//            System.out.println("    Required ingredients:");
//            for (java.util.Map.Entry<ingredient.Ingredient, ingredient.State> entry : order.getRecipe().getIngredientRequirements().entrySet()) {
//                System.out.println("      - " + entry.getKey().getIngName() + " [State: " + entry.getValue() + "]");
//            }
//        }
//        System.out.println("======================================\n");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

//            System.out.println("Checking against: " + order.getRecipe().getName());
            if (dishServed.isDone(order.getRecipe())) {
//                System.out.println("Order Fulfilled: " + order.getRecipe().getName());
                orders.remove(i);
                return order;
            } else {
//                System.out.println("Does not match: " + order.getRecipe().getName());
            }
        }
//        System.out.println("No matching order found!\n");
        return null;
    }

    public List<Order> getActiveOrders() {
        return orders;
    }

    public void clearAll() {
        orders.clear();
        availableRecipes.clear();
        orderCounter = 1;
        timeSinceLastSpawn = 0.0;
//        System.out.println("OrderList cleared");
    }
}