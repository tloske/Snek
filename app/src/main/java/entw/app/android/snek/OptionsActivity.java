package entw.app.android.snek;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;

public class OptionsActivity extends AppCompatActivity {

    CheckBox cbWalls;
    TextView tvObstacles;
    TextView tvSpeed;
    CheckBox cbLight;
    private boolean walls;
    private boolean light;
    private int obstacles;
    private int speed;
    private int color;
    private Save save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setActionBar(myToolbar);

        // Loads all the Options from the save file
        try {
            save = SnakeActivity.loadSave(getFilesDir(), "snake.save");
            if (save == null) {
                save = new Save();
            }
            walls = save.getWalls();
            obstacles = save.getObstacleCount();
            speed = ((100 - save.getSpeed()) / 10) + 1;
            color = save.getColor();
            light = save.getLight();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        /*
        Sets all the options in the menu using the data from the save file
         */
        cbWalls = findViewById(R.id.walls_checkbox);
        cbWalls.setChecked(walls);

        cbLight = findViewById(R.id.light_checkbox);
        cbLight.setChecked(light);

        tvObstacles = findViewById(R.id.obstacles_text_view);
        tvObstacles.setText("Obstacles: " + obstacles);

        tvSpeed = findViewById(R.id.speed_text_view);
        tvSpeed.setText("Speed: " + speed);

        switch (color) {
            case R.array.colorScheme1:
                ((RadioButton) findViewById(R.id.color_scheme_1)).setChecked(true);
                break;
            case R.array.colorScheme2:
                ((RadioButton) findViewById(R.id.color_scheme_2)).setChecked(true);
                break;
            case R.array.colorScheme3:
                ((RadioButton) findViewById(R.id.color_scheme_3)).setChecked(true);
                break;
        }
    }

    /**
     * Increases the obstacles count by 1
     *
     * @param view
     */
    public void increaseObstacles(View view) {
        obstacles++;
        tvObstacles.setText("Obstacles: " + obstacles);
    }

    /**
     * Decreases the obstacle count by 1
     * @param view
     */
    public void decreaseObstacles(View view) {
        if (obstacles > 0) {
            obstacles--;
            tvObstacles.setText("Obstacles: " + obstacles);
        }
    }

    /**
     * Increases the speed of the snake by 1 which decreases the gameloops delay by 10
     * Speed can't by higher than 10
     * @param view
     */
    public void increaseSpeed(View view) {
        if (speed < 10) {
            speed++;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    /**
     * Decreases the speed of the snake by 1 which increases the gameloops delay by 10
     * Speed cant by less than 1
     * @param view
     */
    public void decreaseSpeed(View view) {
        if (speed > 1) {
            speed--;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    /**
     * Applys the changes an saves the options
     * @param view
     */
    public void apply(View view) {
        save.setColor(color);
        save.setObstacleCount(obstacles);
        save.setSpeed(100 - (speed - 1) * 10);
        save.setWalls(cbWalls.isChecked());
        save.setLight(cbLight.isChecked());
        try {
            SnakeActivity.saveGame(save, getFilesDir(), "snake.save");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the previous screen
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * Sets the color scheme depending on which radio button has been checked
     * @param view
     */
    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.color_scheme_1:
                if (checked)
                    color = R.array.colorScheme1;
                break;
            case R.id.color_scheme_2:
                if (checked)
                    color = R.array.colorScheme2;
                break;
            case R.id.color_scheme_3:
                if (checked)
                    color = R.array.colorScheme3;
                break;
        }
    }

    /**
     * Creates a new savegame which contain default settings and loads these settings
     * @param view
     */
    public void defaultSettings(View view) {
        int score = save.getHighScore();
        save = new Save();
        save.setHighScore(score);

        walls = save.getWalls();
        obstacles = save.getObstacleCount();
        speed = ((100 - save.getSpeed()) / 10) + 1;
        color = save.getColor();
        light = save.getLight();

        cbWalls.setChecked(walls);

        cbLight.setChecked(light);

        tvObstacles.setText("Obstacles: " + obstacles);

        tvSpeed.setText("Speed: " + speed);

        switch (color) {
            case R.array.colorScheme1:
                ((RadioButton) findViewById(R.id.color_scheme_1)).setChecked(true);
                break;
            case R.array.colorScheme2:
                ((RadioButton) findViewById(R.id.color_scheme_2)).setChecked(true);
                break;
            case R.array.colorScheme3:
                ((RadioButton) findViewById(R.id.color_scheme_3)).setChecked(true);
                break;
        }
    }
}
