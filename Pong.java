package davidProject22;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Scanner;

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
//create a class drawable object to be able to draw everything at once

class DrawableObject{
    Pair position;
    Pair velocity;
    Pair acceleration;
    Color color;

    public void draw(Graphics g) {

    }
}
class Paddle extends DrawableObject{
    double paddleDamper;
    int width;
    int height;

    Pair OldVelocity;

    public Paddle(Pair position){
        height=120;
        width=35;
        this.position=position;
        velocity=new Pair(0, 0);
        acceleration=new Pair(0,0);
        paddleDamper=1;
        color=Color.orange;
        OldVelocity=velocity;

    }
    public void updateLeftPaddle(World w, double time){
        position = position.add(velocity.times(time));
        velocity = velocity.add(acceleration.times(time));
        //we don't want paddle bouncing off walls
        //bounce(w);
        checkBound(w);
    }
    public void updateRightPaddle(World w, double time){
        position = position.add(velocity.times(time));

        velocity = velocity.add(acceleration.times(time));
        //we don't want paddle bouncing off walls
        if(World.vsComp) {

            bounce(w);
        }else {
            checkBound(w);
        }
    }
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
        Color c = g.getColor();

        g.setColor(color);
        g.fillRect((int) position.x, (int) position.y,width,height);
        g.setColor(c);
    }
    private void checkBound(World w){
       //stop the paddles when they hit the wall
        //should we store old velocity so that we can use it when we press keys

        if (position.y < 0){

            velocity=new Pair(0,0);
        }
        else if(position.y +height >  w.height){
            velocity=new Pair(0,0);
        }
    }
    //ADDITIONAL VARIATION 2
    //Add AI
    //Add bounce when playing easy mode against AI
    //so paddle of the AI is just moving up and down
    private void bounce(World w){

        if (position.y < 0){
            velocity.flipY();
        }
        else if(position.y + height >  w.height){
            velocity.flipY();
        }
    }
}

class Sphere extends DrawableObject{
    double radius;
    double dampening;
    private static int leftScore;
    private static int rightScore;
    public Sphere() {
        new Sphere(30);
    }

    public Sphere(int radius) {
        Random rand = new Random();
        this.radius = radius;

        //PONG RULES 1
        //have the ball begin in the center of the screen.
        position = new Pair(512, 384);

        //PONG RULES 2
        // It is then fired either to the right or the left (with no vertical velocity).
        velocity = new Pair(250,0);
        acceleration = new Pair(0.0, 0.0);

        //start dampening at 1 , you can change it depending on game mode or power-ups hit
        dampening = 1;
        color = Color.yellow;

    }

    //GRAPHICS 1
    //Add a flame trail to the ball.
    public void updateTrailSphere(double time,Sphere ball, Paddle rightPaddle){

//        if(ball.position.x<135){
//            position=new Pair(100, ball.position.y);
//        }else if(ball.position.x>rightPaddle.position.x-45){
//            position=new Pair(ball.position.x+ball.radius, ball.position.y);
//        }
//
         position = position.add(velocity.times(time));

        //find distance
        double deltaX=position.x-ball.position.x;
        double deltaY=position.y-ball.position.y;
        double distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        //make sure  they are always trailing the ball

        if(distance> 150 && ball.velocity.x>0){
            position=new Pair(ball.position.x- ball.radius-radius, ball.position.y);
        }
        if(distance> 150 && ball.velocity.x<0){
            position=new Pair(ball.position.x+ ball.radius+radius, ball.position.y);
        }

//        velocity.x = -ball.velocity.x;
//        velocity.y=-ball.velocity.y;

    }


    public static int getLeftScore() {
        return leftScore;
    }

    public static int getRightScore() {
        return rightScore;
    }

