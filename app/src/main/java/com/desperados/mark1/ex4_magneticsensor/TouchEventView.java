package com.desperados.mark1.ex4_magneticsensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mark1 on 30/05/16.
 */
public class TouchEventView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();

    public TouchEventView(Context ctxt, AttributeSet attrs){
        super(ctxt, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        this.setBackgroundColor(Color.BLACK);
    }
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawPath(path, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xPos, yPos);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                break; //Do nothing, finger lifted
            default:
                return false;
        }
        //schedule a repaint
        invalidate();
        return true;
    }
}
