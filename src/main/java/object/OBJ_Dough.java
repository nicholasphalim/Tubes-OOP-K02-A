package object;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Dough extends SuperObject{
    public OBJ_Dough(){
        name = "Dough";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/dough.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collision = false;
        type = TYPE_PICKUP;
    }

  
}
