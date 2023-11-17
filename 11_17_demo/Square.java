import java.awt.*;
public class Square{
    Color c;
    Pair position;
    static int VELOCITY;
    static final int SIZE = 20;
    public Square(Color c){
        this.c = c;
        this.position = new Pair(0,0);
        VELOCITY = Square.SIZE;
    }

    public void draw(Graphics g){
        /*g.setColor(c);
        g.fillRect((int)position.x+1,(int)position.y+1,SIZE-1,SIZE-1);*/

        int centerX = (int) position.x;
        int centerY = (int) position.y;
        int depth = SIZE/2; // Assuming depth is the same as the radius for simplicity

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
        g.setColor(new Color(180, 180, 180)); // Darker shade for the back
        g.fillPolygon(new int[]{topBackLeft.x, topBackRight.x, bottomBackRight.x, bottomBackLeft.x},
                new int[]{topBackLeft.y, topBackRight.y, bottomBackRight.y, bottomBackLeft.y}, 4);

        // Draw the bottom face (shaded differently)
        g.setColor(new Color(150, 150, 150));
        g.fillPolygon(new int[]{bottomBackLeft.x, bottomBackRight.x, bottomFrontRight.x, bottomFrontLeft.x},
                new int[]{bottomBackLeft.y, bottomBackRight.y, bottomFrontRight.y, bottomFrontLeft.y}, 4);

        // Draw the side face
        g.setColor(new Color(200, 200, 200)); // Lighter shade for the side
        g.fillPolygon(new int[]{topBackRight.x, topFrontRight.x, bottomFrontRight.x, bottomBackRight.x},
                new int[]{topBackRight.y, topFrontRight.y, bottomFrontRight.y, bottomBackRight.y}, 4);

        // Draw the front face
        g.setColor(c); // Front face with the original color
        g.fillPolygon(new int[]{topFrontLeft.x, topFrontRight.x, bottomFrontRight.x, bottomFrontLeft.x},
                new int[]{topFrontLeft.y, topFrontRight.y, bottomFrontRight.y, bottomFrontLeft.y}, 4);

        // Draw edges for clarity
        g.setColor(Color.BLACK);
        g.drawLine(topFrontLeft.x, topFrontLeft.y, topFrontRight.x, topFrontRight.y); // Top edge
        g.drawLine(topFrontLeft.x, topFrontLeft.y, bottomFrontLeft.x, bottomFrontLeft.y); // Left edge

        g.drawLine(bottomFrontLeft.x, bottomFrontLeft.y, bottomFrontRight.x, bottomFrontRight.y); // Bottom edge
        g.drawLine(topFrontRight.x, topFrontRight.y, bottomFrontRight.x, bottomFrontRight.y); // Right edge

        // Draw the lines connecting the front and back faces
        g.drawLine(topFrontLeft.x, topFrontLeft.y, topBackLeft.x, topBackLeft.y);
        g.drawLine(topFrontRight.x, topFrontRight.y, topBackRight.x, topBackRight.y);
        g.drawLine(bottomFrontLeft.x, bottomFrontLeft.y, bottomBackLeft.x, bottomBackLeft.y);

        //graphics2D.drawLine(bottomFrontRight.x, bottomFrontRight.y, bottomBackRight.x, bottomBackRight.y);
    }
}