package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Sausage extends Ingredient {
    public Sausage(GamePanel gp) {
        super("Sausage", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Sausage.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

    public Sausage(Sausage target) {
        super(target);
        this.image = target.image;
        this.collision = target.collision;
        this.type = target.type;
    }

    public Sausage copy() {
        return new Sausage(this);
    }
}
