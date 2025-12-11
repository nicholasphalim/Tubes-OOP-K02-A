package entity;

import ingredient.Dough;
import ingredient.State;
import inventory.Plate;
import item.Dish;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import item.Item;
import main.GamePanel;
import main.KeyHandler;
import object.SuperObject;
import preparable.Preparable;
import station.*;

public class Chef extends Entity {
    public GamePanel gp;
    KeyHandler keyH;

    private String id;
    private String name;
    private Direction direction;
    private Item inventory;
    public Action busyState;
    public Station currentInteractionStation;
    private int startX, startY;

    private int dashCooldown = 20;  
    private int cooldownCounter = 0;
    private boolean canDash = true;

    private boolean isDashing = false; 
    private int dashTargetX, dashTargetY; 
    private int dashSpeed = 16; 

    private boolean dashKeyConsumed = false;

    public Chef(GamePanel gp, KeyHandler keyH, int startX, int startY) {

        super(0,0);

        this.gp = gp;
        this.keyH = keyH;

        this.startX = startX;
        this.startY = startY;

        solidArea = new Rectangle(12,12,24,24);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultState();
        getPlayerImage();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDefaultState() {
        position.x = startX;
        position.y = startY ;
        speed = 4;
        direction = Direction.DOWN;
    }

    public Item getInventory() {
        return inventory;
    }

    public void setInventory(Item inventory) {
        this.inventory = inventory;
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

    public void update() {

        
        if (isDashing) {
            handleDash();
            return;
        }
        
        if (this != gp.activeChef) {
            return; 
        }
        
        handleDash();

        if (isDashing) {
            return;
        }

        boolean isMoving = false;

        if (keyH.upPressed) {
            direction = Direction.UP;
            isMoving = true;
        } else if (keyH.downPressed) {
            direction = Direction.DOWN;
            isMoving = true;
        }

        if (keyH.upPressed || keyH.downPressed) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);

            if (!collisionOn) {
                if (direction == Direction.UP) position.y -= speed;
                if (direction == Direction.DOWN) position.y += speed;
            }
        }

        if (keyH.leftPressed) {
            direction = Direction.LEFT;
            isMoving = true;
        } else if (keyH.rightPressed) {
            direction = Direction.RIGHT;
            isMoving = true;
        }

        if (keyH.leftPressed || keyH.rightPressed) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);

