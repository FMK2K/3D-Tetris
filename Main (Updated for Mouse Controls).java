import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class Main extends JPanel implements KeyListener {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 700;
    public static final int FPS = 60;
    World world;


    class Runner implements Runnable {
        public void run() {
            while (true) {
                world.updateWorld(1.0 / (double) FPS);
                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }


    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 'a' && !world.currentShape.leftCollision) {
            for (int i = 0; i < world.currentShape.s.length; i++) {
                world.currentShape.s[i].position.x -= Square.SIZE;
            }
        }
        if (c == 'd' && !world.currentShape.rightCollision) {
            for (int i = 0; i < world.currentShape.s.length; i++) {
                world.currentShape.s[i].position.x += Square.SIZE;
            }
        }
        if (c == 's' && !world.currentShape.land) {
            for (int i = 0; i < world.currentShape.s.length; i++) {
                world.currentShape.s[i].position.y += Square.SIZE;
            }
        }
        if (c == 'r') {
            switch (world.currentShape.dir) {
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

    public Main() {
        world = new World(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
        new Myframe(world);
    }

    public static void main(String[] args) {
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
    class Myframe extends JFrame implements MouseListener{
        World world;
        //D-PAD
        JLabel[] DPad;
//        JLabel DPad[0];
//        JLabel DPad[1];

        JLabel ButtonR;
        //ImageIcon controller;


        Myframe(World w) {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(500, 250);
            this.setLayout(null);
            this.world = w;

            DPad = new JLabel[3];
            //Move Left
            DPad[0] = new JLabel();
            DPad[0].setBounds(25, 50, 75, 50);
            DPad[0].setBackground(Color.DARK_GRAY);
            DPad[0].setOpaque(true);
            DPad[0].addMouseListener(this);
            //Move Down
            DPad[1] = new JLabel();
            DPad[1].setBounds(100, 100, 50, 75);
            DPad[1].setBackground(Color.DARK_GRAY);
            DPad[1].setOpaque(true);
            DPad[1].addMouseListener(this);
            //Move Right
            DPad[2] = new JLabel();
            DPad[2].setBounds(150, 50, 75, 50);
            DPad[2].setBackground(Color.DARK_GRAY);
            DPad[2].setOpaque(true);
            DPad[2].addMouseListener(this);

            //Rotate
            ButtonR = new JLabel();
            ButtonR.setBounds(375, 100,100, 100);
            ButtonR.setBackground(Color.RED);
            ButtonR.setOpaque(true);
            ButtonR.addMouseListener(this);



//        label.addMouseListener(this);
            this.addMouseListener(this);
            this.add(DPad[0]);
            this.add(DPad[1]);
            this.add(DPad[2]);
            this.add(ButtonR);


            this.setVisible(true);

        }
        @Override
        public void mouseClicked(MouseEvent e){
            //System.out.println("Mouse Has been clicked");

            // Finds the location of the mouse
            PointerInfo a = MouseInfo.getPointerInfo();
            Point cursor = a.getLocation();

            // Gets the x -> and y co-ordinates of cursor
            int x = (int) cursor.getX();
            int y = (int) cursor.getY();
            System.out.println("Mouse x: " + x);
            System.out.println("Mouse y: " + y);

        /*  // Determines which tile the click occured on
        int xTile = x/100;
        int yTile = y/100;

        System.out.println("X Tile: " + xTile);
        System.out.println("Y Tile: " + yTile); */
            if( y>= 50 && y<= 125){
                //Move Left
                if(x>= 25 && x<= 100){
                    System.out.println("Mouse has clicked Move Left");
                    /*DPad[1].setBackground(Color.BLUE);*/
                    for(int i = 0; i < world.currentShape.s.length; i++){
                        world.currentShape.s[i].position.x -= Square.SIZE;
                    }
                }//Move Right
                if(x>= 150 && x<= 225){
                    System.out.println("Mouse has clicked Move Right");
                    /*DPad[1].setBackground(Color.RED);*/
                    for(int i = 0; i < world.currentShape.s.length; i++)
                        world.currentShape.s[i].position.x += Square.SIZE;
                }
            }//Move Down
            if(y>= 100 && y<= 200){
                if(x>= 100 && x<= 175) {
                    System.out.println("Mouse has clicked Move Down");
                    for(int i = 0; i < world.currentShape.s.length; i++)
                        world.currentShape.s[i].position.x += Square.SIZE;
                }
            }
            //Rotate
            if(y>= 130 && y<= 230) {
                if (x >= 380 && x <= 480) {
                    System.out.println("Mouse has clicked Rotate");
                switch(world.currentShape.dir){
                    case 1:
                       world.currentShape.rotate2(); break;
                   case 2:
                       world.currentShape.rotate3(); break;
                    case 3:
                        world.currentShape.rotate4(); break;
                    case 4:
                        world.currentShape.rotate1(); break;
                }
                }
            }
        }

        //Just Pressed & Released
        @Override
        public void mousePressed(MouseEvent e){

//        Color random = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
//        DPad[0].setBackground(random );
        }
        @Override
        public void mouseReleased(MouseEvent e){


//        Color random = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
//        DPad[0].setBackground(random );

//        break;
        }
        //Just Entered & Exit
        @Override
        public void mouseEntered(MouseEvent e){
        }
        @Override
        public void mouseExited(MouseEvent e){

        }
    }
}