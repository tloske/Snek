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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import entw.app.android.snek.OpenGL.GameRenderer;
import entw.app.android.snek.OpenGL.GameSurface;

public class SnakeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static boolean walls = true;
    private static int obstacleCount;
    private static int delay = 100;
    private static boolean openGL = false;
    private static int mHighScore = 0;
    private static int colorID = R.array.colorScheme1;
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
    private Save save;
    private boolean light = false;

    public static void setOpenGL(boolean openGL) {
        SnakeActivity.openGL = openGL;
    }

    private int score;

    /**
     * Saves the given object
     *
     * @param object   the object to be saved
     * @param dir      the directory in which the save game should be saved
     * @param filename the filename of the save game
     * @throws IOException
     */
    public static void saveGame(Serializable object, File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(file));
        objstream.writeObject(object);
        objstream.close();
    }

    /**
     * Loads the saved object
     * @param dir   the directory where the object is saved
     * @param filename  the filename of the saved object
     * @return the save object thats been loaded
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Save loadSave(File dir, String filename) throws ClassNotFoundException, IOException {
        File file = new File(dir, filename);
        if (file.exists()) {
            ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(file));
            Object object = objstream.readObject();
            objstream.close();
            return (Save) object;
        }
        return new Save();
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

    /**
     * The onFling method detects if the user is swiping over the display and the direction in which the user swipes
     * @param e1    the event where the user first touches the display
     * @param e2    the event where the user stops touching the display
     * @param velocityX
     * @param velocityY
     * @return true if fling was detected
     */
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
     * Sets the visibility of the Pause menu so that it doesn't show up in the gameView
     *
     * @param view
     */
    public void resume(View view) {
        findViewById(R.id.pause).setVisibility(View.GONE);
        mPaused = false;
    }

    /**
     * Gets called when the user hits the Pause button.
     * Sets the visibility of the Pause menu so that it shows up in the gameView
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        // Loads the save game
        try {
            save = loadSave(getFilesDir(), "snake.save");
            mHighScore = save.getHighScore();
            walls = save.getWalls();
            obstacleCount = save.getObstacleCount();
            delay = save.getSpeed();
            light = save.getLight();
            colorID = save.getColor();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        mDetector = new GestureDetectorCompat(this, this);
        mModel = new GameModel((int) (getRatio() * 10.0f), obstacleCount, walls);

        RelativeLayout rl = findViewById(R.id.snek_layout);
        int[] colors = getResources().getIntArray(colorID);
        if (openGL) {
            mRenderer = new GameRenderer(this, obstacleCount, walls, colors, light);
            mGLView = new GameSurface(this, mRenderer);
            mGLView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            mGLView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        mGLView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                }
            });
            rl.addView(mGLView, 0); // Sets the gameView to the first position in the layout so that everything else gets drawn over it
        } else {
            mSnakeView = new SnakeView(this, (int) (getRatio() * 10.0f), colors);
            mSnakeView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mSnakeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            mSnakeView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        mSnakeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                }
            });
            rl.addView(mSnakeView, 0);  // Sets the gameView to the first position in the layout so that everything else gets drawn over it
        }

        MobileAds.initialize(this, "ca-app-pub-3663743824897691~7942436331");   // Initializes the ads

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
        }, 500, 10, TimeUnit.MILLISECONDS); // 500ms delay so that the opengl renderer doesn't crash
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
     * Gets called when the Snake hits an obstacle.
     * Pauses the Game and sets the GameOver screen to visible
     */
    public void gameOver() {
        AdView mAdView = findViewById(R.id.game_over_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.game_over).setVisibility(View.VISIBLE);

        findViewById(R.id.pause_button).setVisibility(View.GONE);
        future.cancel(true);

        if (score > mHighScore) {
            mHighScore = score;
            try {
                save.setHighScore(mHighScore);
                saveGame(save, getFilesDir(), "snake.save");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the Score in the ui
     *
     * @param score the new Score
     */
    public void updateScore(int score) {
        TextView tv = findViewById(R.id.score);
        tv.setText(String.format("Score: %d", score));
    }

    public float getRatio() {
        return (float) getResources().getDisplayMetrics().widthPixels / (float) getResources().getDisplayMetrics().heightPixels;
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
        if (score > mHighScore) {
            mHighScore = score;
            try {
                save.setHighScore(mHighScore);
                saveGame(save, getFilesDir(), "snake.save");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The loop that gets called when the game is started in canvas mode
     * Calls the draw methods in the SnakeView to draw the objects in the game
     */
    public void canvasLoop() {
        mModel.move();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mModel.checkCollision())
                    gameOver();
                if (score != mModel.getScore()) {
                    score = mModel.getScore();
                    updateScore(score);
                    if (score % 5 == 0 && delay > 10) {
                        delay -= 5;
                    }
                }
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

    /**
     * This loop gets called when the game is started in OpenGL mode
     * Updates the renderer where to draw the objects and adds new objects to the scene when the snake gets longer
     */
    public void openGLLoop() {
        mModel.move();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mModel.checkCollision())
                    gameOver();
                if (score != mModel.getScore()) {
                    score = mModel.getScore();
                    updateScore(score);
                    if (score % 5 == 0 && delay > 10) {
                        delay -= 5;
                        //Toast.makeText(getApplicationContext(), "" + delay, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                float[] pos;
                ArrayList<float[]> coords = new ArrayList<>();
                int[][] board = mModel.getBoard();
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j] == 1) {
                            pos = mModel.OpenGLCoords(new int[]{i, j});
                            mRenderer.move(pos, 1);
                        }
                        if (board[i][j] == 2) {
                            coords.add(mModel.OpenGLCoords(new int[]{i, j}));
                        }
                    }
                }
                mRenderer.move(coords, 2);
                coords = mModel.OpenGLCoords(mModel.getPrevPos());
                mRenderer.move(coords, 0);
                pos = mModel.OpenGLCoords(mModel.getPos());
                while (mModel.getSnakeSize() > mRenderer.getSnakeSize()) {
                    mRenderer.addToBody(pos);
                }
                mRenderer.move(pos, 0);
            }
        });
    }
}
