package entw.app.android.snek;

import android.opengl.GLES20;
import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    private final String vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main(){" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "   gl_FragColor = vColor;" +
            "}";

    private int mMVPMatrixHandle;

    private final int mProgram;

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    //number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final float size = 0.05f;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};    //order to draw vertices

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    Square(float[] position, int type) {
        float[] squareCoords = new float[]{
                -size + position[0], size + position[1], 0.0f,  //top left
                -size + position[0], -size + position[1], 0.0f, //bottom left
                size + position[0], -size + position[1], 0.0f,  //bottom left
                size + position[0], size + position[1], 0.0f};


        switch (type) {
            case 0:
                color = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
                break;
            case 1:
                color = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
                break;
            case 2:
                color = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
                break;
            default:
                break;
        }

        //initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        //initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2); //# of coordinate values * 2 bytes per short
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = GameRenderer.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GameRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES31.glCreateProgram();

        GLES31.glAttachShader(mProgram, vertexShader);

        GLES31.glAttachShader(mProgram, fragmentShader);

        GLES31.glLinkProgram(mProgram);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexSride = COORDS_PER_VERTEX * 4;

    public void draw(float[] mvpMatrix) {
        GLES31.glUseProgram(mProgram);

        mPositionHandle = GLES31.glGetAttribLocation(mProgram, "vPosition");

        GLES31.glEnableVertexAttribArray(mPositionHandle);

        GLES31.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexSride, vertexBuffer);

        mColorHandle = GLES31.glGetUniformLocation(mProgram, "vColor");

        GLES31.glUniform4fv(mColorHandle, 1, color, 0);

        GLES31.glDrawElements(GLES31.GL_TRIANGLES, drawOrder.length, GLES31.GL_UNSIGNED_SHORT, drawListBuffer);

        mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES31.glDisableVertexAttribArray(mPositionHandle);
    }
}