package main;

import object.OBJ_Dough;

public class AssetSetter {
    GamePanel gp;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject(){
        gp.obj[0] = new OBJ_Dough();
        gp.obj[0].x = 3*gp.tileSize;
        gp.obj[0].y = 3*gp.tileSize;
    }
}
