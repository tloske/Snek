package entw.app.android.snek;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toolbar;

public class OptionsActivity extends AppCompatActivity {

    CheckBox cbWalls;
    TextView tvObstacles;
    TextView tvSpeed;
    private boolean walls;
    private int obstacles;
    private int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setActionBar(myToolbar);

        walls = SnakeActivity.getWalls();
        obstacles = SnakeActivity.getObstacleCount();
        speed = (100 - SnakeActivity.getDelay()) / 10;

        cbWalls = findViewById(R.id.walls_checkbox);
        cbWalls.setChecked(walls);

        tvObstacles = findViewById(R.id.obstacles_text_view);
        tvObstacles.setText("Obstacles: " + obstacles);

        tvSpeed = findViewById(R.id.speed_text_view);
        tvSpeed.setText("Speed: " + speed);
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
        if (speed <= 10) {
            speed++;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    public void decreaseSpeed(View view) {
        if (speed >= 0) {
            speed--;
            tvSpeed.setText("Speed: " + speed);
        }
    }

    public void apply(View view) {
        SnakeActivity.setDelay(100 - speed * 10);
        SnakeActivity.setObstacleCount(obstacles);
        SnakeActivity.setWalls(cbWalls.isChecked());
    }

    public void cancel(View view) {
    }
}
