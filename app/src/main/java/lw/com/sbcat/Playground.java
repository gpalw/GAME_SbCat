package lw.com.sbcat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by lenovo on 2015/9/14.
 */
public class Playground extends SurfaceView implements View.OnTouchListener{

    private int WIDTH=40; //宽度
    private int WALK=0; //宽度
    private static final int COL=10; //行
    private static final int ROW=10; //列

    private static final int BLOCKS=10; //默认添加的路障数

    //猫的初始位置
    private static int CAT_COL=4; //行
    private static int CAT_ROW=5; //列
    //创建二维数组
    private Dot matrix[][];

    //设置猫
    private Dot cat;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix=new Dot[COL][ROW];
        for (int i=0;i<COL;i++){
            for (int j=0;j<ROW;j++){
                matrix[i][j]=new Dot(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private void redraw() {

        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);
        //创建画笔
        Paint paint=new Paint();
        paint.setFlags(paint.ANTI_ALIAS_FLAG);
        for (int i=0;i<COL;i++) {
            int offset=0;
            if (i%2!=0){
                offset=WIDTH/2;
            }
            for (int j = 0; j < ROW; j++) {
               Dot one= getDot(j,i);
                switch (one.getStatus()){
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);

                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                }
                c.drawOval(new RectF(one.getX() * WIDTH + offset, one.getY() * WIDTH,
                        (one.getX() + 1) * WIDTH + offset, (one.getY() + 1) * WIDTH), paint);


            }
        }


        getHolder().unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            WIDTH=width/(COL+1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void initGame(){
        WALK=0;
        for (int i=0;i<COL;i++){
            for (int j=0;j<ROW;j++){
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat=new Dot(CAT_COL,CAT_ROW);
        getDot(CAT_COL,CAT_ROW).setStatus(Dot.STATUS_IN);
        for (int i=0;i<BLOCKS;){
            int x= (int) ((Math.random()*1000)%ROW);
            int y= (int) ((Math.random()*1000)%COL);
            if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    private Dot getDot(int x,int y){
        return matrix[y][x];
    }

    //判断点是否为边界
    private boolean isAtEdge(Dot d){
        if (d.getX()*d.getY()==0||d.getX()+1==COL||d.getY()+1==ROW) {return true;}
        return false;
    }

    private Dot getNrighbour(Dot one,int dir){
        switch (dir){
            case 1:
                return getDot(one.getX()-1,one.getY());
            case 2:
                if (one.getY()%2==0){
                    return  getDot(one.getX()-1,one.getY()-1);
                }else {
                    return  getDot(one.getX(),one.getY()-1);
                }

            case 3:
                if (one.getY()%2==0){
                    return  getDot(one.getX(),one.getY()-1);
                }else {
                    return getDot(one.getX()+1,one.getY()-1);
                }

            case 4:
                return getDot(one.getX()+1,one.getY());

            case 5:
                if (one.getY()%2==0){
                    return  getDot(one.getX(),one.getY()+1);
                }else {
                    return getDot(one.getX()+1,one.getY()+1);
                }

            case 6:
                if (one.getY()%2==0){
                    return getDot(one.getX()-1,one.getY()+1);

                }else {
                    return getDot(one.getX(),one.getY()+1);
                }
        }
            return null;
    }
    //参考点的方向DIR的可能路线
    private int getDistance(Dot one,int dir){
        int distance=0;
        if (isAtEdge(one)){
            return 1;
        }
        Dot ori=one,next;
        while (true){
            next=getNrighbour(ori,dir);
            if (next.getStatus()==Dot.STATUS_ON){
                return distance*(-1);
            }
            if (isAtEdge(next)){
                distance++;
                return distance;
            }
            distance++;
            ori=next;
        }
    }
    //猫移动
    private void MoveTo(Dot one){
        one.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(one.getX(),one.getY());
    }
    private void move() {
        if (isAtEdge(cat)){
            lose();return;
        }
       //将周围6个点的信息存到记录器
        Vector<Dot> avaliable=new Vector<>();
        //可直达屏幕的记录器
        Vector<Dot> postitive=new Vector<>();
        //方向的记录器
        HashMap<Dot,Integer> al=new HashMap<Dot,Integer>();
        for (int i=1;i<7;i++){
           Dot n= getNrighbour(cat,i);
            if (n.getStatus()==Dot.STATUS_OFF){
                avaliable.add(n);
                al.put(n, i);
                if (getDistance(n,i)>0){
                    postitive.add(n);
                }
            }
        }

        if (avaliable.size()==0){
            win();
        }else if (avaliable.size()==1){
            MoveTo(avaliable.get(0));
        }else {
            Dot best=null;
            if(postitive.size()!=0){//存在可以直达屏幕边缘的走向
                int min=999;
                for (int i=0;i<postitive.size();i++){
                    int a=getDistance(postitive.get(i),al.get(postitive.get(i)));
                    if (a<=min){
                        min=a;
                        best=postitive.get(i);
                    }
                }

            }else {//所有方向存在路障
                int max=0;
                for (int i=0;i<avaliable.size();i++){
                    int k=getDistance(avaliable.get(i),al.get(avaliable.get(i)));
                    if (k<=max){
                        max=k;
                        best=avaliable.get(i);
                    }
                }
            }
            MoveTo(best);
        }
    }

    private void lose() {
        Toast.makeText(getContext(), "Lost，你输了,走了"+(WALK+1)+"步", Toast.LENGTH_SHORT).show();
    }

    private void win() {
        Toast.makeText(getContext(), "恭喜！你赢了！！"+(WALK+1)+"步", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
       if (event.getAction()==MotionEvent.ACTION_UP){
          int x,y;

           y= (int) (event.getY()/WIDTH);
           if (y%2==0){
               x= (int) (event.getX()/WIDTH);
           }else {
               x= (int) ((event.getX()-WIDTH/2)/WIDTH);
           }
           if (x+1>COL||y+1>ROW){
               initGame();

           }else {
               if (getDot(x, y).getStatus()==Dot.STATUS_OFF){
               getDot(x, y).setStatus(Dot.STATUS_ON);
               move(); WALK++;
               }
           }redraw();

       }
        return true;
    }


}
