package entw.app.android.snek.OpenGL;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {

    private float mLight = 0.0f;
    private float[] prevPosition = new float[3];
    //number of coordinates per vertex in this array
    private static final int faces = 12;

    private float[] mModelMatrix = new float[16];

    private int mMVPMatrixHandle;
    private int mProgramHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mLightHandle;
    private static final int VERTICES_PER_FACE = 3;
    private static final int normalStride = 3 * 4;
    private final float[] mLightPos = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private int mMVMatrixHandle;
    private int mNormalHandle;
    private int mLightPosHandle;
    private static final int COORDS_PER_VERTEX = 3;
    private static final float size = 0.05f;
    private static final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final int colorStride = 4 * 4;   //4 Values * 4 Byte
    private FloatBuffer normalBuffer;
    private float color[] = new float[faces * VERTICES_PER_FACE * 4];

    /**
     * @param position       the position where the cube should be drawn
     * @param type           the type of cube(food,obstacle,snake) no longer used
     * @param vertexShader   the vertex shader code
     * @param fragmentShader the fragment shader code
     * @param rgba           the color information
     * @param light          if there is a light in the scene
     */
    Cube(float[] position, int type, final String vertexShader, final String fragmentShader, final float[] rgba, final boolean light) {

        if (light)
            mLight = 1.0f;

        float[] cubeCoords = new float[]{
                // Front
                -size + position[0], size + position[1], size,
                -size + position[0], -size + position[1], size,
                size + position[0], -size + position[1], size,
                -size + position[0], size + position[1], size,
                size + position[0], -size + position[1], size,
                size + position[0], size + position[1], size,

                // Right
                -size + position[0], size + position[1], size,
                -size + position[0], size + position[1], -size,
                -size + position[0], -size + position[1], -size,
                -size + position[0], size + position[1], size,
                -size + position[0], -size + position[1], -size,
                -size + position[0], -size + position[1], size,

                // Top
                size + position[0], size + position[1], size,
                size + position[0], size + position[1], -size,
                -size + position[0], size + position[1], -size,
                size + position[0], size + position[1], size,
                -size + position[0], size + position[1], -size,
                -size + position[0], size + position[1], size,

                // Bottom
                -size + position[0], -size + position[1], size,
                -size + position[0], -size + position[1], -size,
                size + position[0], -size + position[1], -size,
                -size + position[0], -size + position[1], size,
                size + position[0], -size + position[1], -size,
                size + position[0], -size + position[1], size,

                // Left
                size + position[0], -size + position[1], size,
                size + position[0], -size + position[1], -size,
                size + position[0], size + position[1], -size,
                size + position[0], -size + position[1], size,
                size + position[0], size + position[1], -size,
                size + position[0], size + position[1], size,

                // Back
                -size + position[0], size + position[1], -size,
                -size + position[0], -size + position[1], -size,
                size + position[0], -size + position[1], -size,
                -size + position[0], size + position[1], -size,
                size + position[0], -size + position[1], -size,
                size + position[0], size + position[1], -size
        };

        for (int i = 0; i < color.length; i += 4) {
            color[i] = rgba[0];
            color[i + 1] = rgba[1];
            color[i + 2] = rgba[2];
            color[i + 3] = rgba[3];
        }

        float[] normals = new float[]{
                // Front
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                // Right
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // Top
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                // Bottom
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,

                // Left
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,

                // Back
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f
        };

        //initialize vertex byte buffer for shape coordinates
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeCoords).position(0);

        // initialize byte buffer for the vertex color
        colorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(color).position(0);

        normalBuffer = ByteBuffer.allocateDirect(normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(normals).position(0);

        int vertexShaderHandle = GameRenderer.compileShader(GLES31.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = GameRenderer.compileShader(GLES31.GL_FRAGMENT_SHADER, fragmentShader);
        mProgramHandle = GameRenderer.createProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"aPosition", "aColor", "aNormal"});

        mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        mMVMatrixHandle = GLES31.glGetUniformLocation(mProgramHandle, "uMVMatrix");
        mLightPosHandle = GLES31.glGetUniformLocation(mProgramHandle, "uLightPos");
        mLightHandle = GLES31.glGetUniformLocation(mProgramHandle, "uLight");

        mPositionHandle = GLES31.glGetAttribLocation(mProgramHandle, "aPosition");
        mColorHandle = GLES31.glGetAttribLocation(mProgramHandle, "aColor");
        mNormalHandle = GLES31.glGetAttribLocation(mProgramHandle, "aNormal");

        Matrix.setIdentityM(mModelMatrix, 0);
    }

    /**
     * Draws the cube in the scene
     * @param viewMatrix    the viewMatrix
     * @param projectionMatrix  the projectionMatrix
     * @param lightPos  the position of the light
     */
    public void draw(final float[] viewMatrix, final float[] projectionMatrix, final float[] lightPos) {
        float[] mvpMatrix = new float[16];

        GLES31.glUseProgram(mProgramHandle);

        vertexBuffer.position(0);
        GLES31.glEnableVertexAttribArray(mPositionHandle);
        GLES31.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES31.GL_FLOAT, false, vertexStride, vertexBuffer);

        colorBuffer.position(0);
        GLES31.glEnableVertexAttribArray(mColorHandle);
        GLES31.glVertexAttribPointer(mColorHandle, 4, GLES31.GL_FLOAT, false, colorStride, colorBuffer);

        normalBuffer.position(0);
        GLES31.glEnableVertexAttribArray(mNormalHandle);
        GLES31.glVertexAttribPointer(mNormalHandle, 3, GLES31.GL_FLOAT, false, normalStride, normalBuffer);

        GLES31.glUniform3f(mLightPosHandle, lightPos[0], lightPos[1], lightPos[2]);
        GLES31.glUniform1f(mLightHandle, mLight);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        GLES31.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvpMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, 36);

        GLES31.glDisableVertexAttribArray(mPositionHandle);
        GLES31.glDisableVertexAttribArray(mColorHandle);
        GLES31.glDisableVertexAttribArray(mNormalHandle);
    }

    /**
     * Moves the cube to the given position by calculating the difference between the old and new position
     * and creating a translation matrix
     *
     * @param currentPos the current position of the cube in the model
     */
    public void move(final float[] currentPos) {
        float[] dPos = new float[3];
        dPos[0] = currentPos[0] - prevPosition[0];
        dPos[1] = currentPos[1] - prevPosition[1];
        dPos[2] = currentPos[2] - prevPosition[2];

        Matrix.translateM(mModelMatrix, 0, dPos[0], dPos[1], dPos[2]);
        prevPosition = currentPos;
    }

    /**
     * Scales the cube on the given axis
     *
     * @param x x-Axis
     * @param y y-Axis
     * @param z z-Axis
     */
    public void scale(float x, float y, float z) {
        Matrix.scaleM(mModelMatrix, 0, x, y, z);
    }
}

