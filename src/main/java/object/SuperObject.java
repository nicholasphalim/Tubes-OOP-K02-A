package object;

import entity.Entity;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int x, y;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public int type;
    public static final int TYPE_PICKUP = 0;
    public static final int TYPE_STATION = 1;

    protected GamePanel gp;

    public SuperObject(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }

    public void interact(Entity entity) {
    }
}