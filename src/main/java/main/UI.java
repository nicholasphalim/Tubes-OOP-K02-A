package main;

import entity.Action;
import station.CuttingStation;

import java.awt.*;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    Font arial_40;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    double playTime = 0.0;
    DecimalFormat df = new DecimalFormat("#0.00");

    public UI(GamePanel gp){
        this.gp = gp;
        arial_40 = new Font("arial", Font.PLAIN, 20);
    }

    public void showMessage(String message){
        this.message = message;
        this.messageOn = true;
    }

    public void draw(Graphics2D g2){
        g2.setFont(arial_40);
        g2.setColor(Color.black);
//        g2.drawString("Hello", 25, 30);

        playTime += (double) 1 /gp.FPS;
        g2.setFont(g2.getFont().deriveFont(15f));
        g2.drawString("Play Time: ", 590, 30);
        g2.drawString(df.format(playTime), 590, 50);

        g2.drawString("x: " + gp.chef.getPosition().x, 600, 70);

        g2.drawString("y: " + gp.chef.getPosition().y, 600, 90);

        //Progress Bar
        if (gp.chef.busyState == Action.CUTTING && gp.chef.currentInteractionStation instanceof CuttingStation) {
            CuttingStation activeStation = (CuttingStation) gp.chef.currentInteractionStation;
            if (activeStation.cutting && activeStation.getCurrentCuttingProgress() < 100) {
                int barWidth = gp.tileSize;
                int barHeight = 10;
                int barX = activeStation.x;
                int barY = activeStation.y;

                g2.setColor(Color.GRAY);
                g2.fillRect(barX, barY, barWidth, barHeight);

                int progressWidth = (int) (barWidth * (activeStation.getCurrentCuttingProgress() / 100.0));
                g2.setColor(Color.GREEN);
                g2.fillRect(barX, barY, progressWidth, barHeight);

                g2.setFont(arial_40.deriveFont(Font.PLAIN, 15f));
                g2.setColor(Color.WHITE);
                String progressText = activeStation.getCurrentCuttingProgress() + "%";
                FontMetrics fm = g2.getFontMetrics();
                int textX = barX + (barWidth - fm.stringWidth(progressText)) / 2;
                int textY = barY + ((barHeight - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(progressText, textX, textY);
            }
        }

        if(this.messageOn){
            g2.setFont(g2.getFont().deriveFont(20f));
            g2.setColor(Color.black);
            g2.drawString(message, 25, 50);

            messageCounter++;

            if(messageCounter > 120){
                messageCounter = 0;
                messageOn = false;
            }
        }
    }
}
