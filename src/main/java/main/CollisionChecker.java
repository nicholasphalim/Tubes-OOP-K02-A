package main;

import entity.Entity;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity){
        int entityLeftX = entity.x + entity.solidArea.x;
        int entityRightX = entity.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopY = entity.y + entity.solidArea.y;
        int entityBottomY = entity.y + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftX/gp.tileSize;
        int entityRightCol = entityRightX/gp.tileSize;
        int entityTopRow = entityTopY/gp.tileSize;
        int entityBottomRow = entityBottomY/gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction){
            case "up":
                entityTopRow = (entityTopY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision ||  gp.tm.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Entity entity, boolean isPlayer){
        int index = 999;
        for(int i = 0; i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                entity.solidArea.x = entity.x + entity.solidArea.x;
                entity.solidArea.y = entity.y + entity.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].x +  gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].y + gp.obj[i].solidArea.y;

                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}
