package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Tomato extends Ingredient {
    public Tomato(GamePanel gp) {
        super("Tomato", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Tomato.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

    public Tomato(Tomato target) {
        super(target);
        this.image = target.image;
        this.collision = target.collision;
        this.type = target.type;
    }

    public Tomato copy() {
        return new Tomato(this);
    }
}
