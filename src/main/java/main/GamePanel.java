package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Chef;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 3;

    

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    int FPS =  60;

    TileManager tm = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();

    Thread gameThread;

    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter as = new AssetSetter(this);
    public Chef chef1;
    public Chef chef2;
    public Chef activeChef;
    public SuperObject[] obj = new SuperObject[10];
    public UI ui = new UI(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,  screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame(){

        chef1 = new Chef(this, keyH, tileSize * 3, tileSize * 3);
        chef2 = new Chef(this, keyH, tileSize * 3, tileSize * 7);
        activeChef = chef1;
        
        as.setObject();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread.isAlive()) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
//                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (keyH.swapPressed) {
            if (activeChef == chef1) {
                activeChef = chef2;
            } else {
                activeChef = chef1;
            }
            keyH.swapPressed = false;

            
        }

        if (chef1 != null) chef1.update();
        if (chef2 != null) chef2.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (tm != null) {
            tm.draw(g2);
        }

        for (int i = 0; i<obj.length; i++){
            if(obj[i] != null){
                obj[i].draw(g2, this);
            }
        }

        if (chef1 != null) {
            chef1.draw(g2);
        }
        if (chef2 != null) {
            chef2.draw(g2);
        }

        
        if (ui != null && activeChef != null) {
        
            ui.draw(g2);
        }
        g2.dispose();
    }
}
