package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Dough extends SuperObject{
    public OBJ_Dough(GamePanel gp){
        super(gp);
        name = "Dough";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Dough.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

  
}
