import java.awt.*;

public class Shape implements Rotation{
    public Square s[] = new Square[4];
    public Square temp[] = new Square[4];
    static int Timer = 0;
    static final int dropTimer = 30;
    int dir = 1;
    boolean isFalling = true;
    boolean leftCollision = false;
    boolean rightCollision = false;
    boolean land = false;

    public void makeSquare(Color c){
        s[0] = new Square(c);
        s[1] = new Square(c);
        s[2] = new Square(c);
        s[3] = new Square(c);

        //Collision Check Array
        temp[0] = s[0];
        temp[1] = s[1];
        temp[2] = s[2];
        temp[3] = s[3];
    }

    public void setPosition(int x, int y){}
    public void update(double time){
        checkCollision();
        makeStasis();
        Timer++;
        if(!land){
            if(Timer == dropTimer){
                for(int i = 0; i < s.length; i++)
                    s[i].position.y += Square.VELOCITY;
                Timer = 0;
            }
        }
    }


    public void draw(Graphics g){}

    public void setRotation(int dir){
        this.dir = dir;

        checkRotate();
        if(!land && !leftCollision && !rightCollision) {
            s[0].position = temp[0].position;
            s[1].position = temp[1].position;
            s[2].position = temp[2].position;
            s[3].position = temp[3].position;
        }
    }


    public void makeStasis(){
        if (land){
            isFalling = false;
        }
    }
    //If Block Fell, it stops moving

    public void checkCollision() {
        rightCollision = false;
        leftCollision = false;
        land = false;

        for (int i = 0; i < s.length; i++) {
            if (s[i].position.x + Square.SIZE == World.width) {
                rightCollision = true;
                // Left / Right Collision
            }
        }
        for (int i = 0; i < s.length; i++) {
            if (s[i].position.y + Square.SIZE == World.height) {
                land = true;
            }
        }
        for (int i = 0; i < s.length; i++) {
            if(s[i].position.x == 0){
                leftCollision = true;
            }
        }
        //Collision with Game Screen / Reached the bottom
        blockCollision();
    }

    //Collision with another block
    private void blockCollision(){
        for(int i = 0; i < World.stasisBlock.size(); i++){
            int CHECK_X  = (int)World.stasisBlock.get(i).position.x;
            int CHECK_Y  = (int)World.stasisBlock.get(i).position.y;

            for(int j = 0; j < s.length; j++){
                if(s[j].position.y + Square.SIZE == CHECK_Y && s[j].position.x == CHECK_X){
                    land = true;
                }
            }

            for(int j = 0; j < s.length; j++){
                if(s[j].position.y == CHECK_Y && s[j].position.x - Square.SIZE == CHECK_X){
                    leftCollision = true;
                }
            }

            for(int j = 0; j < s.length; j++){
                if(s[j].position.y == CHECK_Y && s[j].position.x + Square.SIZE == CHECK_X){
                    rightCollision = true;
                }
            }
        }
    }

    private void checkRotate(){

        for (int i = 0; i < temp.length; i++) {
            if (temp[i].position.x + Square.SIZE > World.width) {
                rightCollision = true;
                // Right Wall Collision
            }
        }
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].position.y + Square.SIZE > World.height) {
                land = true;
            }
        }
        //Bottom screen collision

        for (int i = 0; i < temp.length; i++) {
            if(temp[i].position.x < 0){
                leftCollision = true;
            }
        }
        //Left Wall Collision
    }

    public void rotate1(){}

    public  void rotate2(){}

    public  void rotate3(){}

    public  void rotate4(){}
}

class shape_L extends Shape{

    public shape_L (){
        makeSquare(Color.orange);
    }

    @Override
    public void setPosition(int x, int y) {
        //s[0] is the middle block, remains same through rotation

        // □ s[1]
        // □   s[0]
        // □ □
        // s[2] s[3]

        s[0].position.setPair(x, y);
        s[1].position.setPair(x, y + Square.SIZE);
        s[2].position.setPair(x, y - Square.SIZE);
        s[3].position.setPair(x + Square.SIZE, y + Square.SIZE);

        temp[0] = s[0];
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(s[0].c);
        for(int i = 0; i < s.length; i++)
            s[i].draw(g);
    }

    public void update(double time){
        super.update(time);
        setRotation(dir);
    }

