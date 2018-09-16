package entw.app.android.snek;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class GameModel {
    private int[][] mGameBoard;
    private int mSnake[] = new int[3];
    private ArrayDeque<int[]> mPrevPos;
    private int mScore;
    private int[] direction = new int[]{0, 0};
    private int mObstacleCount;
    private boolean mWalls;

    /**
     * @param ratio     The display ratio
     * @param obstacles the amount of obstacles
     * @param walls     if walls are part of the game or not
     */
    GameModel(int ratio, int obstacles, boolean walls) {
        mScore = 0;
        mSnake[0] = ratio;
        mSnake[1] = 10;
        mObstacleCount = obstacles;
        mWalls = walls;

        mGameBoard = new int[ratio * 2 + 1][21];
        mPrevPos = new ArrayDeque<>();

        spawnFood();
        spawnObstacles();
    }

    /**
     * Moves the snake in the direction it is currently traveling
     */
    public void move() {
        mPrevPos.addFirst(new int[]{mSnake[0], mSnake[1]});

        mSnake[0] += direction[0];
        mSnake[1] += direction[1];

        while (mSnake[2] < mPrevPos.size())
            mPrevPos.removeLast();
    }

    /**
     * Spawns food in a random position on the board
     */
    private void spawnFood() {
        int pos[] = randPos();
        mGameBoard[pos[0]][pos[1]] = 1;
    }

    /**
     * Spawns obstacles up to the obstacle count on the board
     */
    private void spawnObstacles() {
        for (int i = 0; i < mObstacleCount; i++) {
            int pos[];
            do {
                pos = randPos();
            }
            while (mGameBoard[pos[0]][pos[1]] != 0 || (pos[0] == mSnake[0] && pos[1] == mSnake[1]));
            mGameBoard[pos[0]][pos[1]] = 2;
        }
    }

    /**
     * Checks if the snake collides with a wall, an obstacle, food or with it self
     *
     * @return if the snake collided if anything
     */
    public boolean checkCollision() {
        if (mSnake[0] < 0 || Math.abs(mSnake[0]) >= mGameBoard.length) {
            if (mWalls)
                return true;
            else {
                if (mSnake[0] < 0) mSnake[0] = mGameBoard.length - 1;
                else if (mSnake[0] >= mGameBoard.length) mSnake[0] = 0;
            }
        } else if (mSnake[1] < 0 || Math.abs(mSnake[1]) >= mGameBoard[0].length) {
            if (mWalls)
                return true;
            else {
                if (mSnake[1] < 0) mSnake[1] = mGameBoard[0].length - 1;
                else if (mSnake[1] >= mGameBoard[0].length) mSnake[1] = 0;
            }
        } else if (mGameBoard[mSnake[0]][mSnake[1]] == 1) {
            mScore++;
            for (int[] aMGameBoard : mGameBoard)
                Arrays.fill(aMGameBoard, 0);
            mSnake[2]++;
            spawnFood();
            spawnObstacles();
            return false;
        } else if (mGameBoard[mSnake[0]][mSnake[1]] == 2) {
            return true;
        } else {
            for (int[] pos : mPrevPos) {
                if (pos[0] == mSnake[0] && pos[1] == mSnake[1])
                    return true;
            }
        }
        return false;
    }

    /**
     * Calculates an array containing a position coordinates using the size of the board
     * @return the array containing coordinates
     */
    private int[] randPos() {
        int[] pos = new int[2];
        pos[0] = (int) (Math.random() * mGameBoard.length);
        pos[1] = (int) (Math.random() * mGameBoard[0].length);
        return pos;
    }

    /**
     * Returns the current score
     * @return the score
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Returns the current position of the snake
     * @return the snakes position
     */
    public int[] getPos() {
        return new int[]{mSnake[0], mSnake[1]};
    }

    /**
     * An array that represents the board and contains the information where the food and obstacles are
     * @return an array representation of the board
     */
    public int[][] getBoard() {
        return mGameBoard;
    }

    /**
     * Sets the direction the snake is facing and traveling
     * @param direction the direction
     */
    public void setDirection(int[] direction) {
        this.direction = direction;
    }

    /**
     * Gets all the previous position of the snake, used to draw the body of the snake
     * @return a deque containing the previous positions of the snake
     */
    public ArrayDeque<int[]> getPrevPos() {
        return mPrevPos;
    }

    /**
     * Returns the given coordinates and returns them as coordinates in the OpenGL context
     * @param coords    the coordinates on the board
     * @return OpenGL Coordinates
     */
    public float[] OpenGLCoords(int coords[]) {
        float[] pos = new float[3];
        pos[0] = (coords[0] - (mGameBoard.length / 2)) / 10.0f;
        pos[1] = (coords[1] - (mGameBoard[0].length / 2)) / 10.0f;
        pos[2] = 0.0f;
        return pos;
    }

    /**
     * Returns OpenGL coordinates for an ArrayList of coordinates
     * @param positions Deque containing coordinates
     * @return a list with OpenGL coordinates
     */
    public ArrayList<float[]> OpenGLCoords(ArrayDeque<int[]> positions) {
        ArrayList<float[]> newList = new ArrayList<>();

        for (int[] pos : positions) {
            newList.add(OpenGLCoords(pos));
        }

        return newList;
    }

    /**
     * Returns the current size of the snake
     *
     * @return size of the snake
     */
    public int getSnakeSize() {
        return mSnake[2];
    }
}