    double k;
    public void updateSphere(World w, double time,Paddle leftPaddle, Paddle rightPaddle){
        if(Math.abs(World.ball.velocity.x)>800){
            velocity.x=800;
        }
        if(Math.abs(World.ball.velocity.x)<250){
            velocity.x=250;
        }
        position = position.add(velocity.times(time));

        //you want to increase sphere size based on velocity of the sphere
        if(Math.abs(velocity.x)<200 || Math.abs(velocity.x)>500 ) {
            k=0;
        }else {
            k = 0.4 ;
        }

        if(position.x<512 && velocity.x>0){
            //you don't want to make radius too big
            if(radius<60) {
                radius += k;
            }
        }
        if(position.x<512 && velocity.x<0){
            //you don't want to make radius too small
            if(radius>10) {
                radius -= k;
            }
        }
        if(position.x>512 && velocity.x<0){
            //you don't want to make radius too big
            if(radius<60) {
                radius += k;
            }
        }
        if(position.x>512 && velocity.x>0){
            //you don't want to make radius too small
            if(radius>20) {
                radius -= k;
            }
        }
        velocity = velocity.add(acceleration.times(time));

        //check if it's a score
        Scoring(w);

        //check if it hits left paddle
        contactWithLeftPaddle(leftPaddle);

        //check if it hits right paddle
        contactWithRightPaddle(rightPaddle);

        //check if you hit a powerUp or powerDown
        contactWithPower(w);
    }

    public void setPosition(Pair p){position = p;}
    public void setVelocity(Pair v){velocity = v;}
    public void setAcceleration(Pair a){acceleration = a;}
    public Pair getPosition(){return position;}
    public Pair getVelocity(){return velocity;}
    public Pair getAcceleration(){return acceleration;}
    public double flipX() {
        acceleration.flipX();
        return 0.0;
    }
    public double flipY() {
        acceleration.flipY();
        return 0.0;
    }

