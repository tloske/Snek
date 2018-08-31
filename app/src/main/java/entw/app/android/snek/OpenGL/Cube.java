package entw.app.android.snek.OpenGL;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {
    private float[] prevPosition = new float[3];

    private float[] mModelMatrix = new float[16];

    private int mMVPMatrixHandle;
    private int mProgramHandle;
    private int mPositionHandle;
    private int mColorHandle;

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer colorBuffer;

    //number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final float size = 0.05f;
    private static final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final int colorStride = 4 * 4;   //4 Values * 4 Byte

    private short drawOrder[] = {0, 1, 2, 0, 2, 3,  //Top
            0, 4, 5, 0, 5, 1,   //Side
            3, 7, 4, 3, 4, 0,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            4, 5, 6, 4, 6, 7};  //Bottom

    private float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};


    Cube(float[] position, int type, final String vertexShader, final String fragmentShader) {
        float[] cubeCoords = new float[]{
                -size + position[0], size + position[1], size,      //Top left
                -size + position[0], -size + position[1], size,     //Bottom left
                size + position[0], -size + position[1], size,      //Bottom right
                size + position[0], size + position[1], size,       //Top Right
                -size + position[0], size + position[1], -size,     //Top left back
                -size + position[0], -size + position[1], -size,    //Bottom left back
                size + position[0], -size + position[1], -size,     //Bottom right back
                size + position[0], size + position[1], -size};     //Top right back

        switch (type) {
            case 0: //Snake and Body
                color = new float[]{0.0f, 1.0f, 0.0f, 0.0f,         //Top left
                        0.0f, 1.0f, 0.0f, 0.0f,                     //Bottom left
                        0.0f, 1.0f, 0.0f, 0.0f,                     //Bottom right
                        0.0f, 1.0f, 0.0f, 0.0f,                     //Top right
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f};
                break;
            case 1: //Food
                color = new float[]{0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f};
                break;
            case 2: //Obstacles and Walls
                color = new float[]{1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f};
                break;
            case 3:
                color = new float[]{0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                };
            default:
                break;
        }


        //initialize vertex byte buffer for shape coordinates
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        //initialize byte buffer for the draw list
        drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // initialize byte buffer for the vertex color
        colorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        int vertexShaderHandle = GameRenderer.compileShader(GLES31.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = GameRenderer.compileShader(GLES31.GL_FRAGMENT_SHADER, fragmentShader);
        mProgramHandle = GameRenderer.createProgram(vertexShaderHandle, fragmentShaderHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
    }


    public void draw(final float[] vpMatrix) {
        float[] mvpMatrix = new float[16];
        GLES31.glUseProgram(mProgramHandle);

        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, mModelMatrix, 0);

        mPositionHandle = GLES31.glGetAttribLocation(mProgramHandle, "aPosition");

        GLES31.glEnableVertexAttribArray(mPositionHandle);

        GLES31.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES31.GL_FLOAT, false, vertexStride, vertexBuffer);

        mColorHandle = GLES31.glGetAttribLocation(mProgramHandle, "aColor");

        GLES31.glEnableVertexAttribArray(mColorHandle);

        GLES31.glVertexAttribPointer(mColorHandle, 4, GLES31.GL_FLOAT, false, colorStride, colorBuffer);

        GLES31.glDrawElements(GLES31.GL_TRIANGLES, drawOrder.length, GLES31.GL_UNSIGNED_SHORT, drawListBuffer);

        mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgramHandle, "uMVPMatrix");

        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        GLES31.glDisableVertexAttribArray(mPositionHandle);
        GLES31.glDisableVertexAttribArray(mColorHandle);
    }

    /**
     * Moves the cube to the given position by calculating the difference between the old and new position
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
     * Scales the cube on the given
     *
     * @param x
     * @param y
     * @param z
     */
    public void scale(float x, float y, float z) {
        Matrix.scaleM(mModelMatrix, 0, x, y, z);
    }
}

