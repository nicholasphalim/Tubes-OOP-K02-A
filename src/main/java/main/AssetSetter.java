package main;

import ingredient.Dough;
import ingredient.Tomato;
import item.Oven;
import object.OBJ_Dough;
import station.AssemblyStation;
import station.CookingStation;
import station.CuttingStation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetSetter {
    GamePanel gp;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        loadObjectMap("/maps/map1.txt");
    }

    public void loadObjectMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;
            int objIndex = 0;

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {

                String line = br.readLine();

                while (col < gp.maxScreenCol) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);

                    switch (num) {
                        case 2: // Cooking Station
                            Oven oven = new Oven(gp);
                            gp.obj[objIndex] = new CookingStation(gp, oven);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 3: // Assembly Station
                            gp.obj[objIndex] = new AssemblyStation(gp);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 5: // Cutting Station
                            gp.obj[objIndex] = new CuttingStation(gp);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;
                    }
                    col++;
                }

                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