    public void draw(Graphics g){
        Color c = g.getColor();

        g.setColor(color);
        g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));
        g.setColor(c);
    }

    //this checks if ball has hit paddles
    private void contactWithLeftPaddle(Paddle leftPaddle){

        double deltaX ;
        double deltaY ;
        double distance=0;
//        double speed=Math.sqrt(velocity.x* velocity.x+ velocity.y* velocity.y);

        //if ball strikes paddle horizontally right horizontal
        if(position.y>=leftPaddle.position.y && position.y<=leftPaddle.position.y+leftPaddle.height){
            deltaX=position.x-(leftPaddle.position.x+leftPaddle.width);
            deltaY=0;
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

            //if ball strikes paddle from the top
        }else if(position.y<leftPaddle.position.y ){
            deltaX= position.x-(leftPaddle.position.x+leftPaddle.width);
            deltaY= position.y-leftPaddle.position.y;
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

            //if ball strikes paddle from bottom
        }else if(position.y>leftPaddle.position.y+leftPaddle.height) {
            deltaX= position.x-(leftPaddle.position.x+leftPaddle.width);
            deltaY= position.y-(leftPaddle.position.y+leftPaddle.height);
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        }
        if(distance<=radius){
            //PADDLE PHYSICS
            //The angle of the ball’s trajectory after hitting a paddle depends on where on the paddle the ball hits.
            // If the ball hits right in the middle of the paddle, it travels at 0 degrees (horizontally, with no vertical velocity).
            // If it hits at the top most edge of the paddle, it travels at a sharp (~75 degrees) angle upwards (and similarly if it hits the bottom most edge of the paddle).
            // If it hits somewhere in between the middle and edge of the paddle, it travels at an angle between 0 and 75 degrees based on where exactly it hits.

            //Note: You’re free to add to this. For example, you could make it so that when the paddle is moving upward, it imparts some upward momentum to the ball.
            // You could also (or instead) make it so that the incoming angle of the ball affects the post-bounce angle.
            double halfHeight=leftPaddle.height/2;

            double length= Math.abs(position.y-(leftPaddle.position.y+halfHeight));
            //calculate how far from the center it hits the paddle
           // System.out.println(length);

            //tangent 75 = 3.732
            double multiplier=(3.732*length)/halfHeight;

            //multiply by dampening to affect speed of the ball as the game progresses


            double velocityY= Math.abs(velocity.x*multiplier);



            //ADDITIONAL VARIATION 1
            //Make the ball move faster and faster as the match goes on.
            //reverse velocity.x
            velocity.x= -velocity.x*dampening*leftPaddle.paddleDamper;

            //now make ball bounce
            //check if ball strikes above middle of paddle
            if(position.y>leftPaddle.position.y+halfHeight){
                //we want to accelerate upwards proportionally to multiplier
                velocity.y=velocityY;

            }

            //check if ball strikes below middle of paddle
            if(position.y<leftPaddle.position.y+halfHeight){
                //we want to accelerate downwards
                velocity.y=-velocityY;
            }

            //check if ball strikes at exactly middle of paddle
            if(position.y==leftPaddle.position.y+halfHeight){
                //we want to not accelerate, and we want it to bounce back in a horizontal line
                velocity.y=0;
            }
//            double newSpeed=Math.sqrt(velocity.x*velocity.x+velocity.y*velocity.y);

//            velocity.x /= newSpeed;
//            velocity.y /= newSpeed;
//            //speed = 1
//
//            velocity.x *= speed;
//            velocity.y *= speed;


        }
    }
    private void contactWithRightPaddle(Paddle rightPaddle){
        double deltaX ;
        double deltaY ;
        double distance=0;
       //double speed=Math.sqrt(velocity.x* velocity.x+ velocity.y* velocity.y);


        if(position.y>=rightPaddle.position.y && position.y<=rightPaddle.position.y+rightPaddle.height){
            deltaX=position.x-rightPaddle.position.x;
            deltaY=0;
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        }else if(position.y<rightPaddle.position.y){
            deltaX= position.x-rightPaddle.position.x;
            deltaY= position.y-rightPaddle.position.y;
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        }else if(position.y>rightPaddle.position.y+rightPaddle.height) {
            deltaX= position.x-rightPaddle.position.x;
            deltaY= position.y-(rightPaddle.position.y+rightPaddle.height);
            distance=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        }
        if(distance<=radius){
            //PADDLE PHYSICS
            //The angle of the ball’s trajectory after hitting a paddle depends on where on the paddle the ball hits.
            // If the ball hits right in the middle of the paddle, it travels at 0 degrees (horizontally, with no vertical velocity).
            // If it hits at the top most edge of the paddle, it travels at a sharp (~75 degrees) angle upwards (and similarly if it hits the bottom most edge of the paddle).
            // If it hits somewhere in between the middle and edge of the paddle, it travels at an angle between 0 and 75 degrees based on where exactly it hits.

            //Note: You’re free to add to this. For example, you could make it so that when the paddle is moving upward, it imparts some upward momentum to the ball.
            // You could also (or instead) make it so that the incoming angle of the ball affects the post-bounce angle.
            double halfHeight=rightPaddle.height/2;

            double length= Math.abs(position.y-(rightPaddle.position.y+halfHeight));
            //calculate how far from the center it hits the paddle
            // System.out.println(length);

            //tangent 75 = 3.732
            double multiplier=(2*length)/halfHeight;

            //multiply by dampening to affect speed of the ball as the game progresses


            double velocityY= Math.abs(velocity.x*multiplier);



            //ADDITIONAL VARIATION 1
            //Make the ball move faster and faster as the match goes on.
            //reverse velocity.x
            velocity.x= -velocity.x*dampening*rightPaddle.paddleDamper;

            //now make ball bounce
            //check if ball strikes above middle of paddle
            if(position.y>rightPaddle.position.y+halfHeight){
                //we want to accelerate upwards proportionally to multiplier
                velocity.y=velocityY;

            }

            //check if ball strikes below middle of paddle
            if(position.y<rightPaddle.position.y+halfHeight){
                //we want to accelerate downwards
                velocity.y=-velocityY;
            }

            //check if ball strikes at exactly middle of paddle
            if(position.y==rightPaddle.position.y+halfHeight){
                //we want to not accelerate, and we want it to bounce back in a horizontal line
                velocity.y=0;
            }

        }

    }
    private void Scoring(World w){
        //save current velocity so that next point starts with same velocity
        //double v=2*World.ball.velocity.x;
       //System.out.println("Current velocity is "+v);

        //every time you score move ball back to center
        if (position.x - radius < w.leftPaddle.position.x){
            //PONG RULE 5
            //After each point, the ball is placed in the center of the screen
            // and fired away from which ever play just scored a point
            World.ball.setPosition(new Pair(512, 384));

            //each point starts with 250
             World.ball.velocity=new Pair(250,0);

             //flip the velocity so that ball starts by moving towards player who lost the point
             World.ball.velocity.flipX();
             World.ball.radius=60;

             //PONG RULE 3
            //If the ball hits the right wall, the left player gains one point.
             rightScore=rightScore+1;

             System.out.println("Player 1 has "+leftScore +" -  and Player 2 has "+rightScore);
        }
         if (position.x + radius > w.width){
             //PONG RULE 5
             //After each point, the ball is placed in the center of the screen
             // and fired away from which ever play just scored a point
             World.ball.setPosition(new Pair(512, 384));
             World.ball.velocity=new Pair(250,0);
             World.ball.radius=60;

             //PONG RULE 4
             //If the ball hits the left wall, the right player gains one point.
             leftScore=leftScore+1;

             System.out.println("Player 1 has "+leftScore +" -  and Player 2 has "+rightScore);        }
        //if it hits the side walls just bounce normally
        if (position.y - radius < 0){
            velocity.flipY();
            position.y = radius;
        }
        else if(position.y + radius >  w.height){
            velocity.flipY();
            position.y = w.height - radius;
        }
    }
    //ADDITIONAL VARIATION 3
    //Add powerups (e.g., if your paddle makes the ball hit a powerup placed at a random location,
    // your paddle speeds up
    private void contactWithPower(World w){
        Random random= new Random();

        //distance from powerUp
        double deltaX= position.x-w.powerUp.position.x;
        double deltaY=position.y-w.powerUp.position.y;
        double distance1=Math.sqrt(deltaY*deltaY+deltaX*deltaX);

        //distance from powerDown
        double deltaX1= position.x-w.powerDown.position.x;
        double deltaY1=position.y-w.powerDown.position.y;
        double distance2=Math.sqrt(deltaY1*deltaY1+deltaX1*deltaX1);

        if(distance1<radius+w.powerUp.radius){
            //if sphere touches powerUp increase speed of paddle or make paddle hit ball faster
            if(velocity.x>0){
                //this means the ball was hit from the left paddle thus powerUp left paddle
                w.leftPaddle.paddleDamper*=1.1;
               // System.out.println("left paddle damper "+w.leftPaddle.paddleDamper);

                //to prevent speed from  increasing forever
                World.rightPaddle.paddleDamper*= 10.0 /11;
                //System.out.println("right paddle damper "+w.rightPaddle.paddleDamper);
            }
            if(velocity.x<0){
                //this means the ball was hit from the right paddle thus powerUp rightPaddle
                World.rightPaddle.paddleDamper*= 1.1;
                //System.out.println("right paddle damper "+w.rightPaddle.paddleDamper);

                //to prevent speed from  increasing forever
                w.leftPaddle.paddleDamper*=10.0 /11;
                //System.out.println("left paddle damper "+w.leftPaddle.paddleDamper);

            }
            //after contact move powerUp to a different position
            w.powerUp.setPosition(new Pair(random.nextInt(100)+400, random.nextInt(700)+30 ));
        }
//        if(distance2<radius+w.powerDown.radius){
//            //if sphere touches powerDown decrease speed of paddle or make paddle hit ball slower
//
//            if(velocity.x>0){
//                //this means the ball was hit from the left paddle thus powerDown left paddle
//                if(w.leftPaddle.paddleDamper>0.5) {
//                    //you don't want it to slow down too much
//                    w.leftPaddle.paddleDamper *= 0.9;
//                    //System.out.println("left paddle damper " + w.leftPaddle.paddleDamper);
//                }
//
//                //to prevent speed from  decreasing forever
//                if(w.rightPaddle.paddleDamper<2.5) {
//                    //you don't want it to speed up too much
//
//                    w.rightPaddle.paddleDamper *= 10.0 / 9;
//                    //System.out.println("right paddle damper " + w.rightPaddle.paddleDamper);
//                }
//            }
//            if(velocity.x<0){
//                //this means the ball was hit from the right paddle thus powerDown rightPaddle
//                if(w.rightPaddle.paddleDamper>0.5) {
//                    w.rightPaddle.paddleDamper *= 0.9;
//                   // System.out.println("right paddle damper " + w.rightPaddle.paddleDamper);
//                }
//                //to prevent speed from decreasing forever
//                if(w.leftPaddle.paddleDamper<2.5) {
//                    w.leftPaddle.paddleDamper *= 10.0 / 9;
//                   // System.out.println("left paddle damper " + w.leftPaddle.paddleDamper);
//                }
//            }
//            //after contact move powerDown to a different position
//            w.powerDown.setPosition(new Pair(random.nextInt(100)+400, random.nextInt(700)+30 ));
//
//        }
    }
}

