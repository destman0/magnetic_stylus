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

    public static final int LINE = 1;
    public static final int RECTANGLE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;

    public static final float TOUCH_TOLERANCE = 4;
    public static final float TOUCH_STROKE_WIDTH = 5;

    public int mCurrentShape;

    protected Path mPath;
    protected Paint mPaint;
    protected Paint mPaintFinal;
    protected Bitmap mBitmap;
    protected Canvas mCanvas;

    /**
     * Indicates if you are drawing
     */
    protected boolean isDrawing = false;

    /**
     * Indicates if the drawing is ended
     */
    protected boolean isDrawingEnded = false;


    protected float mStartX;
    protected float mStartY;

    protected float xPos;
    protected float yPos;


    //////
    public float screenX = 200;
    public float screenY = 200;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        //Testing
        mCanvas.drawCircle(screenX, screenY, 10, mPaintFinal);
        //

        if (isDrawing){
            switch (mCurrentShape) {
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
        mPath = new Path();

        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);


        mPaintFinal = new Paint(Paint.DITHER_FLAG);
        mPaintFinal.setAntiAlias(true);
        mPaintFinal.setDither(true);
        mPaintFinal.setColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));
        mPaintFinal.setStyle(Paint.Style.STROKE);
        mPaintFinal.setStrokeJoin(Paint.Join.ROUND);
        mPaintFinal.setStrokeCap(Paint.Cap.ROUND);
        mPaintFinal.setStrokeWidth(TOUCH_STROKE_WIDTH);
    }

    protected void reset() {
        mPath = new Path();
        countTouch=0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPos = event.getX();
        yPos = event.getY();
        switch (mCurrentShape) {
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

    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------------------------------------------


    private void onTouchEventSmoothLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = xPos;
                mStartY = yPos;

                mPath.reset();
                mPath.moveTo(xPos, yPos);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = Math.abs(xPos - mStartX);
                float dy = Math.abs(yPos - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mStartX, mStartY, (xPos + mStartX) / 2, (yPos + mStartY) / 2);
                    mStartX = xPos;
                    mStartY = yPos;
                }
                mCanvas.drawPath(mPath, mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mPath.lineTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaintFinal);
                mPath.reset();
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------

    int countTouch =0;
    float basexTriangle =0;
    float baseyTriangle =0;

    private void onDrawTriangle(Canvas canvas){

        if (countTouch<3){
            canvas.drawLine(mStartX,mStartY,xPos,yPos,mPaint);
        }else if (countTouch==3){
            canvas.drawLine(xPos,yPos,mStartX,mStartY,mPaint);
            canvas.drawLine(xPos,yPos,basexTriangle,baseyTriangle,mPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch==1){
                    isDrawing = true;
                    mStartX = xPos;
                    mStartY = yPos;
                } else if (countTouch==3){
                    isDrawing = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                if (countTouch<3){
                    basexTriangle=xPos;
                    baseyTriangle=yPos;
                    mCanvas.drawLine(mStartX,mStartY,xPos,yPos,mPaintFinal);
                } else if (countTouch>=3){
                    mCanvas.drawLine(xPos,yPos,mStartX,mStartY,mPaintFinal);
                    mCanvas.drawLine(xPos,yPos,basexTriangle,baseyTriangle,mPaintFinal);
                    countTouch =0;
                }
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, xPos, yPos), mPaint);
    }

    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = xPos;
                mStartY = yPos;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,xPos,yPos), mPaintFinal);
                invalidate();
                break;
        }
    }

    /**
     *
     * @return
     */
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }

    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas,mPaint);
    }

    private void onTouchEventRectangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = xPos;
                mStartY = yPos;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawRectangle(mCanvas,mPaintFinal);
                invalidate();
                break;
        }
        ;
    }

    private void drawRectangle(Canvas canvas,Paint paint){
        float right = mStartX > xPos ? mStartX : xPos;
        float left = mStartX > xPos ? xPos : mStartX;
        float bottom = mStartY > yPos ? mStartY : yPos;
        float top = mStartY > yPos ? yPos : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

}