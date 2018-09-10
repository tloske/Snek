package entw.app.android.snek.OpenGL;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import entw.app.android.snek.R;

public class GameRenderer implements GLSurfaceView.Renderer {

    String vertexShader;
    String fragmentShader;

    //mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private Context mContext;
    private final float[] vpMatrix = new float[16];
    private float[] mLightPos = new float[4];

    private Cube mSnake;
    private Cube mFood;
    private Cube mGround;
    private ArrayList<Cube> mObstacles;
    private ArrayList<Cube> mSnakeBody;
    private ArrayList<Cube> mWall;
    private boolean mLight;

    private int mObstacleCount;
    private boolean mWalls;
    private float[] colorBackground;
    private float[] colorSnake;
    private float[] colorFood;
    private float[] colorObstacle;

    public GameRenderer(Context contex, final int obstacles, final boolean walls, final int[] colors, final boolean light) {
        super();
        mContext = contex;
        mObstacleCount = obstacles;
        mWalls = walls;
        mLight = light;
        int[] red = new int[colors.length];
        int[] green = new int[colors.length];
        int[] blue = new int[colors.length];
        int[] alpha = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            red[i] = Color.red(colors[i]);
            green[i] = Color.green(colors[i]);
            blue[i] = Color.blue(colors[i]);
            alpha[i] = Color.alpha(colors[i]);
        }
        colorBackground = new float[]{red[0] / 255.0f, green[0] / 255.0f, blue[0] / 255.0f, alpha[0] / 255.0f};
        colorSnake = new float[]{red[1] / 255.0f, green[1] / 255.0f, blue[1] / 255.0f, alpha[1] / 255.0f};
        colorFood = new float[]{red[2] / 255.0f, green[2] / 255.0f, blue[2] / 255.0f, alpha[2] / 255.0f};
        colorObstacle = new float[]{red[3] / 255.0f, green[3] / 255.0f, blue[3] / 255.0f, alpha[3] / 255.0f};
    }

    /**
     * Creates a new Program using the given shader handles
     *
     * @param vertexShaderHandle
     * @param fragmentShaderHandle
     * @return the newly created program
     */
    public static int createProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
        int programHandle = GLES31.glCreateProgram();

        if (programHandle != 0) {
            GLES31.glAttachShader(programHandle, vertexShaderHandle);
            GLES31.glAttachShader(programHandle, fragmentShaderHandle);

            // No idea what this does

            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES31.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            GLES31.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES31.glGetProgramiv(programHandle, GLES31.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                GLES31.glDeleteProgram(programHandle);
                programHandle = 0;
            }

            if (programHandle == 0) {
                throw new RuntimeException("Error creating program.");
            }
        }
        return programHandle;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES31.glClearColor(colorBackground[0], colorBackground[1], colorBackground[2], colorBackground[3]);
        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);

        final float eyeX = 0.0f, eyeY = 0.0f, eyeZ = 3.0f;
        final float lookX = 0.0f, lookY = 0.0f, lookZ = 0.0f;
        final float upX = 0.0f, upY = 1.0f, upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
//        Matrix.rotateM(mViewMatrix, 0, 45.0f, 1.0f, 0.0f, 0.0f);

        // load shader Code from file
        vertexShader = loadShader(R.raw.vertex_shader);
        fragmentShader = loadShader(R.raw.fragment_shader);

        mLightPos = new float[]{0, 0, 0, 1.0f};

        // initialize Snake at pos(0,0,0)
        mSnake = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 0, vertexShader, fragmentShader, colorSnake, mLight);
        // initialize food at pos(0,0,0)
        mFood = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 1, vertexShader, fragmentShader, colorFood, mLight);
        // initialize multiple obstacles at pos(0,0,0)
        mObstacles = new ArrayList<>();
        for (int i = 0; i < mObstacleCount; i++) {
            mObstacles.add(new Cube(new float[]{0.0f, 0.0f, 0.0f}, 2, vertexShader, fragmentShader, colorObstacle, mLight));
        }
        // create a new array list that holds the cubes that make up the snakes body
        mSnakeBody = new ArrayList<>();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES31.glViewport(0, 0, i, i1);

        float ratio = (float) i / i1;
        final float left = -ratio;
        final float right = ratio;
        final float top = 1.0f;
        final float bottom = -1.0f;
        final float near = 1.0f;
        final float far = 7.0f;

        mGround = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 3, vertexShader, fragmentShader, colorBackground, mLight);
        mGround.scale((2 * ratio) * 10.0f, 20.0f, 0.0f);
        mWall = new ArrayList<>();
        if (mWalls) {
            Cube tmp = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 2, vertexShader, fragmentShader, colorObstacle, mLight);
            tmp.scale((2 * ratio) * 10.0f, 0.1f, 1.0f);
            tmp.move(new float[]{0.0f, top * 10.0f, 0.0f});
            mWall.add(tmp);

            tmp = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 2, vertexShader, fragmentShader, colorObstacle, mLight);
            tmp.scale((2 * ratio) * 10.0f, 0.1f, 1.0f);
            tmp.move(new float[]{0.0f, bottom * 10.0f, 0.0f});
            mWall.add(tmp);

            tmp = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 2, vertexShader, fragmentShader, colorObstacle, mLight);
            tmp.scale(0.1f, 20.0f, 1.0f);
            tmp.move(new float[]{left * 10.0f, 0.0f, 0.0f});
            mWall.add(tmp);

            tmp = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 2, vertexShader, fragmentShader, colorObstacle, mLight);
            tmp.scale(0.1f, 20.0f, 1.0f);
            tmp.move(new float[]{right * 10.0f, 0.0f, 0.0f});
            mWall.add(tmp);
        }

        //this projection matrix is applied to object coordinates
        //in the onDrawFrame() method

        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
