import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class World {
    static int height;
    static int width;
    final Pair START_POSITION;
    Shape currentShape;
    Shape nextShape;
    int NUM_SHAPES = 4;
    final int floor = height - 200;
    static ArrayList<Square> stasisBlock = new ArrayList<>();
    public World(int initWidth, int initHeight) {
        width = initWidth;
        height = initHeight;
        START_POSITION = new Pair(width/2.0-Square.SIZE,5*Square.SIZE);
        currentShape = pickShape();
        currentShape.setPosition((int)START_POSITION.x, (int)START_POSITION.y);
        nextShape = pickShape();
    }

    public void drawWorld(Graphics g) {
        currentShape.draw(g);
        for(int i = 0; i < stasisBlock.size(); i++){
            stasisBlock.get(i).draw(g);
        }
    }

    public void updateWorld(double time) {
        if(!currentShape.isFalling){
            stasisBlock.add(currentShape.s[0]);
            stasisBlock.add(currentShape.s[1]);
            stasisBlock.add(currentShape.s[2]);
            stasisBlock.add(currentShape.s[3]);

            currentShape = nextShape;
            currentShape.setPosition((int)START_POSITION.x, (int)START_POSITION.y);
            nextShape = pickShape();
        }
        else {
            currentShape.update(time);
        }
    }

    private Shape pickShape(){
        Shape s = new Shape();
        int p = (int)(Math.random()*NUM_SHAPES);
        switch (p){
            case 0: s = new shape_L(); break;
            case 1: s = new shape_O(); break;
            case 2: s = new shape_T(); break;
            case 3: s = new shape_I(); break;
        }
        return s;
    }
}