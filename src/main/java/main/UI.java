package main;

import entity.Action;
import ingredient.Ingredient;
import ingredient.State;
import order.Order;
import recipe.Recipe;
import station.CuttingStation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class UI {
    GamePanel gp;
    Font arial_10, arial_20, arial_20B, arial_40, arial_40B, arial_60, arial_80;
    Font orderFontTitle, orderFontBody;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    double playTime = 0.0;
    DecimalFormat df = new DecimalFormat("00");

    public int commandNum = 0;

    BufferedImage titlebg;
    BufferedImage mappreview;

    public UI(GamePanel gp){
        this.gp = gp;
        arial_10 = new Font("Arial", Font.BOLD, 14);
        arial_20 = new Font("arial", Font.PLAIN, 20);
        arial_20B = new Font("arial", Font.BOLD, 20);
        arial_40 = new Font("arial", Font.PLAIN, 40);
        arial_40B = new Font("arial", Font.BOLD, 20);
        arial_60 = new Font("arial", Font.PLAIN, 40);
        arial_80 = new Font("arial", Font.BOLD, 80);

        orderFontTitle = new Font("Arial", Font.BOLD, 16);
        orderFontBody = new Font("Arial", Font.PLAIN, 10);

        try {
            titlebg = ImageIO.read(getClass().getResourceAsStream("/res/title-background.jpg"));
            mappreview = ImageIO.read(getClass().getResourceAsStream("/res/map_preview.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMessage(String message){
        this.message = message;
        this.messageOn = true;
    }

    public void draw(Graphics2D g2){
//        g2.setFont(arial_40);
//        g2.setColor(Color.black);
////        g2.drawString("Hello", 25, 30);
//
//        playTime += (double) 1 /gp.FPS;
//        g2.setFont(g2.getFont().deriveFont(15f));
//        g2.drawString("Play Time: ", 590, 30);
//        g2.drawString(df.format(playTime), 590, 50);
//
//        g2.drawString("x: " + gp.chef.getPosition().x, 600, 70);
//
//        g2.drawString("y: " + gp.chef.getPosition().y, 600, 90);

        if (gp.gameState == gp.titleState){
            drawTitleScreen(g2);
        }

        if (gp.gameState == gp.stageState){
            drawStageScreen(g2);
        }

        if (gp.gameState == gp.tutorialState){
            drawTutorialScreen(g2);
        }

        if (gp.gameState == gp.playState){
            // Sidebar
            g2.setColor(new Color(50, 50, 50));
            g2.fillRect(gp.mapWidth, 0, gp.uiPanelWidth, gp.screenHeight);

            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(gp.mapWidth, 0, gp.mapWidth, gp.screenHeight);

            // Timer
            g2.setFont(arial_20B);
            g2.setColor(Color.white);

            int sidebarX = gp.mapWidth + 20;
            int cursorY = 40;

            int minutes = (int) gp.gameTimer / 60;
            int seconds = (int) gp.gameTimer % 60;
            String timeText = "TIME: " + minutes + ":" + df.format(seconds);
            g2.drawString(timeText, sidebarX, cursorY);

            cursorY += 30;
            g2.setFont(arial_10);

            // Score
            g2.drawString("SCORE: " + GamePanel.getScore(), sidebarX, cursorY);
            sidebarX += 90;

            // Fails
            g2.setColor(Color.red);
            g2.drawString("FAILS: " + gp.consecutiveFailures + "/" + gp.MAX_CONSECUTIVE_FAILURES, sidebarX, cursorY);

            // Draw Orders
            drawOrders(g2);

            // Message
            if(this.messageOn){
                g2.setFont(arial_20);
                g2.setColor(Color.black);
                g2.drawString(message, 20, gp.mapHeight - 20);

                messageCounter++;
                if(messageCounter > 120){
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }

        // GAME OVER STATE
        if (gp.gameState == gp.finishState) {
            drawGameOverScreen(g2);
        }
    }

    private void drawTutorialScreen(Graphics2D g2) {
        g2.drawImage(titlebg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.setColor(new Color(0, 0, 0, 220)); // Lebih gelap biar teks terbaca
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;

        g2.setFont(arial_60);
        g2.setColor(Color.white);
        text = "HOW TO PLAY";
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.tileSize * 2;
        g2.drawString(text, x, y);

        g2.setFont(arial_20B); // Bold
        g2.setColor(new Color(255, 200, 0)); // Warna Emas/Kuning
        g2.drawString("CONTROLS", gp.tileSize * 2, y + gp.tileSize);

        g2.setFont(arial_20); // Regular
        g2.setColor(Color.white);
        int lineStart = y + gp.tileSize * 2;
        int lineHeight = 35;

        g2.drawString("[ W, A, S, D ]  :  Move Chef", gp.tileSize * 2, lineStart);
        g2.drawString("[ E ]  :  Pick Up / Drop Item", gp.tileSize * 2, lineStart + lineHeight);
        g2.drawString("[ C ]  :  Interact (Chop / Cook / Wash)", gp.tileSize * 2, lineStart + lineHeight * 2);

        // 4. Instructions Section
        g2.setFont(arial_20B);
        g2.setColor(new Color(255, 200, 0));
        g2.drawString("RULES", gp.tileSize * 10, y + gp.tileSize);

        g2.setFont(arial_20);
        g2.setColor(Color.white);

        int rightColX = gp.tileSize * 10;

        g2.drawString("1. Check Orders on the right panel.", rightColX, lineStart);
        g2.drawString("2. Prepare Ingredients (Chop/Cook).", rightColX, lineStart + lineHeight);
        g2.drawString("3. Assemble Dish on a Plate.", rightColX, lineStart + lineHeight * 2);
        g2.drawString("4. Serve at Counter to get Score.", rightColX, lineStart + lineHeight * 3);
        g2.drawString("5. Wash Dirty Plates at Sink.", rightColX, lineStart + lineHeight * 4);

        // Warning Text
        g2.setColor(new Color(255, 100, 100)); // Merah muda
        g2.drawString("! Watch out for the Timer & Burned Food !", gp.tileSize * 5, (int) (lineStart + lineHeight * 5.5));

        // 5. Back Button
        g2.setFont(arial_20);
        g2.setColor(Color.white);
        text = "BACK";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.screenHeight - gp.tileSize;

        g2.drawString(text, x, y);

        if (commandNum == 1) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }
    }

    private void drawOrders(Graphics2D g2) {
        List<Order> activeOrders = gp.orderList.getActiveOrders();

        int x = gp.mapWidth + 15;
        int y = 120;
        int width = gp.uiPanelWidth - 30;
        int height = 60;
        int padding = 10;

        g2.setFont(arial_10);
        g2.setColor(Color.white);
        g2.drawString("ACTIVE ORDERS:", x, y - 10);

        for (int i = 0; i < activeOrders.size(); i++) {
            Order o = activeOrders.get(i);

            // Background Tiket
            g2.setColor(new Color(240, 240, 220));
            g2.fillRect(x, y, width, height);

            // Border
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(x, y, width, height);

            // Nama Resep
            g2.setFont(orderFontTitle);
            g2.setColor(Color.black);
            String title = o.getRecipe().getName();
            g2.drawString(title, x + 10, y + 25);

            // Ingredients
            g2.setFont(orderFontBody);
            String ingredients = getIngredientsString(o.getRecipe());
            g2.drawString(ingredients, x + 10, y + 40);

            // Timer Bar
            float progress = o.getCurrentTime() / o.getMaxTime();
            int barWidth = width - 20;
            int currentBarWidth = (int) (barWidth * progress);

            if (progress > 0.5) g2.setColor(new Color(0, 180, 0)); // Dark Green
            else if (progress > 0.25) g2.setColor(Color.ORANGE);
            else g2.setColor(Color.RED);

            g2.fillRect(x + 10, y + height - 15, currentBarWidth, 8);

            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x + 10, y + height - 15, barWidth, 8);

            y += height + padding;
        }
    }

    private String getIngredientsString(Recipe recipe) {
        StringBuilder sb = new StringBuilder("(");
        Map<Ingredient, State> reqs = recipe.getIngredientRequirements();

        int count = 0;
        for (Ingredient ing : reqs.keySet()) {
            sb.append(ing.getName());
            if (count < reqs.size() - 1) {
                sb.append(", ");
            }
            count++;
        }
        sb.append(")");
        return sb.toString();
    }

    private void drawStageScreen(Graphics2D g2) {
        g2.drawImage(titlebg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(arial_60);
        g2.setColor(Color.white);
        String text = "Stage Preview";
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        int y = gp.screenHeight/2 - 3*gp.tileSize;
        g2.drawString(text, x, y);

        g2.drawImage(mappreview, x - 10, y + gp.tileSize, gp.tileSize*6 , gp.tileSize*4,  null);

        if (GamePanel.playerScore >= gp.MIN_SCORE_TO_PASS) {
            g2.setFont(arial_40B);
            g2.setColor(Color.green);
            text = "Success";
            length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - length/2;
            y += 36;
            g2.drawString(text, x, y);
        }

        g2.setFont(arial_20);
        g2.setColor(Color.white);
        text = "Target Score: " + gp.MIN_SCORE_TO_PASS;
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = (int) (gp.screenHeight/2 + 2.5*gp.tileSize);
        g2.drawString(text, x, y);

        g2.setFont(arial_20);
        text = "START";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }

        g2.setFont(arial_20);
        text = "BACK";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize/2;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }
    }

    private void drawTitleScreen( Graphics2D g2) {
        g2.drawImage(titlebg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(arial_80);
        g2.setColor(Color.white);
        String text = "NimonsCooked";
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        int y = gp.screenHeight/2 - 2*gp.tileSize;
        g2.drawString(text, x, y);

        g2.setFont(arial_20);
        text = "NEW GAME";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.screenHeight/2 + 2*gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }

        g2.setFont(arial_20);
        text = "HOW TO PLAY";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }

        g2.setFont(arial_20);
        text = "QUIT";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 2) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0,0,0,150));
        g2.fillRect(0,0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;

        g2.setFont(arial_80);

        if (gp.isWin) {
            g2.setColor(Color.green);
            text = "PASS";
        } else {
            g2.setColor(Color.red);
            text = "FAIL";
        }

        // Center text
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.screenHeight/2;
        g2.drawString(text, x, y);

        // Keterangan
        g2.setFont(arial_20);
        g2.setColor(Color.white);
        text = gp.gameOverMessage;
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.screenHeight/2 + 50;
        g2.drawString(text, x, y);

        // Final Score
        text = "Final Score: " + GamePanel.getScore();
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y = gp.screenHeight/2 + 90;
        g2.drawString(text, x, y);

        g2.setFont(arial_20);
        text = "RETRY";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }

        g2.setFont(arial_20);
        text = "BACK";
        length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - gp.tileSize/2, y);
        }
    }
}
