package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Chicken extends Ingredient {
    public Chicken(GamePanel gp) {
        super("Chicken", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Chicken.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

    public Chicken(Chicken target) {
        super(target);
        this.image = target.image;
        this.collision = target.collision;
        this.type = target.type;
    }

    public Chicken copy() {
        return new Chicken(this);
    }
}
