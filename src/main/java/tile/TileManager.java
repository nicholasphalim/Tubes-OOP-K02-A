package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[20];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap();
    }

    public void getTileImage(){
        try {
            tile[0] = new Tile(); //floor
            tile[0].image = ImageIO.read(getClass().getResource("/tiles/floor.png"));
            tile[1] = new Tile(); //wall
            tile[1].image = ImageIO.read(getClass().getResource("/tiles/tile211.png"));
            tile[1].collision = true;
            tile[2] = new Tile(); //oven
            tile[2].image = ImageIO.read(getClass().getResource("/tiles/oven.png"));
            tile[2].collision = true;
            tile[3] = new Tile(); //assembly station
            tile[3].image = ImageIO.read(getClass().getResource("/tiles/tile163.png"));
            tile[3].collision = true;
            tile[4] = new Tile(); //trash
            tile[4].image = ImageIO.read(getClass().getResource("/tiles/trash.png"));
            tile[4].collision = true;
            tile[5] = new Tile(); //cutting
            tile[5].image = ImageIO.read(getClass().getResource("/tiles/cutting.png"));
            tile[5].collision = true;
            tile[6] = new Tile(); //sink
            tile[6].image = ImageIO.read(getClass().getResource("/tiles/sink.png"));
            tile[6].collision = true;
            tile[7] = new Tile(); //ingredient storage
            tile[7].image = ImageIO.read(getClass().getResource("/tiles/ingstorage.png"));
            tile[7].collision = true;
            tile[8] = new Tile(); //plate storage
            tile[8].image = ImageIO.read(getClass().getResource("/tiles/platestorage.png"));
            tile[8].collision = true;
            tile[9] = new Tile(); //floor w meja
            tile[9].image = ImageIO.read(getClass().getResource("/tiles/floor2.png"));
            tile[9].collision = false;
            tile[10] = new Tile(); //sink2
            tile[10].image = ImageIO.read(getClass().getResource("/tiles/sink2.png"));
            tile[10].collision = true;
            tile[11] = new Tile(); //sink2
            tile[11].image = ImageIO.read(getClass().getResource("/tiles/serve.png"));
            tile[11].collision = true;
            tile[12] = new Tile(); //sink2
            tile[12].image = ImageIO.read(getClass().getResource("/tiles/ingstorage.png"));
            tile[12].collision = true;
            tile[13] = new Tile(); //sink2
            tile[13].image = ImageIO.read(getClass().getResource("/tiles/ingstorage.png"));
            tile[13].collision = true;
            tile[14] = new Tile(); //sink2
            tile[14].image = ImageIO.read(getClass().getResource("/tiles/ingstorage.png"));
            tile[14].collision = true;
            tile[15] = new Tile(); //sink2
            tile[15].image = ImageIO.read(getClass().getResource("/tiles/ingstorage.png"));
            tile[15].collision = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(){
        try {
            InputStream is = getClass().getResourceAsStream("/maps/map1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();

                while (col < gp.maxScreenCol){
                    String[] numbers = line.split(" ");
                    int num =  Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxScreenCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g2){
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow){

            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;

            if(col == gp.maxScreenCol){
                col = 0;
                x = 0;
                row++;
                y +=  gp.tileSize;
            }
        }
    }
}
