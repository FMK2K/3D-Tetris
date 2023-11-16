import java.awt.*;
public class Shape extends Rectangle{
    Color c;
    Pair position;
    static Pair VELOCITY;
    static final int SIZE = 30;
    public Shape(Color c){
        this.c = c;
        this.position = new Pair(0,0);
    }

    public void draw(Graphics g){
        g.setColor(c);
        g.drawRect((int)position.x,(int)position.y,SIZE,SIZE);
    }
}

abstract class ShapeArray{
    public Shape s[] = new Shape[4];

    public void makeShape(Color c){
        s[0] = new Shape(c);
        s[1] = new Shape(c);
        s[2] = new Shape(c);
        s[3] = new Shape(c);
    }

    public abstract void setPosition(int x, int y);
    public void update(double time){
        for(int i = 0; i < s.length; i++)
            s[i].position.add(Shape.VELOCITY);
    }

    public abstract void draw(Graphics g);
}

class shape_L extends ShapeArray{

    public shape_L(){
        makeShape(Color.orange);
    }

    @Override
    public void setPosition(int x, int y) {
        //s[0] is the middle block, remains same through rotation

        // □ s[1]
        // □   s[0]
        // □ □
        // s[2] s[3]

        s[0].position.setPair(x,y);
        s[1].position.setPair(x,y+ Shape.SIZE);
        s[2].position.setPair(x,y - Shape.SIZE);
        s[3].position.setPair(x + Shape.SIZE, y + Shape.SIZE);
    }

    @Override
    public void update(double time) {
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(s[0].c);
        for(int i = 0; i < s.length; i++)
            g.fillRect((int)s[i].position.x,(int)s[i].position.y,Shape.SIZE,Shape.SIZE);
    }
}