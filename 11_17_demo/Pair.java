public class Pair {
    public double x;
    public double y;

    public Pair(double initX, double initY){
        x = initX;
        y = initY;
    }

    public Pair add(Pair toAdd){
        return new Pair(x + toAdd.x, y + toAdd.y);
    }


    public Pair times(double val){
        return new Pair(x * val, y * val);
    }

    public Pair times(double val, double val2){
        return new Pair(x * val, y * val2);
    }


    public void flipX(){
        x = -x;
    }

    public void flipY(){
        y = -y;
    }

    public void setPair(double X, double Y){
        this.x = X;
        this.y = Y;
    }

}
