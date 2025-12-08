package order;

import recipe.Recipe;

public class Order {
    private int urutan;
    private Recipe recipe;
    private int reward;
    private int penalty;

    public Order (int urutan, Recipe recipe, int reward, int penalty){
        this.urutan = urutan;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
    }

    public int getUrutan() {
        return urutan;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getReward (){
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }
}
