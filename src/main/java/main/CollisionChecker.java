package main;

import entity.Chef;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Chef chef){
        
        int entityLeftX = chef.getPosition().x + chef.solidArea.x;
        int entityRightX = chef.getPosition().x + chef.solidArea.x + chef.solidArea.width;
        int entityTopY = chef.getPosition().y + chef.solidArea.y;
        int entityBottomY = chef.getPosition().y + chef.solidArea.y + chef.solidArea.height;

        int entityLeftCol = entityLeftX/gp.tileSize;
        int entityRightCol = entityRightX/gp.tileSize;
        int entityTopRow = entityTopY/gp.tileSize;
        int entityBottomRow = entityBottomY/gp.tileSize;

        int tileNum1, tileNum2;

        switch (chef.getDirection()){
            case UP:
                entityTopRow = (entityTopY - chef.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision){
                    chef.collisionOn = true;
                }
                break;
            case DOWN:
                entityBottomRow = (entityBottomY + chef.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision ||  gp.tm.tile[tileNum2].collision) {
                    chef.collisionOn = true;
                }
                break;
            case LEFT:
                entityLeftCol = (entityLeftX - chef.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    chef.collisionOn = true;
                }
                break;
            case RIGHT:
                entityRightCol = (entityRightX + chef.speed) / gp.tileSize;
                tileNum1 = gp.tm.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tm.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tm.tile[tileNum1].collision || gp.tm.tile[tileNum2].collision) {
                    chef.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Chef chef, boolean isPlayer){
        int index = 999;
        for(int i = 0; i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                chef.solidArea.x = chef.getPosition().x + chef.solidArea.x;
                chef.solidArea.y = chef.getPosition().y + chef.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].x +  gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].y + gp.obj[i].solidArea.y;

                switch(chef.getDirection()){
                    case UP:
                        chef.solidArea.y -= chef.speed;
                        if(chef.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                chef.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case DOWN:
                        chef.solidArea.y += chef.speed;
                        if(chef.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                chef.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case LEFT:
                        chef.solidArea.x -= chef.speed;
                        if(chef.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                chef.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                    case RIGHT:
                        chef.solidArea.x += chef.speed;
                        if(chef.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                chef.collisionOn = true;
                            }
                            if(isPlayer){
                                index = i;
                            }
                        }
                        break;
                }
                chef.solidArea.x = chef.solidAreaDefaultX;
                chef.solidArea.y = chef.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}
