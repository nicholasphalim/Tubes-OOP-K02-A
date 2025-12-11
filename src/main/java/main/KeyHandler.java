package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import entity.Direction;

public class KeyHandler implements KeyListener {

    public boolean upPressed = false;
    public boolean downPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean eKeyPressed = false;
    public boolean cKeyPressed = false;

    GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private void changeCommandNum(int delta, int maxIndex) {
        gp.ui.commandNum += delta;
        if (gp.ui.commandNum < 0) gp.ui.commandNum = 0;
        if (gp.ui.commandNum > maxIndex) gp.ui.commandNum = maxIndex;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_W) {
                changeCommandNum(-1, 2);
            }
            if (code == KeyEvent.VK_S) {
                changeCommandNum(1, 2);
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.gameState = gp.stageState;
                }
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.tutorialState;
                    gp.ui.commandNum = 0;
                }
                if (gp.ui.commandNum == 2) {
                    System.exit(0);
                }
            }
        } else if (gp.gameState == gp.stageState) {
            if (code == KeyEvent.VK_W) {
                changeCommandNum(-1, 2);
            }
            if (code == KeyEvent.VK_S) {
                changeCommandNum(1, 2);
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.retryGame();
                    gp.gameState = gp.playState;
                }
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.titleState;
                    gp.ui.commandNum = 0;
                }

            }
        } else if (gp.gameState == gp.playState) {
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
                if (!eKeyPressed) {
                    eKeyPressed = true;
                }
            }

            if (code == KeyEvent.VK_C) {
                cKeyPressed = true;
            }
        } else if (gp.gameState == gp.finishState) {
            if (code == KeyEvent.VK_W) {
                changeCommandNum(-1, 1);
            }
            if (code == KeyEvent.VK_S) {
                changeCommandNum(1, 1);
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.retryGame();
                    gp.gameState = gp.playState;
                }
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.titleState;
                    gp.ui.commandNum = 0;
                }

            }
        } else if (gp.gameState == gp.tutorialState) {
            if (code == KeyEvent.VK_W) {
                changeCommandNum(-1, 1);
            }
            if (code == KeyEvent.VK_S) {
                changeCommandNum(1, 1);
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.titleState;
                    gp.ui.commandNum = 0;
                }

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
    }

    public Direction getDirection() {
        if (upPressed) return Direction.UP;
        if (downPressed) return Direction.DOWN;
        if (leftPressed) return Direction.LEFT;
        if (rightPressed) return Direction.RIGHT;
        return null;
    }
}
