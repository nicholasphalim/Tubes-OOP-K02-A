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
    // STATIC SCORE
    public static int playerScore = 0;

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;

    public final int mapWidth = tileSize * maxScreenCol;
    public final int mapHeight = tileSize * maxScreenRow;

    public final int uiPanelWidth = 200;
    final int screenWidth = mapWidth + uiPanelWidth;
    final int screenHeight = mapHeight;

    int FPS = 60;

    // SYSTEM
    public TileManager tm = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter as = new AssetSetter(this);
    public UI ui = new UI(this);

    // GAME ENTITIES
    public Chef chef1;
    public Chef chef2;
    public Chef activeChef;
    public SuperObject[] obj = new SuperObject[64];

    public OrderList orderList = new OrderList(this);

    // GAME STATE & RULES
    public int gameState;
    public final int titleState = 0;
    public final int stageState = 1;
    public final int playState = 2;
    public final int finishState = 3; // Game Over
    public final int tutorialState = 4;

    // TIMER
    public final double INITIAL_TIME = 180.0;
    public double gameTimer = INITIAL_TIME;

    // MIN SCORE
    public final int MIN_SCORE_TO_PASS = 300;

    // FAILED ORDER
    public int consecutiveFailures = 0;
    public final int MAX_CONSECUTIVE_FAILURES = 5;

    // FINISH STATE
    public boolean isWin = false;
    public String gameOverMessage = "";

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,  screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.orderList = new OrderList(this);
        this.gameState = titleState;
    }

    public void setupGame(){
        chef1 = new Chef(this, keyH, tileSize * 3, tileSize * 3);
        chef2 = new Chef(this, keyH, tileSize * 3, tileSize * 7);
        activeChef = chef1;
        as.setObject();
        setupRecipes();
    }

    private void setupRecipes() {
        orderList = new OrderList(this);
        Recipe pizza_margherita = new Recipe("Pizza Margherita");
        pizza_margherita.addIngredientRequirement(new Ingredient("Dough", this), State.COOKED);
        pizza_margherita.addIngredientRequirement(new Ingredient("Tomato", this), State.COOKED);
        pizza_margherita.addIngredientRequirement(new Ingredient("Cheese", this), State.COOKED);
        orderList.addRecipe(pizza_margherita);

        Recipe pizza_sosis = new Recipe("Pizza Sosis");
        pizza_sosis.addIngredientRequirement(new Ingredient("Dough", this), State.COOKED);
        pizza_sosis.addIngredientRequirement(new Ingredient("Tomato", this), State.COOKED);
        pizza_sosis.addIngredientRequirement(new Ingredient("Cheese", this), State.COOKED);
        pizza_sosis.addIngredientRequirement(new Ingredient("Sausage", this), State.COOKED);
        orderList.addRecipe(pizza_sosis);

        Recipe pizza_ayam = new Recipe("Pizza Ayam");
        pizza_ayam.addIngredientRequirement(new Ingredient("Dough", this), State.COOKED);
        pizza_ayam.addIngredientRequirement(new Ingredient("Tomato", this), State.COOKED);
        pizza_ayam.addIngredientRequirement(new Ingredient("Cheese", this), State.COOKED);
        pizza_ayam.addIngredientRequirement(new Ingredient("Ayam", this), State.COOKED);
        orderList.addRecipe(pizza_ayam);
    }

    public void retryGame() {
        playerScore = 0;

        gameTimer = INITIAL_TIME;
        consecutiveFailures = 0;
        isWin = false;
        gameOverMessage = "";

        chef1.setDefaultState();
        chef1.setInventory(null);
        chef2.setDefaultState();
        chef2.setInventory(null);

        setupGame();
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

        while (gameThread != null) {
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
        if (gameState == playState) {
            orderList.update();
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
            if (gameTimer > 0) {
                gameTimer -= (double) 1/60;
            } else {
                gameTimer = 0;
                checkWinCondition();
            }
        }
    }

    private void checkWinCondition() {
        gameState = finishState;

        if (playerScore >= MIN_SCORE_TO_PASS) {
            isWin = true;
            gameOverMessage = "LEVEL COMPLETED!";
        } else {
            isWin = false;
            gameOverMessage = "TIME'S UP! SCORE TOO LOW";
        }
    }

    public void addFailure() {
        consecutiveFailures++;
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            gameState = finishState;
            isWin = false;
            gameOverMessage = "TOO MANY FAILURES!";
        }
    }

    public void resetFailureCount() {
        consecutiveFailures = 0;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if(gameState == titleState) {
            ui.draw(g2);
        } else {
            tm.draw(g2);
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
            ui.draw(g2);
        }

        g2.dispose();
    }

    public static int getScore() {
        return playerScore;
    }

    public static void addScore(int diff) {
        GamePanel.playerScore += diff;
    }
}
