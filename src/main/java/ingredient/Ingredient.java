package ingredient;

import main.GamePanel;
import preparable.Preparable;
import item.Item;

public class Ingredient extends Item implements Preparable {
    private String ingName;
    private State state;

    public Ingredient (String name, GamePanel gp) {
        super(gp);
        this.name = name;
        this.ingName = name;
        this.state = State.RAW;
    }

    public Ingredient (Ingredient target) {
        super(target.gp);
        this.name = target.name;
        this.ingName = target.ingName;
        this.state = target.state;
    }

    public String getName() {
        return name;
    }

    public State getState(){
        return state;
    }

    public void changeState(State newState) {
        this.state = newState;
    }

    @Override
    public boolean canBeChopped() {
        return state == State.RAW;
    }

    @Override
    public boolean canBeCooked() {
        return (state == State.RAW || state == State.CHOPPED);
    }

    @Override
    public boolean canBePlacedOnPlate(){
        return state == State.COOKED;
    }

    @Override
    public void chop(){
        if(canBeChopped()){
            changeState(State.CHOPPED);
        }
    }

    @Override
    public void cook(){
        if(canBeCooked()){
            changeState(State.COOKING);
        }
    }

    public void finishCooking(boolean burned) {
        if(burned) {
            changeState(State.BURNED);
        } else {
            changeState(State.COOKED);
        }
    }

}