//        Matrix.frustumM(mProjectionMatrix, 0, right, left, bottom, top, near, far);
        Matrix.multiplyMM(vpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    /**
     * Loads a shader from a file
     *
     * @param resID the resource id of the file
     * @return a String containing the shader code
     */
    private String loadShader(int resID) {
        InputStream inputStream = mContext.getResources().openRawResource(resID);
        StringBuilder shaderCode = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                shaderCode.append(line);
            }
            inputStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to load shader", e);
        }

        return shaderCode.toString();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        // Draws the ground
        mGround.draw(mViewMatrix, mProjectionMatrix, mLightPos);

        // Draws the walls around the area
        for (Cube wall : mWall) {
            wall.draw(mViewMatrix, mProjectionMatrix, mLightPos);
        }

        // Draws the food
        mFood.draw(mViewMatrix, mProjectionMatrix, mLightPos);

        // Draws the obstacles
        for (Cube obstacle : mObstacles) {
            obstacle.draw(mViewMatrix, mProjectionMatrix, mLightPos);
        }

        // Draws the body of the snake
        for (Cube body : mSnakeBody) {
            body.draw(mViewMatrix, mProjectionMatrix, mLightPos);
        }

        // Draws the actual snake
        mSnake.draw(mViewMatrix, mProjectionMatrix, mLightPos);
    }

    /**
     * Moves a list of cubes using the given position data
     *
     * @param cubePositions a list containing position data
     * @param type          the type cubes to be moved
     */
    public void move(ArrayList<float[]> cubePositions, final int type) {
        Iterator<float[]> it1 = cubePositions.iterator();
        Iterator<Cube> it2;
        switch (type) {
            case 0: //SnakeBody
                it2 = mSnakeBody.iterator();
                break;
            case 2: //Obstacle
                it2 = mObstacles.iterator();
                break;
            default:
                return;
        }

        while (it1.hasNext() && it2.hasNext()) {
            float[] pos = it1.next();
            Cube cube = it2.next();
            cube.move(pos);
        }
    }

    /**
     * Compiles the shader from the given shaderCode
     *
     * @param type       the type of shader (vertex || fragment)
     * @param shaderCode the shader code
     * @return
     */
    public static int compileShader(int type, String shaderCode) {

        //create a vertex shader type(GLES31.GL_VERTEX_SHADER)
        //or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
        int shaderHandle = GLES31.glCreateShader(type);

        if (shaderHandle != 0) {
            //add the source code to the shader and compile it
            GLES31.glShaderSource(shaderHandle, shaderCode);
            GLES31.glCompileShader(shaderHandle);

            final int[] compileStatus = new int[1];
            GLES31.glGetShaderiv(shaderHandle, GLES31.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                GLES31.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }

            if (shaderHandle == 0)
                throw new RuntimeException("Error creating shader. Type: " + type);
        }
        return shaderHandle;
    }

    /**
     * Moves the given type of cube to the given position
     *
     * @param currentPos the position the cube should be moved to
     * @param type       the type of cube to be moved
     */
    public void move(final float[] currentPos, final int type) {
        switch (type) {
            case 0: //Snake
                mSnake.move(currentPos);
                mLightPos = new float[]{currentPos[0], currentPos[1], currentPos[2], 1.0f};

                Matrix.multiplyMV(mLightPos, 0, mViewMatrix, 0, mLightPos, 0);
                break;
            case 1: //Food
                mFood.move(currentPos);
                break;
            default:
                return;
        }
    }

    /**
     * Adds a new body part to the Snake at the current snake position
     *
     * @param currentPos the current position of the snake
     */
    public void addToBody(final float[] currentPos) {
        Cube cube = new Cube(new float[]{0.0f, 0.0f, 0.0f}, 0, vertexShader, fragmentShader, colorSnake, mLight);
        cube.move(currentPos);
        mSnakeBody.add(cube);
    }
}