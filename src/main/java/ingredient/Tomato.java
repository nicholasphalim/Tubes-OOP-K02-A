package ingredient;

import main.GamePanel;
import preparable.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Tomato extends Ingredient implements Preparable {
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
}
