package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import entity.Direction;

public class KeyHandler implements KeyListener {

    public  boolean upPressed = false;
    public  boolean downPressed = false;
    public  boolean leftPressed = false;
    public  boolean rightPressed = false;
    public boolean eKeyPressed = false;
    public boolean cKeyPressed = false;
    public boolean swapPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            // Only set interactPressed to true if the key was not already held down
            if (!eKeyPressed) {
                eKeyPressed = true; // Mark the key as currently held
            }
        }

        if (code == KeyEvent.VK_C) {
            cKeyPressed = true;
        }

        if (code == KeyEvent.VK_SPACE) {
            if (!swapPressed) {
                swapPressed = true; 
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            eKeyPressed = false;
        }
        if (code == KeyEvent.VK_C) {
            cKeyPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            swapPressed = false;
        }
    }

    public Direction getDirection() {
        if (upPressed) return Direction.UP;
        if (downPressed) return Direction.DOWN;
        if (leftPressed) return Direction.LEFT;
        if (rightPressed) return Direction.RIGHT;
        return null;
    }
}
