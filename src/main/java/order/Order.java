package order;

import recipe.Recipe;

public class Order {
    private int id;
    private Recipe recipe;
    private int reward;
    private int penalty;

    private float maxTime;
    private float currentTime;

    public Order (int id, Recipe recipe, int reward, int penalty,  float timeLimit) {
        this.id = id;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        this.maxTime = timeLimit;
        this.currentTime = timeLimit;
    }

    public boolean update(double deltaTime) {
        currentTime -= deltaTime;
        return currentTime <= 0; // Return true jika waktu habis (expired)
    }

    public float getProgress() {
        return currentTime / maxTime; // Untuk UI bar (0.0 sampai 1.0)
    }

    public int getId() { return id; }
    public Recipe getRecipe() { return recipe; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }
    public float getCurrentTime() { return currentTime; }
}
