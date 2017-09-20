package com.example.harsh.mypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import static com.example.harsh.mypaint.MainActivity.paint;

/**
 * Created by harsh on 9/6/17.
 */


public class paintView extends View {

    public static Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint circlePaint;
    private Path circlePath;
    public paintView(Context c) {
        super(c);
        path = new Path();
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(8f);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //ARGB_8888 = Each pixel is stored on 4 bytes.

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(MainActivity.backgroundColor);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( bitmap, 0, 0, paint);
        canvas.drawPath( path,  paint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float X, Y;
    float TOUCH_TOLERANCE = 0.0f;

    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        X = x;
        Y = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - X);
        float dy = Math.abs(y - Y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //Add a quadratic bezier from the last point,
            //approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(X, Y, (x + X)/2, (y + Y)/2);
            X = x;
            Y = y;
            circlePath.reset();
            circlePath.addCircle(X, Y, 60, Path.Direction.CW);
        }
    }

    private void touch_up() {
        path.lineTo(X, Y);
        circlePath.reset();
        //draw the line on Canvas
        canvas.drawPath(path,  paint);
        //reset the path so that we don't redraw it
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            touch_start(x,y);
            //invalidate()==redraw on screen
            invalidate();
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE){
            touch_move(x,y);
            invalidate();
        }
        else if(event.getAction()==MotionEvent.ACTION_UP){
            touch_up();
            invalidate();
        }
        return true;
    }
}