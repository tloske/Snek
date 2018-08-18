package entw.app.android.snek;

import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SnekActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    float movement[] = new float[16];
    private GameSurface mGLView;
    private GestureDetectorCompat mDetector;
    private GameRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snek);

        mDetector = new GestureDetectorCompat(this, this);

        mRenderer = new GameRenderer();

        mGLView = new GameSurface(this, mRenderer, this);
        RelativeLayout rl = findViewById(R.id.snek_layout);
        rl.addView(mGLView, 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float sensitivity = 100;
        float diffX = e1.getX() - e2.getX();
        float diffY = e1.getY() - e2.getY();
        float rotation[] = new float[16];
        float speed = 0.01f;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > sensitivity) {
                if (diffX > 0) {
                    Log.d("DEBUG", "Swipe Left");
                    Matrix.setRotateM(rotation, 0, -90.0f, 0.0f, 0.0f, 1.0f);
                    movement = new float[]{-speed, 0.0f, 0.0f};
                } else if (diffX < 0) {
                    Log.d("DEBUG", "Swipe Right");
                    Matrix.setRotateM(rotation, 0, 90.0f, 0.0f, 0.0f, 1.0f);
                    movement = new float[]{speed, 0.0f, 0.0f};
                }
            }
        } else if (Math.abs(diffY) > sensitivity) {
            if (diffY > 0) {
                Log.d("DEBUG", "Swipe Up");
                Matrix.setRotateM(rotation, 0, 0.0f, 0.0f, 0.0f, 1.0f);
                movement = new float[]{0.0f, speed, 0.0f};
            } else if (diffY < 0) {
                Log.d("DEBUG", "Swipe Down");
                Matrix.setRotateM(rotation, 0, 180.0f, 0.0f, 0.0f, 1.0f);
                movement = new float[]{0.0f, -speed, 0.0f};
            }
        }

        mRenderer.setRotation(rotation);
        mRenderer.setTranslation(movement);
        return true;
    }

    public void resume(View view) {
        mGLView.resume();
    }

    public void pause(View view) {
        LinearLayout l = findViewById(R.id.pause);
        l.setVisibility(View.VISIBLE);
        mGLView.pause();
    }

    public void gameOver() {
        LinearLayout l = findViewById(R.id.game_over);
        l.setVisibility(View.VISIBLE);
        mGLView.pause();
    }

    public void quit(View view) {

    }
}
