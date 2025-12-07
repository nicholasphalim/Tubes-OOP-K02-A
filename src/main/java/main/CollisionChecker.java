package main;

import entity.Entity;
import entity.Player;
import entity.Direction;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Player player){
        
        int entityLeftX = player.getPosition().x + player.solidArea.x;
        int entityRightX = player.getPosition().x + player.solidArea.x + player.solidArea.width;
        int entityTopY = player.getPosition().y + player.solidArea.y;
        int entityBottomY = player.getPosition().y + player.solidArea.y + player.solidArea.height;

        int entityLeftCol = entityLeftX/gp.tileSize;
        int entityRightCol = entityRightX/gp.tileSize;
        int entityTopRow = entityTopY/gp.tileSize;
        int entityBottomRow = entityBottomY/gp.tileSize;

        int tileNum1, tileNum2;

        switch (player.getDirection()){
            case UP:
                entityTopRow = (entityTopY - player.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision){
                    player.collisionOn = true;
                }
                break;
            case DOWN:
                entityBottomRow = (entityBottomY + player.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision ||  gp.tm.tile[tileNum2].collision) {
                    player.collisionOn = true;
                }
                break;
            case LEFT:
                entityLeftCol = (entityLeftX - player.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    player.collisionOn = true;
                }
                break;
            case RIGHT:
                entityRightCol = (entityRightX + player.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    player.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Player player, boolean isPlayer){
        int index = 999;
        for(int i = 0; i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                player.solidArea.x = player.getPosition().x + player.solidArea.x;
                player.solidArea.y = player.getPosition().y + player.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].x +  gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].y + gp.obj[i].solidArea.y;

                switch(player.getDirection()){
                    case UP:
                        player.solidArea.y -= player.speed;
                        if(player.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                player.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case DOWN:
                        player.solidArea.y += player.speed;
                        if(player.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                player.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case LEFT:
                        player.solidArea.x -= player.speed;
                        if(player.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                player.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case RIGHT:
                        player.solidArea.x += player.speed;
                        if(player.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                player.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                }
                player.solidArea.x = player.solidAreaDefaultX;
                player.solidArea.y = player.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}
