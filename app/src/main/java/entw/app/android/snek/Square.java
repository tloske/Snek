package entw.app.android.snek;

import android.opengl.GLES20;
import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

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
    private static final float size = 0.1f;
    private static float squareCoords[];/* = {
            -0.1f, 0.2f, 0.0f,      //top left
            -0.1f, -0.2f, 0.0f,     //bottom left
            0.1f, -0.2f, 0.0f,      //bottom right
            0.1f, 0.2f, 0.0f};      //top right*/

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};    //order to draw vertices

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private float[] currentPosition;

    Square(float[] position) {
        squareCoords = new float[]{-size + position[0], size + position[1], 0.0f,
                -size + position[0], -size + position[1], 0.0f,
                size + position[0], -size + position[1], 0.0f,
                size + position[0], size + position[1], 0.0f};

        currentPosition = position;

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

    public float[] getPosition() {
        return currentPosition;
    }

    public void setPosition(float[] position) {
        currentPosition = position;
    }
}

class Snek extends Square {
    private ArrayList<SnekBody> bodyParts;
    private ArrayDeque<float[]> transformations;

    public Snek(float[] position) {
        super(position);
        bodyParts = new ArrayList<>();
        transformations = new ArrayDeque<>();
    }

    @Override
    public void draw(float[] mvpMatrix) {
        super.draw(mvpMatrix);
        drawBody();
        addTransforamtion(mvpMatrix);
    }

    public void addToBody(SnekBody part) {
        bodyParts.add(part);
    }

    private void addTransforamtion(float[] mvpMatrix) {
        transformations.addFirst(mvpMatrix);
    }

    private void drawBody() {

        Iterator<SnekBody> it1 = bodyParts.iterator();
        Iterator<float[]> it2 = transformations.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            it1.next().draw(it2.next());
        }

        while (bodyParts.size() < transformations.size()) {
            transformations.removeLast();
        }
    }

    private boolean checkCollision() {

        return false;
    }
}

class SnekBody extends Square {
    public SnekBody(float[] position) {
        super(position);
    }
}

class Obstacle extends Square {
    public Obstacle(float[] position) {
        super(position);
    }
}

class Food extends Square {
    Food(float[] position) {
        super(position);
    }
}
