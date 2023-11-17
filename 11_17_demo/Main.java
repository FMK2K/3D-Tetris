import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class Main extends JPanel implements KeyListener{
    public static final int WIDTH = 400;
    public static final int HEIGHT = 700;
    public static final int FPS = 60;
    World world;


    class Runner implements Runnable{
        public void run() {
            while(true){
                world.updateWorld(1.0 / (double)FPS);
                repaint();
                try{
                    Thread.sleep(1000/FPS);
                }
                catch(InterruptedException e){}
            }
        }
    }


    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if(c == 'a' && !world.currentShape.leftCollision){
            for(int i = 0; i < world.currentShape.s.length; i++){
                world.currentShape.s[i].position.x -= Square.SIZE;
            }
        }
        if(c == 'd' && !world.currentShape.rightCollision){
            for(int i = 0; i < world.currentShape.s.length; i++){
                world.currentShape.s[i].position.x += Square.SIZE;
            }
        }
        if(c == 's' && !world.currentShape.land){
            for(int i = 0; i < world.currentShape.s.length; i++){
                world.currentShape.s[i].position.y += Square.SIZE;
            }
        }
        if(c == 'r'){
            switch(world.currentShape.dir){
                case 1:
                    world.currentShape.rotate2();
                    break;
                case 2:
                    world.currentShape.rotate3();
                    break;
                case 3:
                    world.currentShape.rotate4();
                    break;
                case 4:
                    world.currentShape.rotate1();
                    break;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();

    }


    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public Main(){
        world = new World(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main mainInstance = new Main();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        world.drawWorld(g);
    }
}