class World{
    int height;
    int width;
    //ImageIcon j;

    static Sphere ball ;
    Paddle leftPaddle;
    static Paddle rightPaddle;

    //ADDITIONAL VARIATION 3
    //Add powerups (e.g., if your paddle makes the ball hit a powerup placed at a random location,
    // your paddle speeds up
    Sphere powerUp;
    Sphere powerDown;
    static  boolean vsComp;
    static ArrayList<DrawableObject>drawableObjects;

    //GRAPHICS 1
    //Add a flame trail to the ball.
    static ArrayList<Sphere>trailSpheres;

    public World(int initWidth, int initHeight, boolean vsComputer,boolean hardLevel){
        Random random=new Random();
        width = initWidth;
        height = initHeight;
        ball=new Sphere(60);
        drawableObjects=new ArrayList<>();
        vsComp=vsComputer;


        //ADDITIONAL VARIATION 3
        //Add powerups (e.g., if your paddle makes the ball hit a powerup placed at a random location,
        // your paddle speeds up
        //initialize powerUp
        powerUp=new Sphere(25);
        powerUp.color=Color.yellow;
        powerUp.position=new Pair(512, 65);
        //powerUp.position=new Pair(random.nextInt(400)+200, random.nextInt(700)+45);

        //initialize powerDown
        powerDown=new Sphere();
        //hiding the power-down no longer using it
        powerDown.color=Color.black;
        powerDown.position=new Pair(512, 645);
        ///powerDown.position=new Pair(random.nextInt(400)+200, random.nextInt(700)+45);


        ball.color=Color.green;

        rightPaddle = new Paddle(new Pair(1024 - 100, 324));
        rightPaddle.color=Color.BLUE;

        if(vsComputer && !hardLevel){
            //if playing against computer
            rightPaddle.height=200;
            rightPaddle.velocity.y=400;
        }
        if(vsComputer && hardLevel){
            //if playing against computer
            rightPaddle.position.y=184;
            rightPaddle.height=400;
            //give computer bigger paddle
            rightPaddle.velocity.y=400;
        }
        if(hardLevel && !vsComputer){
            //make game progressively faster

            ball.dampening=1.1;

        }
        leftPaddle=new Paddle(new Pair(70,324));
        leftPaddle.color=Color.red;

        drawableObjects.add(ball);
        drawableObjects.add(leftPaddle);
        drawableObjects.add(rightPaddle);
        drawableObjects.add(powerDown);
        drawableObjects.add(powerUp);

        trailSpheres=new ArrayList<>();

        //GRAPHICS 1
        //Add a flame trail to the ball.
        //add a trail next to the ball
        for(int i=0; i<200; i++){
            Sphere trail=new Sphere(random.nextInt(10));
            trail.color=Color.orange;
            //trail.color=new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
            trail.setVelocity(new Pair(random.nextInt(170), random.nextInt(170)));

            trailSpheres.add(trail);
            drawableObjects.add(trail);
        }
        for(int i=0; i<38; i++){
            Paddle net=new Paddle(new Pair(512, 20*i));
            net.height=10;
            net.width=10;
            net.color=Color.white;
            drawableObjects.add(net);
        }

    }
    public void drawObjects(Graphics g){
        for(DrawableObject obj: drawableObjects){
//            System.out.println("Inside drawable objects");
            obj.draw(g);
        }
    }



