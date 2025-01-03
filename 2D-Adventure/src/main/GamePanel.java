package main;

import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile size
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile size
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // set FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; //helps set up the desired FPS
    Player player;


    public GamePanel() { //public GamePanel displays code under it

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //this DoubleBuffered being set to true improves rendering performance
        this.addKeyListener(keyH);
        this.setFocusable(true); //with this, the GamePanel can be "focused" to receive key input.

        player = new Player(this,keyH);
    }

    public void startGameThread(){
        gameThread = new Thread(this); // "this" passes the GamePanel Class to the thread's constructor
        gameThread.start(); //going to automatically call the run method below
    }

    @Override //==> explanation
    public void run() { //the GameLoop will be the core of the game
        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval; // displays went to "draw" the screen again.
        double delta = 0;
        long currentTime;
        long lastTime = System.nanoTime();
        long timer = 0;
        int drawCount = 0;

        //as long as gameThread exists, it repeats the process that is written inside the brackets
        while(gameThread !=null){

            // Delta/Accumulator method
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                // 1. UPDATE: update information such as character positions
                update();
                //2. DRAW: draw the screen with the updated information
                repaint();
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }


             //Sleep method determines how much time remains until next draw time.
//            try {
//                double remainingTime = nextDrawTime - System.nanoTime();
//                 //convert nano to milli, because sleep counts in milli.
//                 remainingTime = remainingTime/1000000;
//                 //these 2 lines below are to make sure that the Thread will sleep.
//                 if(remainingTime < 0){
//                     remainingTime = 0;
//                 }
//                 Thread.sleep((long) remainingTime);
//                 nextDrawTime += drawInterval;
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }

        }
    }
    public void update(){

        player.update();
    }
    public void paintComponent(Graphics g){ //Graphics is a class with functions to draw object on the screen.


        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        tileM.draw(g2);
        player.draw(g2);

        g2.dispose();
    }
}
