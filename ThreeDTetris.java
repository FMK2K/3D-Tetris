package FinalProject;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.List;

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
    void landed(World w);
    void update(World w, double jump);
}

abstract class Tetromino{
    Cube cube1;
    Cube cube2;
    Cube cube3;
    Cube cube4;
    int size=World.sizeOfCubes;
    Color color;
    Random rand=new Random();
    public abstract void rotate1();
    public abstract void rotate2();
    public abstract void rotate3();
    public abstract void rotate4();
}
class Cube implements methods{
    Pair position;
    Pair velocity;
    Pair acceleration;
    double size;
    double dampening;
    Color color;
    boolean keepMoving;

    public Cube(Color color1) {
        Random rand = new Random();
        position = new Pair(World.sizeOfCubes*16, -25);
        velocity = new Pair(0,0);
        acceleration = new Pair(0, 0);
        size = World.sizeOfCubes*0.5;
        dampening = 1;
        keepMoving =true;
        color=color1;
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
    public Color getColor(){
        return color;
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

    public void landed(World w){
        //a piece stops if it touches the floor or the top of another cube

            if (position.y > World.endOfHeight || w.landsOnOtherCube(w.piece.cube1) ||
                    w.landsOnOtherCube(w.piece.cube2) || w.landsOnOtherCube(w.piece.cube3)||
                    w.landsOnOtherCube(w.piece.cube4)) {

                //this stops the cubes from moving farther
                ThreeDTetris.keepMovingDown =false;
                keepMoving =false;

                World.addCubes(World.tetrominos, w.piece);

                World.isGameOver();
                //Checks if game is over after you add new cubes


                if(!World.gameOver) {
                    //only create another piece if game is not over
                    w.piece = World.newRandomPiece();

                    //re-initialize rotateChecker to 1 everytime you create a new piece
                    World.rotateChecker=1;
                }
            }
        }

    public void update(World w,double jump){

        if(keepMoving && !World.gameOver) {
            //only move cubes if game is not over and we are allowed to keep moving
                position.y += jump * World.sizeOfCubes;
        }
        landed(w);


    }
}

//WE HAVE SEVEN CLASSES FOR SEVEN PIECES
class LinePiece extends Tetromino{
    //             [4][3][2][1]

    public LinePiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

        cube1=new Cube(color);


        cube2=new Cube(color);
        cube2.position.x=cube1.position.x-size;


        cube3=new Cube(color);
        cube3.position.x=cube2.position.x-size;


        cube4=new Cube(color);
        cube4.position.x=cube3.position.x-size;
    }

    public void rotate1(){

//       [4][3][2][1]  --->   [4]
//                            [3]
//                            [2]
 //                           [1]
        //you can't do this rotation if there is a cube less than three cubes under four
        //since you need three spaces under cube 4
        if(clearToRotate1()) {
            cube3.position.y = cube4.position.y + size;
            cube3.position.x = cube4.position.x;
            cube2.position.y = cube3.position.y + size;
            cube2.position.x = cube4.position.x;
            cube1.position.y = cube2.position.y + size;
            cube1.position.x = cube4.position.x;
            World.rotateChecker=2;
        }
    }
    private boolean clearToRotate1() {
        if(cube4.position.y>=World.endOfHeight){
            return false;
        }
        for(Cube t:World.tetrominos){
            if(t.position.x ==cube4.position.x  && t.position.y <= cube4.position.y+3*size ){
                return false;
            }
        }
        return true;
    }

    public void rotate2(){
        // [4]
        // [3]
        // [2]
        // [1]  --->  [1][2][3][4]

        if(clearToRotate2()) {
            cube2.position.y = cube1.position.y;
            cube2.position.x = cube1.position.x + size;
            cube3.position.y = cube1.position.y;
            cube3.position.x = cube2.position.x + size;
            cube4.position.y = cube1.position.y;
            cube4.position.x = cube3.position.x + size;
            World.rotateChecker = 3;
        }
    }

    private boolean clearToRotate2() {
        if(cube1.position.x>World.endOfWidth){
            return false;
        }
        for(Cube t:World.tetrominos){
            if(t.position.y==cube1.position.y && t.position.x<=cube1.position.y+3*size ){
                return false;
            }
        }
        return true;
    }

    public void rotate3(){
//  [1][2][3][4]   --> [1]
//                     [2]
//                     [3]
//                     [4]
        if(clearToRotate3()) {
            cube2.position.y = cube1.position.y + size;
            cube2.position.x = cube1.position.x;
            cube3.position.y = cube2.position.y + size;
            cube3.position.x = cube1.position.x;
            cube4.position.y = cube3.position.y + size;
            cube4.position.x = cube1.position.x;
            World.rotateChecker = 4;
        }

    }

    private boolean clearToRotate3() {
        if(cube4.position.y>=World.endOfHeight){
            return false;
        }
        for(Cube t:World.tetrominos){
            if(t.position.x==cube1.position.x && t.position.y<=cube1.position.y+3*size){
                return false;
            }
        }
        return true;
    }

    public void rotate4() {
        //        [1]
        //        [2]
        //        [3]
        //        [4]   ---> [4][3][2][1]
        if (clearToRotate4()){
            cube3.position.y = cube4.position.y;
        cube3.position.x = cube4.position.x + size;
        cube2.position.y = cube4.position.y;
        cube2.position.x = cube3.position.x + size;
        cube1.position.y = cube4.position.y;
        cube1.position.x = cube2.position.x + size;
        World.rotateChecker = 1;
    }
    }

    private boolean clearToRotate4() {
        if(cube4.position.x>=World.endOfWidth){
            return false;
        }
        for(Cube t:World.tetrominos){
            if(t.position.y==cube4.position.y && t.position.x<=cube4.position.y+3*size){
                return false;
            }
        }
        return true;
    }
}
class SquarePiece extends Tetromino{
    //              [4][3]
//                  [2][1]

