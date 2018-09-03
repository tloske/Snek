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
    private boolean walls;
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

//        walls = SnakeActivity.getWalls();
//        obstacles = SnakeActivity.getObstacleCount();
//        speed = ((100 - SnakeActivity.getDelay()) / 10) + 1;
//        color = SnakeActivity.getColorID();

        try {
            save = SnakeActivity.loadSave(getFilesDir(), "snake.save");
            if (save == null) {
                save = new Save();
            }
            walls = save.getWalls();
            obstacles = save.getObstacleCount();
            speed = ((100 - save.getSpeed()) / 10) + 1;
            color = save.getColor();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        cbWalls = findViewById(R.id.walls_checkbox);
        cbWalls.setChecked(walls);

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
        }
    }

    public void increaseObstacles(View view) {
        obstacles++;
        tvObstacles.setText("Obstacles: " + obstacles);
    }

    public void decreaseObstacles(View view) {
        if (obstacles >= 0) {
            obstacles--;
            tvObstacles.setText("Obstacles: " + obstacles);
        }
    }

    public void increaseSpeed(View view) {
        if (speed < 10) {
            speed++;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    public void decreaseSpeed(View view) {
        if (speed > 1) {
            speed--;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    public void apply(View view) {
        SnakeActivity.setDelay(100 - (speed - 1) * 10);
        SnakeActivity.setObstacleCount(obstacles);
        SnakeActivity.setWalls(cbWalls.isChecked());
        SnakeActivity.setColorID(color);

        save.setColor(color);
        save.setObstacleCount(obstacles);
        save.setSpeed(100 - (speed - 1) * 10);
        save.setWalls(cbWalls.isChecked());
        try {
            SnakeActivity.saveGame(save, getFilesDir(), "snake.save");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back(View view) {
        finish();
    }

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
        }
    }

    public void defaultSettings(View view) {
        int score = save.getHighScore();
        save = new Save();
        save.setHighScore(score);

        walls = save.getWalls();
        obstacles = save.getObstacleCount();
        speed = ((100 - save.getSpeed()) / 10) + 1;
        color = save.getColor();

        cbWalls = findViewById(R.id.walls_checkbox);
        cbWalls.setChecked(walls);

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
        }
    }
}