    @Override
    public void rotate1() {
        //
        // □ □ □
        // □

        temp[1].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        temp[2].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        temp[3].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y + Square.SIZE);
        this.dir = 1;
    }

    @Override
    public void rotate2() {
        // □ □
        //   □
        //   □
        temp[1].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[2].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        temp[3].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y - Square.SIZE);
        this.dir = 2;
    }

    @Override
    public void rotate3() {
        //     □
        // □ □ □
        //
        temp[1].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        temp[2].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        temp[3].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y - Square.SIZE);
        this.dir = 3;
    }

    @Override
    public void rotate4() {
        // □
        // □
        // □ □

        temp[1].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        temp[2].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[3].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y + Square.SIZE);
        this.dir = 4;
    }
}

class shape_T extends Shape {
    public shape_T() {
        makeSquare(Color.MAGENTA);
    }

    @Override
    public void setPosition(int x, int y) {
        //s[0] is the middle block, remains same through rotation

        // □ □ □
        //   □


        s[0].position.setPair(x, y);
        s[1].position.setPair(x - Square.SIZE, y);
        s[2].position.setPair(x + Square.SIZE, y);
        s[3].position.setPair(x, y - Square.SIZE);

        temp[0] = s[0];
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(s[0].c);
        for (int i = 0; i < s.length; i++)
            s[i].draw(g);
    }

    public void update(double time) {
        super.update(time);
        setRotation(dir);
    }

    @Override
    public void rotate1() {
        //   □
        // □ □
        //   □

        temp[1].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        temp[2].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[3].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        this.dir = 1;
    }

    @Override
    public void rotate2() {
        //   □
        // □ □ □
        //

        temp[1].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        temp[2].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[3].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        this.dir = 2;
    }

    @Override
    public void rotate3() {
        // □
        // □ □
        // □

        temp[1].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        temp[2].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[3].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        this.dir = 3;
    }

    @Override
    public void rotate4() {
        // □ □ □
        //   □

        temp[1].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        temp[2].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        temp[3].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        this.dir = 4;
    }
}

class shape_O extends Shape{
    public shape_O() {
        makeSquare(Color.YELLOW);
    }

    @Override
    public void setPosition(int x, int y) {

        // □ □
        // □ □


        s[0].position.setPair(x, y);
        s[1].position.setPair(x, y + Square.SIZE);
        s[2].position.setPair(x + Square.SIZE, y);
        s[3].position.setPair(x + Square.SIZE, y + Square.SIZE);

        temp[0] = s[0];
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(s[0].c);
        for (int i = 0; i < s.length; i++)
            s[i].draw(g);
    }
}

class shape_I extends Shape{
    public shape_I() {
        makeSquare(Color.CYAN);
    }

    @Override
    public void setPosition(int x, int y) {
        // □
        // □
        // □
        // □


        s[0].position.setPair(x, y);
        s[1].position.setPair(x, y - Square.SIZE);
        s[2].position.setPair(x, y + Square.SIZE);
        s[3].position.setPair(x, y + 2*Square.SIZE);

        temp[0] = s[0];
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(s[0].c);
        for (int i = 0; i < s.length; i++)
            s[i].draw(g);
    }

    public void update(double time) {
        super.update(time);
        setRotation(dir);
    }

    @Override
    public void rotate1() {
        //
        // □ □ □ □
        //

        temp[1].position.setPair(s[0].position.x - Square.SIZE, s[0].position.y);
        temp[2].position.setPair(s[0].position.x + Square.SIZE, s[0].position.y);
        temp[3].position.setPair(s[0].position.x + 2 * Square.SIZE, s[0].position.y);
        this.dir = 1;
    }

    @Override
    public void rotate2() {
        // □
        // □
        // □
        // □


        temp[1].position.setPair(s[0].position.x, s[0].position.y - Square.SIZE);
        temp[2].position.setPair(s[0].position.x, s[0].position.y + Square.SIZE);
        temp[3].position.setPair(s[0].position.x, s[0].position.y + 2 * Square.SIZE);
        this.dir = 2;
    }

    @Override
    public void rotate3() {
        rotate1();
        this.dir = 3;
    }

    @Override
    public void rotate4() {
        rotate2();
        this.dir = 4;
    }
}