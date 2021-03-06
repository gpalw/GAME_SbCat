package lw.com.sbcat;


public class Dot {
    //记录当前点的XY
    int x,y;
    //状态
    int status;

    public static  final int STATUS_ON=1;  //路障
    public static  final int STATUS_OFF=0;  //可以走的
    public static  final int STATUS_IN=9;   //猫的位置

    public Dot(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        status=STATUS_OFF;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }
}
