package entw.app.android.snek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    // Not used in the game
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Save save = SnakeActivity.loadSave(getFilesDir(), "snake.save");
            ((TextView) findViewById(R.id.highscore)).setText("HighScore: " + save.getHighScore());

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        MobileAds.initialize(this, "ca-app-pub-3663743824897691~7942436331");

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Save save = SnakeActivity.loadSave(getFilesDir(), "snake.save");
            ((TextView) findViewById(R.id.highscore)).setText("HighScore: " + save.getHighScore());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Starts the Game where the Game is displayed using a Canvas
     *
     * @param view
     */
    public void startGameCanvas(View view) {
        SnakeActivity.setOpenGL(false);
        Intent intent = new Intent(this, SnakeActivity.class);
        startActivity(intent);
    }

    /**
     *  Starts the game where the game is displayed using OpenGL
     * @param view
     */
    public void startGameOpenGL(View view) {
        SnakeActivity.setOpenGL(true);
        Intent intent = new Intent(this, SnakeActivity.class);
        startActivity(intent);
    }

    /**
     *  Opens the options menu
     * @param view
     */
    public void optionsButton(View view) {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    /**
     * Not used in the Game
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
