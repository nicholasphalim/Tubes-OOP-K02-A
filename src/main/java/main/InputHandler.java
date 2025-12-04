package main;

import entity.Direction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed, pickUpPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_F) interactPressed = true; // ini buat tombol F (interact)
        if (code == KeyEvent.VK_E) pickUpPressed = true;   // ini buat tombol E (pick up)
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_F) interactPressed = false;
        if (code == KeyEvent.VK_E) pickUpPressed = false;
    }

    // buat ngubah boolean (yang ada di KeyHandler, Player) jadi enum Direction 
    public Direction getCurrentDirection() {
        if (upPressed) return Direction.UP;
        if (downPressed) return Direction.DOWN;
        if (leftPressed) return Direction.LEFT;
        if (rightPressed) return Direction.RIGHT;
        return null;
    }
}