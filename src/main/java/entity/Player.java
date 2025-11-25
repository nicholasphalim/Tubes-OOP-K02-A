package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    int standCounter = 0;
    boolean moving = false;
    int pixelCounter = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        solidArea = new Rectangle(0,0,47,47);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultState();
        getPlayerImage();
    }

    public void setDefaultState() {
        x = gp.tileSize * 1;
        y = gp.tileSize * 1;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {

        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/tile031.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/tile030.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/tile018.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/tile019.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/tile024-mirror.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/tile025-mirror.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/tile024.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/tile025.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void update(){
        if(!moving){
            if(keyH.upPressed ||  keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                if(keyH.upPressed) {
                    direction = "up";
                } else if(keyH.downPressed) {
                    direction = "down";
                }  else if(keyH.leftPressed) {
                    direction = "left";
                } else if(keyH.rightPressed) {
                    direction = "right";
                }

                moving = true;

                collisionOn = false;
                gp.cChecker.checkTile(this);

                int objIndex = gp.cChecker.checkObject(this, true);
                pickUpObject(objIndex);
        }
        }
        if(moving){
            if(!collisionOn){
                switch (direction) {
                    case "up":
                        y -= speed;
                        break;
                    case "down":
                        y += speed;
                        break;
                    case "left":
                        x -= speed;
                        break;
                    case "right":
                        x += speed;
                        break;
                }
            }

            spriteCounter++;
            if(spriteCounter >= 10) {
                if(spriteNum == 1) {
                    spriteNum = 2;
                } else if(spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }

            pixelCounter += speed;

            if(pixelCounter == 48) {
                moving = false;
                pixelCounter = 0;
            }
        }

    }

    public void pickUpObject(int index){
        if(index != 999){
            gp.ui.showMessage("You picked up object " + gp.obj[index].name);
            gp.obj[index] = null;

        }
    }

    public void draw(Graphics2D g2){
//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        BufferedImage image = null;

        switch (direction) {
            case "up":
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                    image = up2;
                }
                break;
            case "down":
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                break;
            case "left":
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                break;
            case "right":
                if(spriteNum == 1){
                    image = right1;
                }
                if(spriteNum == 2){
                    image = right2;
                }
                break;
        }


        float scaleFactor = 2.5f;

        int drawWidth = (int) (gp.tileSize * scaleFactor);
        int drawHeight = (int) (gp.tileSize * scaleFactor);

        int drawX = x - (drawWidth - gp.tileSize) / 2;
        int drawY = y - (drawHeight - gp.tileSize) ;


        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        g2.setColor(Color.red);
        g2.drawRect(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }
}
