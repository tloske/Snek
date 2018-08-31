package entw.app.android.snek;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;

public class SnakeView extends android.support.v7.widget.AppCompatImageView {

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private int mColorBackground;
    private int mColorSnake;
    private int mColorFood;
    private int mColorObstacle;
    private int mRatio;

    public SnakeView(Context context, int ratio) {
        super(context);

        mRatio = ratio;

        mPaint = new Paint();

        mBitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        mColorBackground = ResourcesCompat.getColor(getResources(), R.color.colorBackGround, null);
        mColorSnake = ResourcesCompat.getColor(getResources(), R.color.colorSnake, null);
        mColorFood = ResourcesCompat.getColor(getResources(), R.color.colorFood, null);
        mColorObstacle = ResourcesCompat.getColor(getResources(), R.color.colorObstacle, null);
        mPaint.setColor(mColorBackground);
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Draws the background of the game
     */
    public void drawBackGround() {
        mBitmap = Bitmap.createBitmap((mRatio * 2 + 1) * 4, 21 * 4, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mColorBackground);
    }

    /**
     * Draws the walls at the edge of the area
     */
    public void drawWalls() {
        //Draws a Grid
        int bitWidth = (mRatio * 2 + 1) * 4 - 1;
        int bitHeight = 21 * 4 - 1;
        mPaint.setColor(mColorObstacle);

        mCanvas.drawLine(0, 0, bitWidth, 0, mPaint);
        mCanvas.drawLine(0, 0, 0, bitHeight, mPaint);
        mCanvas.drawLine(0, bitHeight, bitWidth, bitHeight, mPaint);
        mCanvas.drawLine(bitWidth, 0, bitWidth, bitHeight + 1, mPaint);


        //        for (int i = 4; i < bitWidth; i += 4) {
//            mCanvas.drawLine(i, 0, i, bitHeight, mPaint);
//        }
//        for (int j = 4; j < bitHeight; j += 4) {
//            mCanvas.drawLine(0, j, bitWidth, j, mPaint);
//        }
    }

    /**
     * Draws a square onto the bitmap.
     *
     * @param coords the coordinates where to draw the square
     * @param type   the type of square to be drawn (decides the color of the square)
     */
    public void drawSquare(int[] coords, int type) {

        switch (type) {
            case 0:
                mPaint.setColor(mColorSnake);
                break;
            case 1:
                mPaint.setColor(mColorFood);
                break;
            case 2:
                mPaint.setColor(mColorObstacle);
                break;
        }

        int squareSize = 4;
        int rectX = coords[0] * 4 + squareSize / 2;
        int rectY = coords[1] * 4 + squareSize / 2;
        int left = rectX - squareSize / 2;
        int top = rectY - squareSize / 2;
        int right = rectX + squareSize / 2;
        int bottom = rectY + squareSize / 2;

        Rect rect = new Rect();
        rect.set(left, top, right, bottom);
        mCanvas.drawRect(rect, mPaint);
    }

    /**
     * Creates a scaled bitmap using the width and height of the SnakeView and invalidates the View
     * so that the onDraw method will be called
     */
    public void draw() {

        int height = getHeight();
        int width = getWidth();
        Log.d("DEBUG", "draw: height:" + height + " width: " + width);
        if (!(height == 0 || width == 0)) {
            mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, false);
            invalidate();
        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBitmap.prepareToDraw();
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }
}
