package entw.app.android.snek;

import java.io.Serializable;

public class Save implements Serializable {

    private int highScore = 0;
    private boolean walls = true;
    private int obstacleCount = 0;
    private int speed = 100;
    private int color = R.array.colorScheme1;

    private boolean light = false;

    public boolean getLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public boolean getWalls() {
        return walls;
    }

    public void setWalls(boolean walls) {
        this.walls = walls;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void setObstacleCount(int obstacleCount) {
        this.obstacleCount = obstacleCount;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
