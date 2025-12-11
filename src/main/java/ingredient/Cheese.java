package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Cheese extends Ingredient {
    public Cheese(GamePanel gp) {
        super("Cheese", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Cheese.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

    public Cheese(Cheese target) {
        super(target);
        this.image = target.image;
        this.collision = target.collision;
        this.type = target.type;
    }

    public Cheese copy() {
        return new Cheese(this);
    }
}
