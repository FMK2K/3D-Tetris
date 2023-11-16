package FinalProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

//NOTES
//we will have a createRandomBlock method that creates a random tetromino at a position y=top of window,
//this tetromino will have a Y velocity going downwards, it will be controlled by the arrow keys for left and right movements
//when it starts falling create will create another one and display it at the side to show player what's coming next
//when our first one "lands" its stops and the second one starts falling and a third is created and displayed
//Pressing up will rotate clockwise and pressing down will rotate counter-clockwise
//once a row is filled up all the cubes are destroyed and everything on top falls down one level or many levels depending on rows cleared
class Pair{
    public double x;
    public double y;

    public Pair(double initX, double initY){
        x = initX;
        y = initY;
    }

    public Pair add(Pair toAdd){
        return new Pair(x + toAdd.x, y + toAdd.y);
    }

    public Pair divide(double denom){
        return new Pair(x / denom, y / denom);
    }

    public Pair times(double val){
        return new Pair(x * val, y * val);
    }

    public void flipX(){
        x = -x;
    }

    public void flipY(){
        y = -y;
    }
}

//USE OF INTERFACES
//we need the interface since the landing method will be different for different shapes
interface methods{
    //We will also add a rotate method here, or we can have a rotate method outside that takes any of the shapes using generics
    public void landed(World w, double time);
    public void update(World w, double time);
}

class Tetromino {

    Pair position;
    Pair velocity;
    Pair acceleration;
    double size;
    double dampening;
    Color color;
    public void setPosition(Pair p){
        position = p;
    }
    public void setVelocity(Pair v){
        velocity = v;
    }
    public void setAcceleration(Pair a){
        acceleration = a;
    }
    public Pair getPosition(){
        return position;
    }
    public Pair getVelocity(){
        return velocity;
    }
    public Pair getAcceleration(){
        return acceleration;
    }
    public double flipX() {
        acceleration.flipX();
        return 0.0;
    }
    public double flipY() {
        acceleration.flipY();
        return 0.0;
    }

public void draw(Graphics g){

}
public void update(World w, double time){

}

}

//cube should not implement methods, we will be using cube to create the other shapes which will implement methods
class Cube extends Tetromino {

