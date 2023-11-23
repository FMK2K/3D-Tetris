import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class World {
    static int height;
    static int width;
    final Pair START_POSITION;
    static final int DELETE_BLOCK_COUNT_NUM = 20;

    int count_horizontal_blocks = 0;
    Shape currentShape;
    Shape nextShape;
    int NUM_SHAPES = 4;
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

            //Add falling block to pile of blocks
            stasisBlock.addAll(Arrays.asList(currentShape.s));

            //Make Next Shape
            currentShape = nextShape;
            currentShape.setPosition((int)START_POSITION.x, (int)START_POSITION.y);
            nextShape = pickShape();
            removeBlocks();
        }
        else {
            currentShape.update(time);
        }
    }

    private void removeBlocks(){

        //COUNT BLOCKS IN ROW
        for(Square s: stasisBlock){
            if(s.position.y + Square.SIZE == World.height){
                count_horizontal_blocks++;
            }
        }

        //REMOVE FILLED ROW AND SHIFT REST DOWN BY 1
        if(count_horizontal_blocks >= DELETE_BLOCK_COUNT_NUM){

            stasisBlock.removeIf(square -> square.position.y + Square.SIZE == World.height);

            for(Square s: stasisBlock){
                if(s.position.y + Square.SIZE != World.height){
                    s.position.y += Square.SIZE;
                    System.out.println("BLOCKS REMOVED");
                }
            }

        }
        count_horizontal_blocks = 0;
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