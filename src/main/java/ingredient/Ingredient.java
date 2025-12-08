package ingredient;

import preparable.Preparable;
import item.Item;

public class Ingredient extends Item implements Preparable {
    private String name;
    private State state;

    public Ingredient (String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.state = State.RAW;
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