    public Cube() {
        Random rand = new Random();
        position = new Pair(500.0, -25);
        velocity = new Pair(0,200);
        acceleration = new Pair(0, 0);
        size = 25;
        dampening = 1;
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    //we only need draw method for cube since every other shape is a combination of cubes
    public void draw(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;

        int centerX = (int) position.x;
        int centerY = (int) position.y;
        int depth = (int) size; // Assuming depth is the same as the radius for simplicity

        // Define the front face corners
        Point topFrontLeft = new Point(centerX - depth, centerY - depth);
        Point topFrontRight = new Point(centerX + depth, centerY - depth);
        Point bottomFrontLeft = new Point(centerX - depth, centerY + depth);
        Point bottomFrontRight = new Point(centerX + depth, centerY + depth);

        // Define the back face corners (offset by depth in the Z dimension)
        Point topBackLeft = new Point(topFrontLeft.x - depth, topFrontLeft.y - depth);
        Point topBackRight = new Point(topFrontRight.x - depth, topFrontRight.y - depth);
        Point bottomBackLeft = new Point(bottomFrontLeft.x - depth, bottomFrontLeft.y - depth);
        Point bottomBackRight = new Point(bottomFrontRight.x - depth, bottomFrontRight.y - depth);

        // Draw the back face
        graphics2D.setColor(new Color(180, 180, 180)); // Darker shade for the back
        graphics2D.fillPolygon(new int[]{topBackLeft.x, topBackRight.x, bottomBackRight.x, bottomBackLeft.x},
                new int[]{topBackLeft.y, topBackRight.y, bottomBackRight.y, bottomBackLeft.y}, 4);

        // Draw the bottom face (shaded differently)
        graphics2D.setColor(new Color(150, 150, 150));
        graphics2D.fillPolygon(new int[]{bottomBackLeft.x, bottomBackRight.x, bottomFrontRight.x, bottomFrontLeft.x},
                new int[]{bottomBackLeft.y, bottomBackRight.y, bottomFrontRight.y, bottomFrontLeft.y}, 4);

        // Draw the side face
        graphics2D.setColor(new Color(200, 200, 200)); // Lighter shade for the side
        graphics2D.fillPolygon(new int[]{topBackRight.x, topFrontRight.x, bottomFrontRight.x, bottomBackRight.x},
                new int[]{topBackRight.y, topFrontRight.y, bottomFrontRight.y, bottomBackRight.y}, 4);

        // Draw the front face
        graphics2D.setColor(color); // Front face with the original color
        graphics2D.fillPolygon(new int[]{topFrontLeft.x, topFrontRight.x, bottomFrontRight.x, bottomFrontLeft.x},
                new int[]{topFrontLeft.y, topFrontRight.y, bottomFrontRight.y, bottomFrontLeft.y}, 4);

        // Draw edges for clarity
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(topFrontLeft.x, topFrontLeft.y, topFrontRight.x, topFrontRight.y); // Top edge
        graphics2D.drawLine(topFrontLeft.x, topFrontLeft.y, bottomFrontLeft.x, bottomFrontLeft.y); // Left edge

        graphics2D.drawLine(bottomFrontLeft.x, bottomFrontLeft.y, bottomFrontRight.x, bottomFrontRight.y); // Bottom edge
        graphics2D.drawLine(topFrontRight.x, topFrontRight.y, bottomFrontRight.x, bottomFrontRight.y); // Right edge

        // Draw the lines connecting the front and back faces
        graphics2D.drawLine(topFrontLeft.x, topFrontLeft.y, topBackLeft.x, topBackLeft.y);
        graphics2D.drawLine(topFrontRight.x, topFrontRight.y, topBackRight.x, topBackRight.y);
        graphics2D.drawLine(bottomFrontLeft.x, bottomFrontLeft.y, bottomBackLeft.x, bottomBackLeft.y);

        //graphics2D.drawLine(bottomFrontRight.x, bottomFrontRight.y, bottomBackRight.x, bottomBackRight.y);
    }
    //REMEMBER TO REMOVE THIS METHOD
    public void landed(World w, double time){

        if (w.currentPiece.position.y + 2* size > w.height || touchesOtherPieces(w,w.currentPiece)){
            velocity.y=0;
            Tetromino tetromino= w.currentPiece;
            w.currentPiece=new Cube();

            //when our current piece lands we add it to our collection
            //WE CAN HAVE AN ADJUST METHOD THAT ADJUSTS THE POSITION IN CASE IT LOOKS LIKE A CUBE LANDED INSIDE ANOTHER CUBE
            w.tetrominos.add(tetromino);
        }

    }

    private boolean touchesOtherPieces(World w, Tetromino tetromino) {

            for(Tetromino t: w.tetrominos){
                if(t.position.x==tetromino.position.x && tetromino.position.y>t.position.y-2*size){
                    return true;
                }
            }

        return false;
    }

    //REMEMBER TO REMOVE THIS METHOD
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }


}

//WE HAVE SEVEN CLASSES FOR SEVEN PIECES
class LinePiece extends Tetromino implements methods{
    //It consists of four blocks in a straight line.

