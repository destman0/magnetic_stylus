package com.desperados.mark1.ex4_magneticsensor;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.View;

/*This code is based on code that can be found at: http://gmariotti.blogspot.de/search?q=shapes+finger*/

/**
 * Created by mark1 on 30/05/16.
 */

public class TouchEventView extends View {

    public static final int DEBUG = 0;
    public static final int RECTANGLE = 3;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;

    public static final float TOUCH_TOLERANCE = 4;
    public static final float TOUCH_STROKE_WIDTH = 5;

    public int activeShape;

    protected Path pathToDraw;
    protected Paint initialPaint;
    protected Paint endingPaint;
    protected Bitmap bitmapImage;
    protected Canvas drawingCanvas;

    /* Check for drawing */
    protected boolean drawingActive = false;
    

    protected float beginX;
    protected float beginY;

    protected float xPos;
    protected float yPos;


    //////
    //public int debug = 1;
    public float screenX = 0;
    public float screenY = 0;
    /////


    public TouchEventView(Context context) {
        super(context);
        init();
    }

    public TouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int width, int height, int prevW, int prevH) {
        super.onSizeChanged(width, height, prevW, prevH);
        bitmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(bitmapImage);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmapImage, 0, 0, initialPaint);

        //Testing
        //if (debug == 1)
        if(activeShape==DEBUG)
            drawingCanvas.drawCircle(screenX, screenY, 10, endingPaint);
        //

        if (drawingActive){
            switch (activeShape) {
                case RECTANGLE:
                    onDrawRectangle(canvas);
                    break;
                case CIRCLE:
                    onDrawCircle(canvas);
                    break;
                case TRIANGLE:
                    onDrawTriangle(canvas);
                    break;
            }
        }
    }




    protected void init() {
        pathToDraw = new Path();

        initialPaint = new Paint(Paint.DITHER_FLAG);
        initialPaint.setAntiAlias(true);
        initialPaint.setDither(true);
        initialPaint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
        initialPaint.setStyle(Paint.Style.STROKE);
        initialPaint.setStrokeJoin(Paint.Join.ROUND);
        initialPaint.setStrokeCap(Paint.Cap.ROUND);
        initialPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);


        endingPaint = new Paint(Paint.DITHER_FLAG);
        endingPaint.setAntiAlias(true);
        endingPaint.setDither(true);
        endingPaint.setColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));
        endingPaint.setStyle(Paint.Style.STROKE);
        endingPaint.setStrokeJoin(Paint.Join.ROUND);
        endingPaint.setStrokeCap(Paint.Cap.ROUND);
        endingPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
    }

    protected void reset() {
        pathToDraw = new Path();
        countTouch=0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        xPos = screenX;
        yPos = screenY;
        switch (activeShape) {
            case SMOOTHLINE:
                onTouchEventSmoothLine(event);
                break;
            case RECTANGLE:
                onTouchEventRectangle(event);
                break;
            case CIRCLE:
                onTouchEventCircle(event);
                break;
            case TRIANGLE:
                onTouchEventTriangle(event);
                break;
        }
        return true;
    }

    // Drawable line

    private void onTouchEventSmoothLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingActive = true;
                beginX = xPos;
                beginY = yPos;

                pathToDraw.reset();
                pathToDraw.moveTo(xPos, yPos);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                float distanceX = Math.abs(xPos - beginX);
                float distanceY = Math.abs(yPos - beginY);
                if (distanceX >= TOUCH_TOLERANCE || distanceY >= TOUCH_TOLERANCE) {
                    pathToDraw.quadTo(beginX, beginY, (xPos + beginX) / 2, (yPos + beginY) / 2);
                    beginX = xPos;
                    beginY = yPos;
                }
                drawingCanvas.drawPath(pathToDraw, initialPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawingActive = false;
                pathToDraw.lineTo(beginX, beginY);
                drawingCanvas.drawPath(pathToDraw, endingPaint);
                pathToDraw.reset();
                invalidate();
                break;
        }
    }

    // Two stroke triangle

    int countTouch =0;
    float basexTriangle =0;
    float baseyTriangle =0;

    private void onDrawTriangle(Canvas canvas){

        if (countTouch<3){
            canvas.drawLine(beginX,beginY,xPos,yPos,initialPaint);
        }else if (countTouch==3){
            canvas.drawLine(xPos,yPos,beginX,beginY,initialPaint);
            canvas.drawLine(xPos,yPos,basexTriangle,baseyTriangle,initialPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch==1){
                    drawingActive = true;
                    beginX = xPos;
                    beginY = yPos;
                } else if (countTouch==3){
                    drawingActive = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                drawingActive = false;
                if (countTouch<3){
                    basexTriangle=xPos;
                    baseyTriangle=yPos;
                    drawingCanvas.drawLine(beginX,beginY,xPos,yPos,endingPaint);
                } else if (countTouch>=3){
                    drawingCanvas.drawLine(xPos,yPos,beginX,beginY,endingPaint);
                    drawingCanvas.drawLine(xPos,yPos,basexTriangle,baseyTriangle,endingPaint);
                    countTouch =0;
                }
                invalidate();
                break;
        }
    }

    // Midpoint drawable circle

    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(beginX, beginY, calculateRadius(beginX, beginY, xPos, yPos), initialPaint);
    }

    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingActive = true;
                beginX = xPos;
                beginY = yPos;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawingActive = false;
                drawingCanvas.drawCircle(beginX, beginY, calculateRadius(beginX,beginY,xPos,yPos), endingPaint);
                invalidate();
                break;
        }
    }

    // @return

    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }

    // Rectangle

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas,initialPaint);
    }

    private void onTouchEventRectangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingActive = true;
                beginX = xPos;
                beginY = yPos;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawingActive = false;
                drawRectangle(drawingCanvas,endingPaint);
                invalidate();
                break;
        }
        ;
    }

    private void drawRectangle(Canvas canvas,Paint paint){
        float right = beginX > xPos ? beginX : xPos;
        float left = beginX > xPos ? xPos : beginX;
        float bottom = beginY > yPos ? beginY : yPos;
        float top = beginY > yPos ? yPos : beginY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

}