    public void updateSpheres(double time){
        ball.updateSphere(this, time,leftPaddle,rightPaddle);
        for(int i=0; i<trailSpheres.size(); i++){
            Sphere current=trailSpheres.get(i);
            current.updateTrailSphere(time,ball,rightPaddle);
        }
    }
    public void updatePaddles(double time){
        leftPaddle.updateLeftPaddle(this,time);
        rightPaddle.updateRightPaddle(this,time);
    }

}

public class Pong extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;
    int leftPlayerScore=0; //to keep track of the score
    int rightPlayerScore=0;

    class Runner implements Runnable{
        public void run() {
            while(true){
                world.updateSpheres(1.0 / (double)FPS);
                //UNCOMMENT THIS TO MOVE THE PADDLES
                world.updatePaddles(1.0 / (double)FPS);
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
        //System.out.println("You pressed down: " + c);

        //INPUT
        //r, f, and v should control the left paddle,
        // while u, j, and n, should control the right paddle.
        if(c=='v'){
            world.leftPaddle.velocity.y=250*world.leftPaddle.paddleDamper;
        }

        if(c=='f'){
            world.leftPaddle.velocity.y=0;
        }
        if(c=='r'){
            world.leftPaddle.velocity.y=-250*world.leftPaddle.paddleDamper;
        }
        if(!World.vsComp) {
            //r u should make the paddles move upwards
            if (c == 'u') {
                World.rightPaddle.velocity.y = -250 * World.rightPaddle.paddleDamper;
            }

            //f j should make the paddles stop moving
            if (c == 'j') {
                World.rightPaddle.velocity.y = 0;
            }
            //v n should make the paddles move downwards
            if (c == 'n') {
                World.rightPaddle.velocity.y = 250 * World.rightPaddle.paddleDamper;
            }
        }
    }


    public void keyReleased(KeyEvent e) {
        char c=e.getKeyChar();
    }


    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public Pong(boolean vsComputer, boolean hardLevel){
        world = new World(WIDTH, HEIGHT, vsComputer,hardLevel);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
    }
static Scanner scanner=new Scanner(System.in);
    public static void main(String[] args){

        boolean vComputer;
        boolean hardLevel;
        System.out.println("PICK A GAME MODE : ENTER 1 FOR ONE PLAYER AND 2 FOR 2 PLAYER");
        int mode=scanner.nextInt();
        vComputer= mode == 1;

            System.out.println("WHAT LEVEL DO YOU WANT TO PLAY? : ENTER 1 FOR EASY AND 2 FOR HARD");

        int level= scanner.nextInt();
        hardLevel=level==2;

//
//        System.out.print("PICK LEVEL : PRESS 1 FOR EASY AND 2 FOR HARD");
//        int level= scanner.nextInt();

        JFrame frame = new JFrame("Pong!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Pong mainInstance = new Pong(vComputer, hardLevel);
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        world.drawObjects(g);

    }
}