    @Override
    public void landed(World w, double time) {

    }

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class SquarePiece extends Tetromino implements methods{
    //It is shaped like a square and consists of four blocks in a 2x2 formation.
    // It's the only regular tetromino that doesn't have to rotate due to its symmetrical shape.

    @Override
    public void landed(World w, double time) {

    }

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class TPiece extends Tetromino implements methods{
    //This piece is shaped like the letter T
    // composed of a row of three blocks with one added above the center.

    @Override
    public void landed(World w, double time) {

    }

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class JPiece extends Tetromino implements methods{
// is shaped like a mirror-reversed 'L' and consists of three blocks in a row with one added above the left side.
public void landed(World w, double time) {

}

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class LPiece extends Tetromino implements methods{
    //This piece is shaped like the letter 'L',
    // composed of three blocks in a row with one added above the right side.

    public void landed(World w, double time) {

    }

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class SPiece extends Tetromino implements methods{
//It consists of two stacked horizontal dimers (two blocks together) offset by one block.
// When viewed from above, it forms the letter 'S'.
public void landed(World w, double time) {

}

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}
class ZPiece extends Tetromino implements methods{
//This is the mirror image of the 'S' piece,
// consisting of two stacked horizontal dimers offset by one block,
// which forms a 'Z' when viewed from above.
    @Override
    public void landed(World w, double time) {

    }

    @Override
    public void update(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        landed(w, time);
    }
}

class World{
    int height;
    int width;
    Tetromino currentPiece;

    ArrayList<Tetromino> tetrominos;

    public World(int initWidth, int initHeight){
        width = initWidth;
        height = initHeight;
        tetrominos=new ArrayList<>();
        currentPiece=new Cube();


//            Cube cube=new Cube();
//            cube.setPosition(new Pair(500, 700));
//            cube.setVelocity(new Pair(0,0));
//            tetrominos.add(cube);


    }

    public void drawTetrominos(Graphics g){
        currentPiece.draw(g);
        for (Tetromino t:tetrominos){
            t.draw(g);
        }
    }

    //WE DON'T NEED THIS METHOD ?
    public void updateTetrominos(double time){
       currentPiece.update(this,time);
        for (Tetromino t:tetrominos) {
            t.update(this, time);
        }
    }
}

public class ThreeDTetris extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;

    class Runner implements Runnable{
        public void run() {
            while(true){
                world.updateTetrominos(1.0 / (double)FPS);
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
        System.out.println("You pressed down: " + c);
    }

    public void keyReleased(KeyEvent e) {

        char c=e.getKeyChar();
        if(c=='a' || c=='A'){

            if(world.currentPiece.position.x>0 && world.currentPiece.position.x<world.width-60 && !touchingOtherSides2()) {
                //ONLY MOVE BLOCK SIDEWAYS IF THERE IS NO WALL  OR ANY OTHER BLOCK
                world.currentPiece.position.x -= 2*world.currentPiece.size;
            }
        }
        if(c=='d' || c=='D'){
            if(world.currentPiece.position.x>0 && world.currentPiece.position.x<world.width-60 && !touchingOtherSides1()) {
                world.currentPiece.position.x += 2*world.currentPiece.size;
            }
        }
    }

    private boolean touchingOtherSides1() {
        for(Tetromino t: world.tetrominos) {
            double distance =world.currentPiece.position.x-t.position.x;


            if (world.currentPiece.position.y+2*t.size > t.position.y ) {
                //first check if the spheres are on the same horizontal level

                if(distance<0) {
                    if (Math.abs(distance )< 2.5 * t.size) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean touchingOtherSides2() {
        for(Tetromino t: world.tetrominos) {
            double distance =world.currentPiece.position.x-t.position.x;

            if (world.currentPiece.position.y+2*t.size > t.position.y ) {
                //first check if the spheres are on the same horizontal level                //first check if the spheres are on the same horizontal level
                if(distance>0){
                    if(distance<2.5*t.size){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public ThreeDTetris(){
        world = new World(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("3D-TETRIS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ThreeDTetris mainInstance = new ThreeDTetris();
        mainInstance.setLayout(new BorderLayout());

        //frame.setLocationRelativeTo(null);
        frame.setContentPane(mainInstance);
        frame.pack();

        frame.setSize(WIDTH,HEIGHT);

        //frame.getContentPane().add(label);

        frame.setVisible(true);

    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        ImageIcon background = new ImageIcon("/Users/davidnkairu/IdeaProjects/COSC-112./resource/images/pngtree-galaxy-wallpaper-backgrounds-free-picture-image_3408160.jpg");
        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

        // Now draw the spheres on top of the background image
        world.drawTetrominos(g);
    }


}
