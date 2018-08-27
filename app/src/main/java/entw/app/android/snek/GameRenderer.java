package entw.app.android.snek;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private ArrayList<Square> squares;
    //mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float[] mTranslationMatrix = new float[16];
    private boolean finished = true;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        squares = new ArrayList<>();
//        square = new Square(new float[]{0.0f, 0.0f, 0.0f}, 0);
//        square2 = new Square(new float[]{1.0f, 0.0f, 0.0f}, 1);

        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.setIdentityM(mRotationMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES31.glViewport(0, 0, i, i1);

        float ratio = (float) i / i1;

        //this projection matrix is applied to object coordinates
        //in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, ratio, -ratio, -1, 1, 3, 7);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        finished = false;
        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        //Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        for (Square square : squares)
            square.draw(mMVPMatrix);

        finished = true;

//        //Combine the rotation matrix with the projection and camera view
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//
//        //Creates the translation Matrix to e used on the Snek
//        Matrix.translateM(mTranslationMatrix, 0, mMovement[0], mMovement[1], mMovement[2]);
//
//        Matrix.multiplyMM(mMVPMatrix, 0, mTranslationMatrix, 0, scratch, 0);
//
//        //Draw Shape
//        mSnek.draw(mMVPMatrix);
    }

    public boolean finished() {
        return finished;
    }

    public static int loadShader(int type, String shaderCode) {

        //create a vertex shader type(GLES31.GL_VERTEX_SHADER)
        //or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
        int shader = GLES31.glCreateShader(type);

        //add the source code to the shader and compile it
        GLES31.glShaderSource(shader, shaderCode);
        GLES31.glCompileShader(shader);

        return shader;
    }

    public void addSquares(ArrayList<Square> squares) {
        this.squares = squares;
    }


    public void addSquare(float x, float y, int type) {
        if (squares == null)
            squares = new ArrayList<>();

        squares.add(new Square(new float[]{x, y, 0.0f}, type));
    }

//    public void setTranslation(float[] movement) {
//        mMovement = movement;
//    }
}