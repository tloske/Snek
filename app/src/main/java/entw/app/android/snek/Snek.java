package entw.app.android.snek;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Snek {

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

    //number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static float snekCoords[] = {
            0.0f, 0.1f, 0.0f,       //top
            -0.1f, -0.1f, 0.0f,     //bottom left
            0.1f, -0.1f, 0.0f       //bottom right
    };
    /*private static float snekCoords[] = {
            0.0f, 0.622008459f, 0.0f,       //top
            -0.5f, -0.311004243f, 0.0f,     //bottom left
            0.5f, -0.311004243f, 0.0f       //bottom right
    };*/

    //Set color with red,green, blue and alpha(opacity) values
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Snek() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(snekCoords.length * 4);   //number of coordinate values * 4 bytes per float
        //user the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        //create floating point buffer from ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        //add the coordinates to the FloatBuffer
        vertexBuffer.put(snekCoords);
        //set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = GameRenderer.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GameRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //create empty OpenGL ES Program
        mProgram = GLES31.glCreateProgram();

        //add the vertex shader to program
        GLES31.glAttachShader(mProgram, vertexShader);

        //add the fragment shader to program
        GLES31.glAttachShader(mProgram, fragmentShader);

        GLES31.glLinkProgram(mProgram);
    }

    private int mPositonHandle;
    private int mColorHandle;

    private final int vertexCount = snekCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; //4bytes per vertex

    public void draw(float[] mvpMatrix) {

        //Add program to OpenGL ES environment
        GLES31.glUseProgram(mProgram);

        //get handle to vertex shader's vPosition member
        mPositonHandle = GLES31.glGetAttribLocation(mProgram, "vPosition");

        //Enable a handle to the triangle vertices
        GLES31.glEnableVertexAttribArray(mPositonHandle);

        //Prepare the triangle coordinate data
        GLES31.glVertexAttribPointer(mPositonHandle, COORDS_PER_VERTEX, GLES31.GL_FLOAT, false, vertexStride, vertexBuffer);

        //get handle to fragment shader's vColor member
        mColorHandle = GLES31.glGetUniformLocation(mProgram, "vColor");

        //Set color for drawing Snek
        GLES31.glUniform4fv(mColorHandle, 1, color, 0);

        //Draw Snek
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount);

        //get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");

        //Pass the projection and view transformation to the shader
        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //Disable vertex array
        GLES31.glDisableVertexAttribArray(mPositonHandle);
    }
}
