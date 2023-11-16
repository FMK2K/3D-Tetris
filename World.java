import java.awt.*;
import java.util.Random;

public class World {
    int height;
    int width;
    Shape s;
    final Pair START_POSITION;
    shape_L L;

    public World(int initWidth, int initHeight) {
        width = initWidth;
        height = initHeight;
        START_POSITION = new Pair(width/2.0,height/2.0);
        L = new shape_L();
        L.setPosition((int)START_POSITION.x, (int)START_POSITION.y);
    }

    public void drawWorld(Graphics g) {
        L.draw(g);
    }

    public void updateWorld(double time) {

    }
}