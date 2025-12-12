package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import ingredient.Ingredient;
import ingredient.State;
import order.Order;
import recipe.Recipe;

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

        try {
            java.io.InputStream is = getClass().getResourceAsStream("/fonts/ari-w9500.ttf");
            
            if (is == null) {
                // Jika font tidak ketemu, lempar error agar masuk ke catch
                throw new IOException("Font file not found!");
            }

            Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, is);

            arial_10 = pixelFont.deriveFont(Font.BOLD, 14f);
            arial_20 = pixelFont.deriveFont(Font.PLAIN, 20f);
            arial_20B = pixelFont.deriveFont(Font.BOLD, 20f);
            arial_40 = pixelFont.deriveFont(Font.PLAIN, 40f);
            arial_40B = pixelFont.deriveFont(Font.BOLD, 40f); // Typo di kodemu '20' saya ubah jadi 40 sesuai nama var
            arial_60 = pixelFont.deriveFont(Font.PLAIN, 60f);
            arial_80 = pixelFont.deriveFont(Font.BOLD, 80f);

            // Font khusus Order
            orderFontTitle = pixelFont.deriveFont(Font.BOLD, 16f);
            orderFontBody = pixelFont.deriveFont(Font.BOLD, 10f);
            
        } catch (FontFormatException | IOException e) {
            System.err.println("Gagal memuat font kustom, menggunakan Arial bawaan.");
            e.printStackTrace();
            // Fallback: Kalau gagal load, pakai Arial biasa
            arial_40 = new Font("Arial", Font.PLAIN, 40);
            arial_80 = new Font("Arial", Font.BOLD, 80);
        }

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
        g2.drawString("[ Space ]  :  Switch Chef", gp.tileSize * 2, lineStart + lineHeight * 3);
        g2.drawString("[ Shift ]  :  Dash", gp.tileSize * 2, lineStart + lineHeight * 4);

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
        int paddingBetweenOrders = 10;

        g2.setFont(arial_10);
        g2.setColor(Color.white);
        g2.drawString("ACTIVE ORDERS:", x, y - 10);

        for (int i = 0; i < activeOrders.size(); i++) {
            Order o = activeOrders.get(i);

            // ---------------------------------------------------------
            // 1. HITUNG TINGGI KOTAK SECARA DINAMIS
            // ---------------------------------------------------------
            g2.setFont(orderFontBody); // Set font dulu untuk ukur teks
            String ingredients = getIngredientsString(o.getRecipe());
            
            // Panggil helper untuk memecah teks menjadi list baris
            List<String> wrappedText = getWrappedText(g2, ingredients, width - 20);
            
            // Atur Spasi Antar Baris (Line Spacing)
            int lineHeight = 14; // Jarak antar baris (pixel) -> Bisa diatur biar rapi
            int textBlockHeight = wrappedText.size() * lineHeight;

            // Tinggi Kotak = Header Title + Tinggi Teks Ingredients + Bar Timer + Padding
            // 25px (Title) + textBlock + 20px (Timer area)
            int boxHeight = 25 + textBlockHeight + 20; 

            // ---------------------------------------------------------
            // 2. GAMBAR KOTAK & KONTEN
            // ---------------------------------------------------------
            
            // Background
            g2.setColor(new Color(240, 240, 220));
            g2.fillRect(x, y, width, boxHeight);

            // Border
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(x, y, width, boxHeight);

            // Judul Resep
            g2.setFont(orderFontTitle);
            g2.setColor(Color.black);
            g2.drawString(o.getRecipe().getName(), x + 10, y + 22);

            // Ingredients (Looping per baris yang sudah dihitung tadi)
            g2.setFont(orderFontBody);
            int textY = y + 38; // Posisi awal teks ingredients
            
            for (String line : wrappedText) {
                g2.drawString(line, x + 10, textY);
                textY += lineHeight; // Turun ke baris berikutnya
            }

            // ---------------------------------------------------------
            // 3. GAMBAR TIMER BAR (POSISI MENGIKUTI TINGGI KOTAK)
            // ---------------------------------------------------------
            float progress = o.getCurrentTime() / o.getMaxTime();
            int barWidth = width - 20;
            int currentBarWidth = (int) (barWidth * progress);

            // Warna Bar
            if (progress > 0.5) g2.setColor(new Color(0, 180, 0)); 
            else if (progress > 0.25) g2.setColor(Color.ORANGE);
            else g2.setColor(Color.RED);

            // Posisi Bar selalu di bagian bawah kotak (boxHeight - 12)
            int barY = y + boxHeight - 12;
            g2.fillRect(x + 10, barY, currentBarWidth, 6);

            // Border Bar
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x + 10, barY, barWidth, 6);

            // Update Y untuk order berikutnya
            y += boxHeight + paddingBetweenOrders;
        }
    }

    // Method Helper: Memecah teks panjang menjadi beberapa baris (List)
    private java.util.List<String> getWrappedText(Graphics2D g2, String text, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        java.awt.FontMetrics fm = g2.getFontMetrics();

        // Jika teks pendek, langsung kembalikan 1 baris
        if (fm.stringWidth(text) < maxWidth) {
            lines.add(text);
            return lines;
        }

        // Pecah kata per kata
        String[] words = text.split(" ");
        String currentLine = words[0];

        for (int i = 1; i < words.length; i++) {
            // Cek apakah kata berikutnya muat di baris ini
            if (fm.stringWidth(currentLine + " " + words[i]) < maxWidth) {
                currentLine += " " + words[i];
            } else {
                // Jika tidak muat, simpan baris ini dan mulai baris baru
                lines.add(currentLine);
                currentLine = words[i];
            }
        }
        // Jangan lupa simpan baris terakhir
        lines.add(currentLine);

        return lines;
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
