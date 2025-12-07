package object;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int x,y;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    // Add object type
    public int type;
    public static final int TYPE_PICKUP = 0; // For objects that can be picked up
    public static final int TYPE_INTERACTABLE = 1; // For objects with specific interactions (e.g., doors, switches)

    public void interact(entity.Player player) {
        // This method will now only be called for TYPE_INTERACTABLE objects.
        // Subclasses can override this for specific interactions.
    }

    public void draw(Graphics2D g2, GamePanel gp){
        g2.drawImage(image,x,y,gp.tileSize,gp.tileSize,null);
    }
}