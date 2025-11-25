package main;

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

        g2.drawString("x: " + gp.player.x, 600, 70);

        g2.drawString("y: " + gp.player.y, 600, 90);

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
