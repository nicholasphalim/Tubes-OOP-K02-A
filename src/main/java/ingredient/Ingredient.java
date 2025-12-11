package ingredient;

import main.GamePanel;
import preparable.Preparable;
import item.Item;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Ingredient extends Item implements Preparable, Cloneable {
    private String ingName;
    private State state;

    public Ingredient (String name, GamePanel gp) {
        super(gp);
        this.name = name;
        this.ingName = name;
        this.state = State.RAW;
    }

    public String getName() {
        return name;
    }

    public String getIngName() {return ingName;}

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

    public void updateImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/" + ingName + "_" + state + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ingredient clone() throws CloneNotSupportedException {
        return (Ingredient) super.clone();
    }

}