            if (!collisionOn) {
                if (direction == Direction.LEFT) position.x -= speed;
                if (direction == Direction.RIGHT) position.x += speed;
            }
        }

         int prevX = position.x; 
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter >= 10) {
                if (spriteNum == 1) spriteNum = 2;
                else if (spriteNum == 2) spriteNum = 1;
                spriteCounter = 0;
            }

            if (busyState != null) {
                busyState = null;
                currentInteractionStation = null;
            }
        }

        if (keyH.eKeyPressed) {
            pickDrop();
            keyH.eKeyPressed = false;
        }

        if (keyH.cKeyPressed) {
            interact();
            keyH.cKeyPressed = false;
        }
   }

    public void pickDrop(){
        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;
        int objOnPlayerTileIndex = getObjectIndex(position.x, position.y);

        if (objOnPlayerTileIndex != 999 && gp.obj[objOnPlayerTileIndex].type == SuperObject.TYPE_PICKUP) {
            pickUpObject(objOnPlayerTileIndex);
            return;
        }

        int interactX = position.x;
        int interactY = position.y;

        switch (direction) {
            case UP:
                interactY -= gp.tileSize;
                break;
            case DOWN:
                interactY += gp.tileSize;
                break;
            case LEFT:
                interactX -= gp.tileSize;
                break;
            case RIGHT:
                interactX += gp.tileSize;
                break;
        }

        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;

        int objIndex = getObjectIndex(interactX, interactY);

        if(objIndex != 999) {
            if (gp.obj[objIndex].type == SuperObject.TYPE_PICKUP) {
                pickUpObject(objIndex);
            }
            else if (gp.obj[objIndex] instanceof Station) {
                Station station = (Station) gp.obj[objIndex];
                this.currentInteractionStation = station;

                if (station instanceof CookingStation) {
                    handleCookingStationInteraction((CookingStation) station);
                }
                else if (station instanceof TrashStation) {
                    handleTrashStationInteraction((TrashStation) station);
                }
                else {
                    handleGeneralStationInteraction(station);
                }
            }
        } else {
            if(inventory != null){
                dropObject();
            }
        }
    }

    private void handleTrashStationInteraction(TrashStation ts) {
        if (inventory == null) return;

        if (inventory instanceof Plate) {
            Plate plate = (Plate) inventory;

            if (plate.dish != null) {
                boolean success = ts.placeItem(plate.dish);
                if (success) {
                    plate.dish = null;
                    gp.ui.showMessage("Cleaned plate contents");
                }
            } else {
                ts.placeItem(plate);
            }
        }
        else {
            boolean success = ts.placeItem(inventory);
            if (success) {
                inventory = null;
            }
        }
    }

    private void handleCookingStationInteraction(CookingStation cs) {

        if (inventory != null) {

            if (inventory instanceof Plate) {
                Plate p = (Plate) inventory;
                if (p.dish != null) {
                    boolean success = cs.placeItem(p.dish);
                    if (success) {
                        gp.ui.showMessage("Placed dish into oven");
                        p.dish = null;
                    }
                } else {

                    Item cookedItem = cs.takeItem();
                    if (cookedItem != null) {
                        if (cookedItem instanceof Dish) {
                            p.dish = (Dish) cookedItem;
                            gp.ui.showMessage("Plated the cooked dish!");
                        } else {
                            gp.ui.showMessage("Took item with plate!");
                        }
                    } else {
                        gp.ui.showMessage("Oven is empty!");
                    }
                }
            }
            else if (inventory instanceof Dish || (inventory instanceof Dough && ((Dough) inventory).getState() == State.CHOPPED)) {
                boolean success = cs.placeItem(inventory);
                if (success) {
                    gp.ui.showMessage("Placed dish into oven");
                    inventory = null;
                }
            }
            else {
                gp.ui.showMessage("Can only cook Dishes!");
            }
        }

        else {
            Item itemTaken = cs.takeItem();
            if (itemTaken != null) {
                inventory = itemTaken;
                gp.ui.showMessage("Took " + itemTaken.name);
            } else {
                gp.ui.showMessage("Oven is empty!");
            }
        }
    }

    private void handleGeneralStationInteraction(Station station) {
        if (inventory != null) {
            boolean success = station.placeItem(inventory);
            if (success) {
                gp.ui.showMessage("You placed " + inventory.name);
                inventory = null;
            }
        } else {
            Item itemTaken = station.takeItem();
            if (itemTaken != null) {
                inventory = itemTaken;
            } else {
                gp.ui.showMessage("Nothing to take yet!");
            }
        }
    }

    public void pickUpObject(int index){

        if(index != 999){
            if(inventory == null){
                inventory = (Item) gp.obj[index];
                gp.obj[index] = null;
                gp.ui.showMessage("You picked up object " + inventory.name);
            } else {
                gp.ui.showMessage("You can only carry one object at a time!");
            }

        }
    }

    public void dropObject(){
        if (inventory != null) {

            int currentX = position.x;
            int currentY = position.y;

            int dropX = currentX;
            int dropY = currentY;

            switch (direction) {
                case UP: dropY -= gp.tileSize; break;
                case DOWN: dropY += gp.tileSize; break;
                case LEFT: dropX -= gp.tileSize; break;
                case RIGHT: dropX += gp.tileSize; break;
            }

            int col = (dropX + gp.tileSize / 2) / gp.tileSize;
            int row = (dropY + gp.tileSize / 2) / gp.tileSize;

            if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
                int tileNum = gp.tm.mapTileNum[col][row];

                if (gp.tm.tile[tileNum].collision) {
                    dropX = currentX;
                    dropY = currentY;
                }
            }

            for (int i = 0; i < gp.obj.length; i++) {
                if (gp.obj[i] == null) {
                    gp.obj[i] = inventory;

                    gp.obj[i].x = dropX;
                    gp.obj[i].y = dropY;

                    inventory = null;
                    gp.ui.showMessage("You dropped " + gp.obj[i].name);
                    break;
                }
            }
        } else {
            gp.ui.showMessage("You have nothing to drop!");
        }
    }

    public void interact(){
        int interactX = position.x;
        int interactY = position.y;

        switch (direction) {
            case UP:
                interactY -= gp.tileSize;
                break;
            case DOWN:
                interactY += gp.tileSize;
                break;
            case LEFT:
                interactX -= gp.tileSize;
                break;
            case RIGHT:
                interactX += gp.tileSize;
                break;
        }

        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;

        int objIndex = getObjectIndex(interactX, interactY);

        if(objIndex != 999) {
            if (gp.obj[objIndex] instanceof Station) {
                Station station = (Station) gp.obj[objIndex];
                this.currentInteractionStation = station;
                gp.obj[objIndex].interact(this);
            } else {
                gp.ui.showMessage("No interaction");
                this.currentInteractionStation = null;
            }
        } else {
            gp.ui.showMessage("No station to interact in front of you!");
        }
    }

    public int getObjectIndex(int x, int y) {
        int index = 999;
        boolean found = false;

        Rectangle checkArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
        for(int i = 0; i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                gp.obj[i].solidArea.x = gp.obj[i].x + gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].y + gp.obj[i].solidAreaDefaultY;

                if(checkArea.intersects(gp.obj[i].solidArea)){
                    index = i;
                    found = true;
                }

            gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
            gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;

            if(found) break;
            }
        }
        return index;
    }

    
    private void calculateDashTarget() {
        int safeX = position.x;
        int safeY = position.y;
        
        // simpan speed asli 
        int originalSpeed = speed;
        speed = 0; 

        // cek blok 1, 2, 3 didepannya
        for (int i = 1; i <= 2; i++) {
            int jumpDistance = gp.tileSize * i;         

            int checkX = position.x;
            int checkY = position.y;
            
            switch (direction) {
                case UP:    checkY -= jumpDistance; break;
                case DOWN:  checkY += jumpDistance; break;
                case LEFT:  checkX -= jumpDistance; break;
                case RIGHT: checkX += jumpDistance; break;
            }

            int originalX = position.x;
            int originalY = position.y;

            // pindahin player
            position.x = checkX;
            position.y = checkY;

            // cek collision
            collisionOn = false;
            gp.cChecker.checkTile(this);
            int objIndex = gp.cChecker.checkObject(this, true);

            position.x = originalX;
            position.y = originalY;

            if (!collisionOn && objIndex == 999) {
                safeX = checkX;
                safeY = checkY;
            } else {
                break;
            }
        }
        
        speed = originalSpeed;
        
        dashTargetX = safeX;
        dashTargetY = safeY;
    }

    private void moveTowardsDashTarget() {
        int xDist = Math.abs(position.x - dashTargetX);
        int yDist = Math.abs(position.y - dashTargetY);
        
        if (xDist <= dashSpeed && yDist <= dashSpeed) {
            position.x = dashTargetX;
            position.y = dashTargetY;
            isDashing = false;
        } 
        else {
            if (position.x < dashTargetX) position.x += dashSpeed;
            else if (position.x > dashTargetX) position.x -= dashSpeed;
            
            if (position.y < dashTargetY) position.y += dashSpeed;
            else if (position.y > dashTargetY) position.y -= dashSpeed;
        }
    }

    private void handleDash() {
        if (!keyH.dashPressed) {
            dashKeyConsumed = false;
        }
        if (isDashing) {
            moveTowardsDashTarget();
            
            spriteCounter++;
            if (spriteCounter >= 5) {
                if (spriteNum == 1) spriteNum = 2;
                else spriteNum = 1;
                spriteCounter = 0;
            }
            return;
        }

        //validasi dash
        if (keyH.dashPressed && canDash && !dashKeyConsumed) {
            calculateDashTarget(); 
            
            if (dashTargetX != position.x || dashTargetY != position.y) {
                isDashing = true;
                canDash = false;
                cooldownCounter = 0;

                dashKeyConsumed = true;
            }
        }

        if (!canDash) {
            cooldownCounter++;
            if (cooldownCounter > dashCooldown) {
                canDash = true;
            }
        }
    }

    public void draw(Graphics2D g2){
//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        BufferedImage image = null;

        switch (direction) {
            case UP:
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                    image = up2;
                }
                break;
            case DOWN:
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                break;
            case LEFT:
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                break;
            case RIGHT:
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

        int drawX = position.x - (drawWidth - gp.tileSize) / 2;
        int drawY = position.y - (drawHeight - gp.tileSize) ;

        // Gambar chef
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
       
        g2.setColor(Color.red);
        g2.drawRect(position.x + solidArea.x, position.y + solidArea.y, solidArea.width, solidArea.height);

        // Gambar item
        if (inventory != null) {
            int itemSize = (int) (gp.tileSize * 0.8);
            int itemX = drawX + (drawWidth - itemSize) / 2;
            int itemY = drawY + gp.tileSize/2;

            // CASE PLATE
            if (inventory instanceof Plate) {
                Plate p = (Plate) inventory;

                if (p.image != null) {
                    g2.drawImage(p.image, itemX, itemY, itemSize, itemSize, null);
                }

                if (p.dish != null && p.dish instanceof Dish) {
                    Dish d = (Dish) p.dish;
                    int offset = 5;

                    for (Preparable prep : d.getComponents()) {
                        Item component = (Item) prep;
                        if (component.image != null) {
                            g2.drawImage(component.image, itemX, itemY - offset, itemSize, itemSize, null);
                            offset += 6;
                        }
                    }
                }
            }
            // CASE DISH
            else if (inventory instanceof Dish) {
                Dish d = (Dish) inventory;
                int offset = 0;

                for (Preparable prep : d.getComponents()) {
                    Item component = (Item) prep;
                    if (component.image != null) {
                        g2.drawImage(component.image, itemX, itemY - offset, itemSize, itemSize, null);
                        offset += 6;
                    }
                }
            }
            // CASE SINGLE ITEM
            else {
                if (inventory.image != null) {
                    g2.drawImage(inventory.image, itemX, itemY, itemSize, itemSize, null);
                }
            }
        if (this == gp.activeChef) {
            g2.setColor(Color.GREEN);

            int centerX = drawX + (drawWidth / 2);
            
            int tipY = drawY + 35; //posisi ujung bawah segitiga
            
            int triangleHeight = 10; 
            int triangleHalfWidth = 6;

  
            int[] xPoints = {
                centerX,                       //x bawah
                centerX - triangleHalfWidth,   //x atas kiri
                centerX + triangleHalfWidth    //x atas kanan
            };


            int[] yPoints = {
                tipY,                   //y bawah
                tipY - triangleHeight,  //y atas kiri
                tipY - triangleHeight   //y atas kanan
            };

            g2.fillPolygon(xPoints, yPoints, 3);
            
            // border putih
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke((float) 0.5)); 
            g2.drawPolygon(xPoints, yPoints, 3);

        }
    }
}