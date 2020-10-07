package com.example.flappybird;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.os.Handler;

import java.util.Random;


public class GameView extends View {

    //this is custom View class

    Handler handler; //handler is required to schedule a runnable after some delay
    Runnable runnable;
    final int UPDATE_MILLIS = 30;
    Bitmap background;
    Bitmap toptube, bottomtube;
    Display display;
    Point point;
    int dWidth, dHeight; //ngang, rộng thiết bị tương ứng
    Rect rect;

    Bitmap[] birds;

    //tạo biến int theo dõi hình ảnh con chim
    int birdFrame = 0;
    int velocity = 0, gravity = 3;   //  vận tốc và trọng lượng
    int birdX, birdY;    // theo dõi vị trí chim
    boolean gameState = false;
    int gap = 400;  //khoảng cách giữa ống trên và ống dưới
    int minTubeOffset, maxTubeOffset;
    int numberOfTube = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTube];
    int[] topTubeY = new int[numberOfTube];
    Random random;
    int tubeVelocity = 8;

    public GameView(Context context) {
        super(context);
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // call onDraw
            }
        };
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_day);
        toptube = BitmapFactory.decodeResource(getResources(), R.drawable.toptube);
        bottomtube = BitmapFactory.decodeResource(getResources(), R.drawable.bottomtube);

        display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0, 0, dWidth, dHeight);
        birds = new Bitmap[3];
        birds[0] = BitmapFactory.decodeResource(getResources(), R.drawable.yellowbird_downflap);
        birds[1] = BitmapFactory.decodeResource(getResources(), R.drawable.yellowbird_midflap);
        birds[2] = BitmapFactory.decodeResource(getResources(), R.drawable.yellowbird_upflap);
        birdX = dWidth / 2 - birds[0].getWidth() / 2;
        birdY = dHeight / 2 - birds[0].getHeight() / 2;
        distanceBetweenTubes = dWidth * 3 / 4;
        minTubeOffset = gap / 2;
        maxTubeOffset = dHeight - minTubeOffset - gap;
        random = new Random();
        for (int i = 0; i < numberOfTube; i++) {
            tubeX[i] = dWidth + i*distanceBetweenTubes;
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);

            }
        }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //we will draw our view inside onDraw
        //draw the background on canvas
        //canvas.drawBitmap(background,0,0,null);
        canvas.drawBitmap(background,null,rect,null);
        if(birdFrame == 0){
            birdFrame = 1;
        }else {
            birdFrame = 0;
        }
        if(gameState) {
            if (birdY < dHeight - birds[0].getHeight() || velocity < 0) {  //chim phải ở trong màn hình
                velocity += gravity;    //vận tốc rơi càng ngày càng nhanh hơn khi giá trị vận tốc tăng theo trọng lực mỗi lần
                birdY += velocity;
            }
            for(int i =0; i<numberOfTube; i++){
                tubeX[i] -= tubeVelocity;
                if(tubeX[i]< -toptube.getHeight()) {
                    tubeX[i] += numberOfTube*distanceBetweenTubes;
                    topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);

                }

                canvas.drawBitmap(toptube,tubeX[i],topTubeY[i] - toptube.getHeight(),null);
                canvas.drawBitmap(bottomtube,tubeX[i],topTubeY[i] + gap,null);

            }
        }
        //con chim ở giữa
        //3 hoạt cảnh con chim cùng một chiều
        canvas.drawBitmap(birds[birdFrame],birdX,birdY,null);
        handler.postDelayed(runnable,UPDATE_MILLIS);
    }

    //event touch

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if(action==MotionEvent.ACTION_DOWN){    //khi phát hiện chạm vào màn hình
            velocity = -30;     //di chuyển 30 đơn vị
            gameState = true;

        }

        return true;
    }
}
