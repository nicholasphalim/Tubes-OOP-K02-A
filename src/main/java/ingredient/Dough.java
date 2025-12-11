package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Dough extends Ingredient {
    public Dough(GamePanel gp) {
        super("Dough", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/dough.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

    public Dough(Dough target) {
        super(target);
        this.image = target.image;
        this.collision = target.collision;
        this.type = target.type;
    }

    public Dough copy() {
        return new Dough(this);
    }
}
