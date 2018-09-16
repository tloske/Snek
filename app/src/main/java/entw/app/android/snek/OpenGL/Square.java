package entw.app.android.snek.OpenGL;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Class no longer used was replaced by cube class
 */
public class Square {

    private float[] prevPosition = new float[3];

    private float[] mModelMatrix = new float[16];

    private int mMVPMatrixHandle;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer colorBuffer;

    //number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final float size = 0.05f;
    private static final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final int colorStride = 4 * 4;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};    //order to draw vertices

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};


    public Square(float[] position, int type, int matrixHandle, int program, int positionHandle, int colorHandel) {
        float[] squareCoords = new float[]{
                -size + position[0], size + position[1], 0.0f,  //top left
                -size + position[0], -size + position[1], 0.0f, //bottom left
                size + position[0], -size + position[1], 0.0f,  //bottom right
                size + position[0], size + position[1], 0.0f};  //top right

        switch (type) {
            case 0:
                color = new float[]{0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f};
                break;
            case 1:
                color = new float[]{0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f};
                break;
            case 2:
                color = new float[]{1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f};
                break;
            default:
                break;
        }

        mProgram = program;
        mPositionHandle = positionHandle;
        mMVPMatrixHandle = matrixHandle;
        mColorHandle = colorHandel;

        //initialize vertex byte buffer for shape coordinates
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        //initialize byte buffer for the draw list
        drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }


    public void draw(float[] mvpMatrix, float[] currentPos) {
        final float[] dPos = new float[3];
        dPos[0] = currentPos[0] - prevPosition[0];
        dPos[1] = currentPos[1] - prevPosition[1];
        dPos[2] = currentPos[2] - prevPosition[2];

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, dPos[0], dPos[1], dPos[2]);

        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, mModelMatrix, 0);

        GLES31.glEnableVertexAttribArray(mPositionHandle);

        GLES31.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES31.GL_FLOAT, false, vertexStride, vertexBuffer);

        GLES31.glEnableVertexAttribArray(mColorHandle);

        GLES31.glVertexAttribPointer(mColorHandle, 4, GLES31.GL_FLOAT, false, colorStride, colorBuffer);

        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES31.glDrawElements(GLES31.GL_TRIANGLES, drawOrder.length, GLES31.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES31.glDisableVertexAttribArray(mPositionHandle);
        GLES31.glDisableVertexAttribArray(mColorHandle);

        prevPosition = currentPos;
    }
}