    public SquarePiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

        cube1=new Cube(color);

        cube2=new Cube(color);
        cube2.position.x=cube1.position.x-size;
        cube2.position.y=cube1.position.y;

        cube3=new Cube(color);
        cube3.position.x=cube1.position.x;
        cube3.position.y=cube1.position.y-size;

        cube4=new Cube(color);
        cube4.position.x=cube2.position.x;
        cube4.position.y=cube3.position.y;
    }

    @Override
    public void rotate1() {

    }

    @Override
    public void rotate2() {

    }

    @Override
    public void rotate3() {

    }

    @Override
    public void rotate4() {

    }
}
class TPiece extends Tetromino {
    //This piece is shaped like the letter T
    // composed of a row of three blocks with one added above the center.

    public TPiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

//        [4][3][2]
//           [1]
        cube1=new Cube(color);

        cube2=new Cube(color);
        cube2.position.x=cube1.position.x+size;
        cube2.position.y=cube1.position.y-size;

        cube3=new Cube(color);
        cube3.position.x=cube1.position.x;
        cube3.position.y=cube1.position.y-size;

        cube4=new Cube(color);
        cube4.position.x=cube1.position.x-size;
        cube4.position.y=cube1.position.y-size;

    }

    @Override
    public void rotate1() {
        //        [4][3][2]         [4]
//                   [1]    ->   [1][3]
 //                                 [2]
        if(clearToRotate1()){
            cube4.position.x+=2*size;
            cube3.position.x+=size;
            cube3.position.y+=size;
            cube2.position.y+=2*size;
            World.rotateChecker=2;
        }
    }

    private boolean clearToRotate1() {
        if(cube1.position.y>=World.endOfHeight){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.x==cube2.position.x &&
                        (t.position.y==cube2.position.y+size || t.position.y==cube2.position.y+2*size  )){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate2() {
//           [4]
//        [1][3]             [1]
//           [2]    ----> [2][3][4]
        if(clearToRotate2()){
            cube3.position.x-=size;
            cube3.position.y+=size;
            cube2.position.x-=2*size;
            cube4.position.y+=2*size;
            World.rotateChecker=3;
        }
    }

    private boolean clearToRotate2() {
        if(cube3.position.x>=World.endOfWidth){
            return false;
        }else {
            for(Cube t: World.tetrominos){
                if(t.position.y==cube2.position.y &&
                        (t.position.x==cube1.position.x || t.position.x==cube1.position.x-size  )){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate3() {
//              [1]
//           [2][3][4]  --->  [2]
//                            [3][1]
//                            [4]
        if(clearToRotate3()){
            cube3.position.x-=size;
            cube3.position.y-=size;
            cube2.position.y-=2*size;
            cube4.position.x-=2*size;

            World.rotateChecker=4;
        }

    }

    private boolean clearToRotate3() {
        for(Cube t: World.tetrominos){
        if(t.position.x==cube2.position.x &&
                (t.position.y==cube2.position.y-size || t.position.y==cube2.position.y-2*size  )){
            return false;
        }
    }
        return true;
    }

    @Override
    public void rotate4() {
//        [2]
//        [3][1]
//        [4]      ---> [4][3][2]
//                         [1]
        if(clearToRotate4()){
            cube3.position.x+=size;
            cube3.position.y-=size;
            cube2.position.x+=2*size;
            cube4.position.y-=2*size;
            World.rotateChecker=1;
        }

    }

    private boolean clearToRotate4() {
        if(cube1.position.x>=World.endOfWidth){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.y==cube2.position.y &&
                        (t.position.x==cube1.position.x || t.position.x==cube1.position.x+size  )){
                    return false;
                }
            }
        }
        return true;
    }
}
class JPiece extends Tetromino{
// is shaped like a mirror-reversed 'L' and consists of three blocks in a row with one added above the left side.
//              [4]
//              [3]
//           [2][1]

    public JPiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        cube1=new Cube(color);

        cube2=new Cube(color);
        cube2.position.x=cube1.position.x-size;
        cube2.position.y=cube1.position.y;

        cube3=new Cube(color);
        cube3.position.x=cube1.position.x;
        cube3.position.y=cube1.position.y-size;

        cube4=new Cube(color);
        cube4.position.x=cube3.position.x;
        cube4.position.y=cube3.position.y-size;
    }

    @Override
    public void rotate1() {
        //      [4]
//              [3]       [3]
//           [2][1]   --->[2][1][4]
        if(clearToRotate1()){
            cube3.position.x-=size;
            cube4.position.x+=size;
            cube4.position.y+=2*size;
            World.rotateChecker=2;
        }

    }

    private boolean clearToRotate1() {
        if(cube1.position.x>=World.endOfWidth){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.x==cube2.position.x && t.position.y==cube3.position.y){
                    return false;
                }
                if(t.position.x==cube1.position.x+size && t.position.y==cube2.position.y){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate2() {
        //[3]
        //[2][1][4] ---> [2][1]
//                       [3]
//                       [4]
        if(clearToRotate2()){
            cube3.position.y+=2*size;
            cube4.position.x-=2*size;
            cube4.position.y+=2*size;
            World.rotateChecker=3;
        }

    }

    private boolean clearToRotate2() {
        if(cube2.position.y>=World.endOfHeight){
            return false;
        }else {
            for(Cube t: World.tetrominos){
                if(t.position.x==cube2.position.x &&
                        (t.position.y==cube2.position.y+size || t.position.y==cube2.position.y+2*size  )){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate3() {
//        [2][1]
//        [3]
//        [4]   -->   [2][1][3]
//                          [4]
        if(clearToRotate3()){
            cube4.position.x+=2*size;
            cube3.position.x+=2*size;
            cube4.position.y-=size;
            cube3.position.y-=size;
            World.rotateChecker=4;
        }
    }

    private boolean clearToRotate3() {
        if(cube1.position.x>=World.endOfWidth){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.x==cube1.position.x+size &&
                        (t.position.y==cube2.position.y+size || t.position.y==cube2.position.y  )){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate4() {

//                           [4]
//                           [3]
//        [2][1][3]  ---> [2][1]
//              [4]
        if(clearToRotate4()){
            cube4.position.x-=size;
            cube3.position.x-=size;
            cube4.position.y-=3*size;
            cube3.position.y-=size;
            World.rotateChecker=1;
        }

    }

    private boolean clearToRotate4() {
        for(Cube t: World.tetrominos){
            if(t.position.x==cube1.position.x &&
                    (t.position.y==cube1.position.y-size || t.position.y==cube1.position.y-2*size  )){
                return false;
            }
        }
        return true;
    }
}
class LPiece extends Tetromino {
    //              [4]
//                  [3]
//                  [2][1]
    public LPiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        cube1=new Cube(color);

        cube2=new Cube(color);
        cube2.position.x=cube1.position.x-size;
        cube2.position.y=cube1.position.y;

        cube3=new Cube(color);
        cube3.position.x=cube2.position.x;
        cube3.position.y=cube2.position.y-size;

        cube4=new Cube(color);
        cube4.position.x=cube2.position.x;
        cube4.position.y=cube3.position.y-size;
    }

    @Override
    public void rotate1() {
        //              [4]
//                      [3]
//                      [2][1]  ---> [2][1][4]
        //                           [3]
        if(clearToRotate1()){
            cube4.position.x+=2*size;
            cube4.position.y+=2*size;
            cube3.position.y+=2*size;
            World.rotateChecker=2;
        }
    }

    private boolean clearToRotate1() {
        if(cube2.position.y>=World.endOfHeight || cube1.position.x>=World.endOfWidth){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.y==cube1.position.y && t.position.x==cube1.position.x+size){
                    return false;
                }
                if(t.position.x==cube2.position.x && t.position.y==cube2.position.y+size){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void rotate2() {
//          [2][1][4]  -->  [2][1]
//          [3]                [3]
//                             [4]
        if(clearToRotate2()){
            cube3.position.x+=size;
            cube4.position.x-=size;
            cube4.position.y+=2*size;
            World.rotateChecker=3;
        }

     }

    private boolean clearToRotate2() {
        if(cube3.position.y>=World.endOfHeight){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.x==cube1.position.x &&
                        (t.position.y==cube1.position.y+size || t.position.y==cube1.position.y+2*size)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate3() {
//       [2][1]
//          [3]            [4]
//          [4] ---> [2][1][3]
        if(clearToRotate3()){
            cube4.position.x+=size;
            cube4.position.y-=3*size;
            cube3.position.x+=size;
            cube3.position.y-=size;
            World.rotateChecker=4;
        }
    }

    private boolean clearToRotate3() {
        if(cube3.position.x>=World.endOfWidth){
            return false;
        }else{
            for(Cube t: World.tetrominos){
                if(t.position.x==cube1.position.x+size &&
                        (t.position.y==cube1.position.y || t.position.y==cube1.position.y+size)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotate4() {
        //            [4]
        //      [2][1][3]  ---> [4]
        //                      [3]
        //                      [2][1]
        if(clearToRotate4()){
            cube3.position.x-=2*size;
            cube3.position.y-=size;
            cube4.position.x-=2*size;
            cube4.position.y-=size;
            World.rotateChecker=1;
        }
    }

    private boolean clearToRotate4() {
        for(Cube t: World.tetrominos){
            if(t.position.x==cube2.position.x &&
                    (t.position.y==cube2.position.y+size || t.position.y==cube2.position.y+2*size)){
                return false;
            }
        }
        return true;
    }
}
class SPiece extends Tetromino {
    //              [4][3]
//               [2][1]

    public SPiece(){
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

        cube1=new Cube(color);

        cube2=new Cube(color);
        cube2.position.x=cube1.position.x-size;
        cube2.position.y=cube1.position.y;

        cube3=new Cube(color);
        cube3.position.x=cube1.position.x+size;
        cube3.position.y=cube1.position.y-size;

        cube4=new Cube(color);
        cube4.position.x=cube1.position.x;
        cube4.position.y=cube3.position.y;
    }

    @Override
    public void rotate1() {
        //              [4][3]        [4]
//                   [2][1]     ----> [2][1]
        //                               [3]
        if(clearToRotate1()){
            cube4.position.x-=size;
            cube3.position.y+=2*size;
            cube3.position.x-=size;
            World.rotateChecker=2;
        }

    }

    private boolean clearToRotate1() {
        for(Cube t: World.tetrominos){
            if(t.position.x==cube2.position.x && t.position.y==cube4.position.y){
                return false;
            }
            if(t.position.x==cube1.position.x && t.position.y==cube1.position.y+size){
                return false;
            }
            if(cube2.position.y>=World.endOfHeight){
                return false;
            }
        }
        return true;
    }

    @Override
    public void rotate2() {
//        [4]
//        [2][1]           [2][1]
//           [3]   ---> [3][4]
        if(clearToRotate2()){
            cube3.position.x-=2*size;
            cube4.position.y+=2*size;

            World.rotateChecker=3;
        }
    }

    private boolean clearToRotate2() {
        for(Cube t: World.tetrominos){
            if(t.position.y==cube4.position.y && t.position.x==cube2.position.x-size){
                return false;
            }
            if(cube4.position.x<=World.startOfWidth){
                return false;
            }
        }
        return true;
    }

    @Override
    public void rotate3() {
        //      [2][1]        [4]
//           [3][4]      ---> [2][1]
        //                       [3]
        if(clearToRotate3()){
            cube4.position.y-=2*size;
            cube3.position.x+=2*size;
            World.rotateChecker=4;
        }
    }

    private boolean clearToRotate3() {
        for(Cube t: World.tetrominos){
            if(t.position.x==cube2.position.x && t.position.y==cube2.position.y-size){
                return false;
            }
            if(t.position.x==cube1.position.x && t.position.y==cube1.position.y+size){
                return false;
            }
        }
        return true;
    }

    @Override
    public void rotate4() {
        //    [4]
        //    [2][1]
        //       [3]

        //          [4][3]
//               [2][1]
        if(clearToRotate4()){
            cube4.position.x+=size;
            cube3.position.y-=2*size;
            cube3.position.x+=size;

            World.rotateChecker=1;
        }
    }

    private boolean clearToRotate4() {
        for(Cube t: World.tetrominos){
            if(t.position.x==cube2.position.x-size && t.position.y==cube4.position.y){
                return false;
            }
            if(cube4.position.x<=World.startOfWidth){
                return false;
            }
        }
        return true;
    }
}
class ZPiece extends Tetromino {
    //              [4][3]
//                     [2][1]
public ZPiece(){

    color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

    cube1=new Cube(color);

    cube2=new Cube(color);
    cube2.position.x=cube1.position.x-size;
    cube2.position.y=cube1.position.y;

    cube3=new Cube(color);
    cube3.position.x=cube2.position.x;
    cube3.position.y=cube2.position.y-size;

    cube4=new Cube(color);
    cube4.position.x=cube3.position.x-size;
    cube4.position.y=cube3.position.y;
}

    @Override
    public void rotate1() {
        //              [4][3]              [4]
//                         [2][1]  ----> [2][1]
 //                                      [3]

        if(clearToRotate1()){
            cube4.position.x=cube1.position.x;
            cube3.position.y=cube2.position.y+size;
            World.rotateChecker=2;
        }
    }

    private boolean clearToRotate1() {
         for(Cube t: World.tetrominos){
             if(t.position.x==cube1.position.x && t.position.y==cube4.position.y){
                 return false;
             }
             if(t.position.x==cube2.position.x && t.position.y== cube2.position.y+size){
                 return false;
             }
             if(cube2.position.y==World.endOfHeight){
                 return false;
             }
         }
         return true;
    }

    @Override
    public void rotate2() {
    //             [4]      [4][3]
        //      [2][1]    ---> [2][1]
        //      [3]
        if(clearToRotate2()){
            cube4.position.x=cube2.position.x-size;
            cube3.position.x=cube2.position.x;
            cube3.position.y=cube4.position.y;
            World.rotateChecker=3;
        }

    }

    private boolean clearToRotate2() {
        for(Cube t: World.tetrominos){
            if(t.position.y==cube4.position.y && t.position.x==cube2.position.x-size){
                return false;
            }
        }
        return true;
    }

    @Override
    public void rotate3() {
    //        [4][3]              [3]
        //       [2][1]  ----> [2][1]
        //                     [4]
        //

        if(clearToRotate3()){
            cube3.position.x+=size;
            cube4.position.y=cube2.position.y+size;
            cube4.position.x=cube2.position.x;
            World.rotateChecker=4;
        }
    }

    private boolean clearToRotate3() {
        for(Cube t: World.tetrominos){
            if((t.position.x==cube3.position.x +size &&  t.position.y ==cube3.position.y)||
                    (t.position.x==cube2.position.x && t.position.y==cube2.position.y +size )){
                return false;
            }
            if(cube2.position.y==World.endOfHeight){
                return false;
            }
        }
        return true;
    }

    @Override
    public void rotate4() {
    //            [3]
        //     [2][1]            [4][3]
        //     [4]                 [2][1]
        if(clearToRotate4()){
           cube4.position.y-=size;
           cube4.position.x-=size;
           cube3.position.y+=size;
           cube3.position.x-=size;
           cube1.position.y+=size;
           cube2.position.y+=size;

            World.rotateChecker=1;
        }
    }

    private boolean clearToRotate4() {
        for(Cube t: World.tetrominos){
            if(t.position.y==cube4.position.y &&
                    (t.position.x==cube1.position.x || t.position.x==cube1.position.x+size)){
                return false;
            }

        }
        return true;
    }
}

class World {
    static int height, startOfHeight, endOfHeight;

    static int width, startOfWidth, endOfWidth;
    Tetromino piece;
    static int sizeOfCubes=30;
    static boolean gameOver;
    static int rotateChecker;

    //when we add a block to the array we will add the cubes that make up the block
    static ArrayList<Cube> tetrominos;
    private int timer;
    private int timer1;
    //private int timer;


    public World(int initWidth, int initHeight) {
        width = initWidth;
        height = initHeight;
        tetrominos = new ArrayList<>();
        gameOver=false;
        rotateChecker=1;
        startOfHeight=9*sizeOfCubes;
        endOfHeight=height - 2*sizeOfCubes;
        startOfWidth=2*sizeOfCubes;
        endOfWidth=width-sizeOfCubes;

        //create a new random piece to be our current piece
        piece = newRandomPiece();

        timer=-50;
        timer1=0;
        for(int i=0; i<5; i++) {
            Random rand = new Random();

            Cube starter = new Cube(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
            starter.setPosition(new Pair(startOfWidth+i*i*sizeOfCubes, endOfHeight));
            starter.setVelocity(new Pair(0, 0));
            tetrominos.add(starter);

            Cube end = new Cube(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));

            end.setPosition(new Pair(endOfWidth-i*i*sizeOfCubes, endOfHeight));
            end.setVelocity(new Pair(0, 0));
            tetrominos.add(end);
        }

    }

    public static Tetromino newRandomPiece() {
        //generate a random number from 0 to 6 and creates a piece based on the number
            Tetromino t=null;
            int n= (int) (Math.random()*7);

            if(n==0){t= new LinePiece();}
            if(n==1){t= new SquarePiece();}
            if(n==2){t= new TPiece();}
            if(n==3){t= new ZPiece();}
            if(n==4){t= new SPiece();}
            if(n==5){t= new LPiece();}
            if(n==6){t= new JPiece();}
            ThreeDTetris.keepMovingDown=true;


            //Change to t
            return  t;
    }

    public static void addCubes(ArrayList<Cube> tetrominos, Tetromino piece) {


        tetrominos.add(piece.cube1);
        tetrominos.add(piece.cube2);
        tetrominos.add(piece.cube3);
        tetrominos.add(piece.cube4);

        if(piece.cube1.position.y>endOfHeight || piece.cube2.position.y>endOfHeight ||
                piece.cube3.position.y>endOfHeight || piece.cube4.position.y>endOfHeight) {

            //piece.cube1.position.y = roundToNearestMultipleOfSize(piece.cube1.position.y)-sizeOfCubes;
            piece.cube1.position.y=roundToLowerMultipleOfSize(piece.cube1.position.y);
            piece.cube2.position.y = roundToNearestMultipleOfSize(piece.cube2.position.y);
            piece.cube3.position.y = roundToNearestMultipleOfSize(piece.cube3.position.y);
            piece.cube4.position.y = roundToNearestMultipleOfSize(piece.cube4.position.y);
        }else {
            //you want to align the cubes along the y-axis
        piece.cube1.position.y=roundToLowerMultipleOfSize(piece.cube1.position.y);
        piece.cube2.position.y=roundToLowerMultipleOfSize(piece.cube2.position.y);
        piece.cube3.position.y=roundToLowerMultipleOfSize(piece.cube3.position.y);
        piece.cube4.position.y=roundToLowerMultipleOfSize(piece.cube4.position.y);
        }
    }

    //MOVE THIS METHOD TO CUBE CLASS
    public  boolean landsOnOtherCube( Cube cube) {
        //this checks if any cube in our current piece stops on top of another cube

        for(Cube t: World.tetrominos){
            if ( cube.position.x >t.position.x-cube.size/2 && cube.position.x <t.position.x+cube.size/2 &&
                         cube.position.y> t.position.y -2* cube.size && cube.position.y<t.position.y) {
                cube.keepMoving=false;
                ThreeDTetris.keepMovingDown =false;
                return true;

            }

        }
        return false;
    }
    //MOVE THIS METHOD TO CUBE CLASS
    public static double roundToLowerMultipleOfSize(double number) {
        // Calculate the nearest multiple of size
        double remainder = number % sizeOfCubes;
        return  number - remainder;
    }
    //MOVE THIS METHOD TO CUBE CLASS
    public static double roundToNearestMultipleOfSize(double number) {
        // Calculate the nearest multiple of size
        double remainder = number % sizeOfCubes;
        double nearestMultiple = number - remainder;

        // Determine whether to round up or down based on the remainder
        if (remainder >= sizeOfCubes/2 && nearestMultiple< height-2*sizeOfCubes) {
            nearestMultiple += sizeOfCubes;
        }
        return nearestMultiple;

    }

    public void drawTetrominos(Graphics g) {
        for(int i=startOfWidth-sizeOfCubes; i<endOfWidth; i=i+sizeOfCubes) {
            g.setColor(Color.black);
            g.drawRect(i, startOfHeight, sizeOfCubes, endOfHeight - startOfHeight);
            //g.setColor(Color.cyan);
            g.drawRect(i, 0, sizeOfCubes, startOfHeight);
        }
        g.setColor(Color.cyan);
        g.fillRect(width-5*sizeOfCubes,0, 5*sizeOfCubes, 5*sizeOfCubes);

        // Sort the tetrominos ArrayList based on x positions in descending order, to avoid overlaps when drawn
        ArrayList<Cube>allCubes=new ArrayList<>();
        allCubes.addAll(tetrominos);
        allCubes.add(piece.cube1);
        allCubes.add(piece.cube2);
        allCubes.add(piece.cube3);
        allCubes.add(piece.cube4);
        Collections.sort(allCubes, new Comparator<Cube>() {
                    @Override
                    public int compare(Cube cube1, Cube cube2) {
                        if(cube1.position.y!=cube2.position.y){
                            return Double.compare(cube2.position.y, cube1.position.y);
                        }
                        // Compare cubes based on their x positions in descending order
                        return Double.compare(cube2.getPosition().x, cube1.getPosition().x);
                    }
                }
        );
        for(Cube cube:allCubes){
            cube.draw(g);
        }


    }

    public void updateTetrominos() {
        //first align all the cubes before we start moving them
        piece.cube1.position.y=roundToNearestMultipleOfSize(piece.cube1.position.y);
        piece.cube2.position.y=roundToNearestMultipleOfSize(piece.cube2.position.y);
        piece.cube3.position.y=roundToNearestMultipleOfSize(piece.cube3.position.y);
        piece.cube4.position.y=roundToNearestMultipleOfSize(piece.cube4.position.y);
        timer++;

        if(piece.cube1.position.y<height-2.5*sizeOfCubes &&
                piece.cube2.position.y<height-2.5*sizeOfCubes &&
                piece.cube3.position.y<height-2.5*sizeOfCubes &&
                piece.cube4.position.y<height-2.5*sizeOfCubes ) {


            if (timer > 20) {
                piece.cube1.update(this, 0.5);
                piece.cube2.update(this, 0.5);
                piece.cube3.update(this, 0.5);
                piece.cube4.update(this, 0.5);

                timer = 0;
            }
        }else{
            if (timer > 50) {
                piece.cube1.update(this, 0.5);
                piece.cube2.update(this,0.5);
                piece.cube3.update(this,0.5);
                piece.cube4.update(this, 0.5);
                timer = 0;
            }
        }
        timer1++;
        if (timer1>5) {
            checkIfAnyLevelIsFullAndDelete();
            timer1=0;
        }
    }


        //This checks if any cube is above the limit of our window and ends the game
        public static void isGameOver() {
        for (Cube t: tetrominos){
            if (t.position.y < startOfHeight) {
                gameOver = true;
                break;
            }
        }
    }

    private void checkIfAnyLevelIsFullAndDelete() {
        int[]filledRows=new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        //this keeps track of filled rows
        for(int row=startOfHeight; row<=endOfHeight; row=row+sizeOfCubes){
            for(Cube t: tetrominos){
                if(t.position.y ==row){
                    filledRows[(row-startOfHeight)/sizeOfCubes]++;
                }
            }
        }
        for(int i=0; i<filledRows.length; i++){
            //this checks if a row is filled up
            if(filledRows[i]==28) {
                Iterator<Cube>deleteIterator= tetrominos.iterator();
                while (deleteIterator.hasNext()){
                    //This removes any cube on the filled up row

                    Cube cube =deleteIterator.next();
                    if((cube.position.y-startOfHeight)/sizeOfCubes==i){
                        deleteIterator.remove();
                    }
                    //This drops any cube above the filled up row

                    if((cube.position.y-startOfHeight)/sizeOfCubes<i){
                        cube.position.y+=sizeOfCubes;
                    }

                    //Check for hanging cubes???
                }
            }
        }
    }
}

public class ThreeDTetris extends JPanel implements KeyListener{
    public static final int WIDTH = World.sizeOfCubes*30;
    public static final int HEIGHT = World.sizeOfCubes*25;
    public static final int FPS = 60;
    World world;
    static boolean keepMovingDown;


    class Runner implements Runnable{
        public void run() {
            while(true){
                world.updateTetrominos();
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


    }

    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 's' || c == 'S') {
            int n = World.sizeOfCubes;
            if (keepMovingDown && notCloseToAnotherCube()) {
                //you are able to change position as long as you are allowed to keep moving down
                //and if the piece hasn't landed on another piece
                if (world.piece.cube1.position.y < World.endOfHeight-n) {

                    world.piece.cube1.position.y += n;
                    world.piece.cube2.position.y += n;
                    world.piece.cube3.position.y += n;
                    world.piece.cube4.position.y += n;
                }
            }
        }
        double n = world.piece.size;
        if (c == 'd' || c == 'D') {

            if (lessThanWidth()) {
                if (rightHorizontalPlane() && notTouchingAnyCubeOnTheRight()) {
                    //you can only move the current piece to the right if its not touching other pieces on the right
                    world.piece.cube1.position.x += n;
                    world.piece.cube2.position.x += n;
                    world.piece.cube3.position.x += n;
                    world.piece.cube4.position.x += n;
                }

                //WEIRD THINGS HAPPEN AT THE BOTTOM ROW
                if (world.piece.cube1.position.y == World.endOfHeight ||
                        world.piece.cube2.position.y == World.endOfHeight ||
                        world.piece.cube3.position.y == World.endOfHeight ||
                        world.piece.cube4.position.y == World.endOfHeight) {

                    if (positionOnTheRightIsEmpty()) {
                        world.piece.cube1.position.x += n;
                        world.piece.cube2.position.x += n;
                        world.piece.cube3.position.x += n;
                        world.piece.cube4.position.x += n;
                    }
                }

                //Allowed to move when we land on top of another cube
                if (weAreOnTopOfAnotherCube()) {
                    if (positionOnTheRightIsEmpty()) {
                        world.piece.cube1.position.x += n;
                        world.piece.cube2.position.x += n;
                        world.piece.cube3.position.x += n;
                        world.piece.cube4.position.x += n;
                    }
                }
            }
        }

        if (c == 'a' || c == 'A') {
            if(greaterThanStart()){

            if (rightHorizontalPlane() && notTouchingAnyCubeOnTheLeft()) {

                world.piece.cube1.position.x -= n;
                world.piece.cube2.position.x -= n;
                world.piece.cube3.position.x -= n;
                world.piece.cube4.position.x -= n;

            }
            //WEIRD THINGS HAPPEN AT THE BOTTOM ROW
            if (world.piece.cube1.position.y == World.endOfHeight ||
                    world.piece.cube2.position.y == World.endOfHeight ||
                    world.piece.cube3.position.y == World.endOfHeight||
                    world.piece.cube4.position.y == World.endOfHeight) {

                if (positionOnTheLeftIsEmpty()) {
                    world.piece.cube1.position.x -= n;
                    world.piece.cube2.position.x -= n;
                    world.piece.cube3.position.x -= n;
                    world.piece.cube4.position.x -= n;
                }
            }
            //IT'S HARD TO MOVE A PIECE ONCE IT HAS LANDED ON ANOTHER PIECE
            if (weAreOnTopOfAnotherCube()) {
                if (positionOnTheLeftIsEmpty()) {
                    world.piece.cube1.position.x -= n;
                    world.piece.cube2.position.x -= n;
                    world.piece.cube3.position.x -= n;
                    world.piece.cube4.position.x -= n;
                }
            }

        }

    }
        if(c=='r' || c=='R'){
            if(World.rotateChecker==1){
                world.piece.rotate1();

            }else
            if(World.rotateChecker==2){
                world.piece.rotate2();

            }else
            if(World.rotateChecker==3){
                world.piece.rotate3();

            }else
            if(World.rotateChecker==4){
                world.piece.rotate4();

            }

        }

    }

    private boolean weAreOnTopOfAnotherCube() {
        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube : cubeArrayList){
            for(Cube t: World.tetrominos){
                if(t.position.y==cube.position.y+World.sizeOfCubes && t.position.x==cube.position.x){
                    return true;
                }
            }
        }
        return false;
    }

    //THIS CHECKS IF WE CAN MOVE A PIECE THAT'S AT THE BOTTOM TO THE RIGHT
    private boolean positionOnTheRightIsEmpty(){
        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube: cubeArrayList){
            double positionToMove=cube.position.x+World.sizeOfCubes;
            for(Cube t: World.tetrominos){
                if( t.position.y==cube.position.y && t.position.x==positionToMove){
                    return false;
                }
            }
        }
        return true;
    }
    //THIS CHECKS IF WE CAN MOVE A PIECE THAT'S AT THE BOTTOM TO THE LEFT
    private boolean positionOnTheLeftIsEmpty(){
        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube: cubeArrayList){
            double positionToMove=cube.position.x-World.sizeOfCubes;
            for(Cube t: World.tetrominos){
                if(t.position.y==cube.position.y && t.position.x==positionToMove){
                    return false;
                }
            }
        }
        return true;
    }

    //THIS CHECKS IF THE CUBES ARE ON THE RIGHT HORIZONTAL PLANE, YOU DONT WANT TO MOVE A CUBE INTO THE HALF OF ANOTHER CUBE
    private  boolean rightHorizontalPlane(){
        return world.piece.cube1.position.y % World.sizeOfCubes == 0 &&
                world.piece.cube2.position.y % World.sizeOfCubes == 0 &&
                world.piece.cube3.position.y % World.sizeOfCubes == 0 &&
                world.piece.cube4.position.y % World.sizeOfCubes == 0;
    }
    private boolean notCloseToAnotherCube() {
        //this checks if any cube is close to any other cube


        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube: cubeArrayList) {
            for (Cube t : World.tetrominos) {
                if (cube.position.x > t.position.x - cube.size / 2 && cube.position.x < t.position.x + cube.size / 2) {

                    if (cube.position.y > t.position.y - 3 * World.sizeOfCubes) {

                        return false;
                    }

                }

            }
        }
        return true;
    }

    private boolean greaterThanStart(){
        //this checks if the current piece is within the left side bounds of our window
        // starts at 2*sizeOfCubes
        double n = World.sizeOfCubes;
        if(world.piece.cube1.position.x<2.5*n){
            return false;
        }
        if(world.piece.cube2.position.x<2.5*n){
            return false;
        }
        if(world.piece.cube3.position.x<2.5*n){
            return false;
        }
        return !(world.piece.cube4.position.x < 2.5 * n);
    }
    private boolean lessThanWidth(){
        //this checks if the current piece is within the left side bounds of our window
        //ends at width-2*sizeOfCubes
        double n = world.width-1.5*World.sizeOfCubes;

        if(world.piece.cube1.position.x>n){
            return false;
        }
        if(world.piece.cube2.position.x>n){
            return false;
        }
        if(world.piece.cube3.position.x>n){
            return false;
        }
        return !(world.piece.cube4.position.x > n);
    }
    private boolean notTouchingAnyCubeOnTheRight() {
        //This checks if any cube in current piece is touching any other cube to the right
        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube: cubeArrayList) {
            int n = World.sizeOfCubes;
            for (Cube t : World.tetrominos) {
                if(cube.position.y==t.position.y && cube.position.x>t.position.x-1.5*n && cube.position.x<t.position.x-0.5*n){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean notTouchingAnyCubeOnTheLeft() {
        //This checks if any cube in current piece is touching any other cube on their right wall
        ArrayList<Cube>cubeArrayList=new ArrayList<>();
        cubeArrayList.add(world.piece.cube1);
        cubeArrayList.add(world.piece.cube2);
        cubeArrayList.add(world.piece.cube3);
        cubeArrayList.add(world.piece.cube4);

        for(Cube cube: cubeArrayList) {
            int n = World.sizeOfCubes;
            for (Cube t : World.tetrominos) {
                if(cube.position.y==t.position.y && cube.position.x>t.position.x+0.5*n && cube.position.x<t.position.x+1.5*n){
                    return false;
                }
            }
        }
        return true;
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
        keepMovingDown =true;
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

        frame.setLocationRelativeTo(null);
        frame.setContentPane(mainInstance);
        frame.pack();

        frame.setSize(WIDTH,HEIGHT);

        //frame.getContentPane().add(label);

        frame.setVisible(true);

    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        ImageIcon background = new ImageIcon("/Users/davidnkairu/IdeaProjects/COSC-112./resource/images/img.png");
        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

        

        world.drawTetrominos(g);

    }


}
