package main;

import ingredient.Dough;
import ingredient.Tomato;
import item.Oven;
import object.OBJ_Dough;
import station.*;

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
            WashingStation ws1 = null;
            WashingStation ws2 = null;

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

                        case 7: // Ingredient Station
                            gp.obj[objIndex] = new IngredientStorage(gp, new Dough(gp));
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 12: // Ingredient Station Dough
                            gp.obj[objIndex] = new IngredientStorage(gp, new Dough(gp));
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 13: // Ingredient Station Tomato
                            gp.obj[objIndex] = new IngredientStorage(gp, new Tomato(gp));
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 14: // Ingredient Station
                            gp.obj[objIndex] = new IngredientStorage(gp, new Dough(gp));
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 15: // Ingredient Station
                            gp.obj[objIndex] = new IngredientStorage(gp, new Dough(gp));
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 4: // trash Station
                            gp.obj[objIndex] = new TrashStation(gp);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 8: // trash Station
                            gp.obj[objIndex] = new PlateStorage(gp);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            objIndex++;
                            break;

                        case 10: // Washing Station (Clean Stack)
                            gp.obj[objIndex] = new WashingStation(gp, false);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            ws2 = (WashingStation) gp.obj[objIndex];
                            objIndex++;
                            break;

                        case 6: // Washing Station
                            gp.obj[objIndex] = new WashingStation(gp, true);
                            gp.obj[objIndex].x = col * gp.tileSize;
                            gp.obj[objIndex].y = row * gp.tileSize;
                            ws1 = (WashingStation) gp.obj[objIndex];
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
            ws1.setCleanStack(ws2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
