package main;

import entity.Chef;
import ingredient.Ingredient;
import ingredient.State;
import object.SuperObject;
import order.OrderList;
import recipe.Recipe;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

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
    public Chef chef = new Chef(this, keyH);
    public SuperObject[] obj = new SuperObject[64];
    public UI ui = new UI(this);
    public OrderList orderList = new OrderList();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,  screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.orderList = new OrderList();
    }

    public void setupGame(){
        as.setObject();
        Recipe pizza_margherita = new Recipe("Pizza Margherita");
         pizza_margherita.addIngredientRequirement(new Ingredient("Dough", this), State.COOKED);
         pizza_margherita.addIngredientRequirement(new Ingredient("Tomato", this), State.COOKED);
        orderList.addRecipe(pizza_margherita);
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
        chef.update();
        orderList.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        tm.draw(g2);
        for (int i = 0; i<obj.length; i++){
            if(obj[i] != null){
                obj[i].draw(g2, this);
            }
        }

        chef.draw(g2);

        ui.draw(g2);

        g2.dispose();
    }
}
