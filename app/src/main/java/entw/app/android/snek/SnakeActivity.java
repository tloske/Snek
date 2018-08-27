package entw.app.android.snek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SnakeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static boolean walls = true;
    private static int obstacleCount;
    private static int delay = 100;
    private static boolean openGL = false;
    int movement[] = new int[2];
    private GameSurface mGLView;
    private GestureDetectorCompat mDetector;
    private GameModel mModel;
    private GameRenderer mRenderer;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> future;
    private boolean mPaused = false;
    private SnakeView mSnakeView;
    private int count;

    public static boolean getWalls() {
        return SnakeActivity.walls;
    }

    public static void setWalls(boolean walls) {
        SnakeActivity.walls = walls;
    }

    public static int getDelay() {
        return SnakeActivity.delay;
    }

    public static void setDelay(int delay) {
        SnakeActivity.delay = delay;
    }

    public static int getObstacleCount() {
        return SnakeActivity.obstacleCount;
    }

    public static void setObstacleCount(int obstacles) {
        SnakeActivity.obstacleCount = obstacles;
    }

    public static void setOpenGL(boolean openGL) {
        SnakeActivity.openGL = openGL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        mDetector = new GestureDetectorCompat(this, this);
        mModel = new GameModel((int) (getRatio() * 10.0f), obstacleCount, walls);

        RelativeLayout rl = findViewById(R.id.snek_layout);

        if (openGL) {
            mRenderer = new GameRenderer();
            mGLView = new GameSurface(this, mRenderer);
            rl.addView(mGLView, 0);
        } else {
            mSnakeView = new SnakeView(this, (int) (getRatio() * 10.0f));
            mSnakeView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mSnakeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            rl.addView(mSnakeView, 0);
        }

        MobileAds.initialize(this, "ca-app-pub-3663743824897691~7942436331");

        count = 100;
        //Scheduled Executor that runs every 10ms
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (count % delay == 0) {
                    if (openGL) {
                        openGLLoop();
                    } else {
                        canvasLoop();
                    }
                    count = 0;
                }
                if (!mPaused)
                    count++;
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
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
        int direction = 1;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (!(Math.abs(movement[0]) > 0) && Math.abs(diffX) > sensitivity) {
                if (diffX > 0) {                        //Swipe Left
                    movement = new int[]{-direction, 0};
                } else if (diffX < 0) {                 //Swipe Right
                    movement = new int[]{direction, 0};
                }
            }
        } else if (!(Math.abs(movement[1]) > 0) && Math.abs(diffY) > sensitivity) {
            if (diffY > 0) {                            //Swipe Up
                if (openGL)
                    movement = new int[]{0, direction};
                else
                    movement = new int[]{0, -direction};
            } else if (diffY < 0) {                     //Swipe Down
                if (openGL)
                    movement = new int[]{0, -direction};
                else
                    movement = new int[]{0, direction};
            }
        }

        mModel.setDirection(movement);
//        mRenderer.setTranslation(movement);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openGL)
            mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (openGL)
            mGLView.onPause();
    }

    /**
     * Gets called when the user hits the resume button in the Pause Menu.
     *
     * @param view
     */
    public void resume(View view) {
        findViewById(R.id.pause).setVisibility(View.GONE);
        mPaused = false;
    }

    /**
     * Gets called when the user hits the Pause button.
     *
     * @param view
     */
    public void pause(View view) {
        AdView mAdView = findViewById(R.id.pause_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.pause).setVisibility(View.VISIBLE);

        mPaused = true;
    }

    /**
     * Gets called when the Snake hits an obstacle.
     * Pauses the Game and sets the GameOver screen to visible
     */
    public void gameOver() {
        AdView mAdView = findViewById(R.id.game_over_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.game_over).setVisibility(View.VISIBLE);

        findViewById(R.id.pause_button).setVisibility(View.GONE);
        mPaused = true;
    }

    /**
     * Gets called when ths user hits the restart button in the GameOver Screen.
     * Cancels the GameLoop calls finish on the current activity and creates a new SnakeActivity
     *
     * @param view
     */
    public void restart(View view) {
        future.cancel(false);
        finish();
        Intent intent = new Intent(this, SnakeActivity.class);
        startActivity(intent);
    }

    /**
     * Gets called when the user hits the quit button in the GameOverScreen or the PauseMenu
     * Stops GameLoop and starts a new MainActivity
     *
     * @param view
     */
    public void quit(View view) {
        future.cancel(false);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Updates the Score in the ui
     *
     * @param score
     */
    public void updateScore(int score) {
        TextView tv = findViewById(R.id.score);
        tv.setText(String.format("Score: %d", score));
    }

    public float getRatio() {
        return (float) getResources().getDisplayMetrics().widthPixels / (float) getResources().getDisplayMetrics().heightPixels;
    }

    public void canvasLoop() {

        mModel.move();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mModel.checkCollision())
                    gameOver();
                updateScore(mModel.getScore());
            }
        });

        mSnakeView.drawBackGround();
        if (walls) {
            mSnakeView.drawWalls();
        }

        int[][] board = mModel.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    mSnakeView.drawSquare(new int[]{i, j}, board[i][j]);
                }
            }
        }

        mSnakeView.drawSquare(mModel.getPos(), 0);
        ArrayDeque<int[]> prevPos = mModel.getPrevPos();
        for (int[] pos : prevPos) {
            mSnakeView.drawSquare(pos, 0);
        }
        mSnakeView.draw();

    }

    //Calls the Renderer and updates the ui
    public void openGLLoop() {

        mGLView.queueEvent(new Runnable() {

            @Override
            public void run() {
                ArrayList<Square> squares = new ArrayList<>();
                mModel.move();
                int type = 0;
                float[] coords = mModel.OpenGLCoords(mModel.getPos());
//                mRenderer.addSquare(coords[0], coords[1], type);
                squares.add(new Square(new float[]{coords[0], coords[1], 0.0f}, type));
                int[][] board = mModel.getBoard();
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        type = board[i][j];
                        if (type > 0) {
                            coords = mModel.OpenGLCoords(new int[]{i, j});
//                            mRenderer.addSquare(coords[0], coords[1], type);
                            squares.add(new Square(new float[]{coords[0], coords[1], 0.0f}, type));
                        }
                    }
                }
                mRenderer.addSquares(squares);
                mGLView.requestRender();
            }
        });


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mModel.checkCollision())
                    gameOver();
                updateScore(mModel.getScore());
            }
        });
    }
}
