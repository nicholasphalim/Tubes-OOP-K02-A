package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Dough extends Ingredient implements Preparable {
    public Dough(GamePanel gp) {
        super("Dough", gp);
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Dough.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }
}
