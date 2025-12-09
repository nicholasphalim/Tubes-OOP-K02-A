package main;

import ingredient.Dough;
import ingredient.Tomato;
import item.Oven;
import object.OBJ_Dough;
import station.AssemblyStation;
import station.CookingStation;
import station.CuttingStation;

public class AssetSetter {
    GamePanel gp;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject(){
        gp.obj[0] = new Tomato(gp);
        gp.obj[0].x = 3*gp.tileSize;
        gp.obj[0].y = 3*gp.tileSize;

        gp.obj[1] = new CuttingStation(gp);
        gp.obj[1].x = 4*gp.tileSize;
        gp.obj[1].y = 0*gp.tileSize;

        gp.obj[2] = new CuttingStation(gp);
        gp.obj[2].x = 8*gp.tileSize;
        gp.obj[2].y = 0*gp.tileSize;

        gp.obj[3] = new AssemblyStation(gp);
        gp.obj[3].x = 3*gp.tileSize;
        gp.obj[3].y = 4*gp.tileSize;

        gp.obj[4] = new Dough(gp);
        gp.obj[4].x = 2*gp.tileSize;
        gp.obj[4].y = 3*gp.tileSize;

        Oven oven  = new Oven(gp);

        gp.obj[5] = new CookingStation(gp, oven);
        gp.obj[5].x = 1*gp.tileSize;
        gp.obj[5].y = 7*gp.tileSize;
    }
}
