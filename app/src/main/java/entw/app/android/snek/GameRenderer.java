package entw.app.android.snek;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private Square mSquare;
    //mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private Food mFood;
    private ArrayList<Obstacle> mObstacles;
    private float[] mRotationMatrix = new float[16];
    private float[] mTranslationMatrix = new float[16];
    private float[] mMovement = {0.0f, 0.0f, 0.0f};

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //initialize a square
        mSquare = new Square(new float[]{0.0f, 0.0f, 0.0f});

        float[] pos = new float[]{randNr(), randNr(), 0.0f};
        Log.d("DEBUG", Arrays.toString(pos));
        mFood = new Food(pos);

        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.setIdentityM(mRotationMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES31.glViewport(0, 0, i, i1);

        Log.d("DEBUG", "i:" + i + " i1" + i1);
        float ratio = (float) i / i1;

        //this projection matrix is applied to object coordinates
        //in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, ratio, -ratio, -1, 1, 3, 7);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] scratch = new float[16];

        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        //Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mFood.draw(mMVPMatrix);
//        for(Obstacle obstacle:mObstacles){
//            obstacle.draw(mMVPMatrix);
//        }

        //Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        //Creates the translation Matrix to e used on the Snek
        Matrix.translateM(mTranslationMatrix, 0, mMovement[0], mMovement[1], mMovement[2]);

        //update the Position of the object
        float[] position = mSquare.getPosition();
        float[] newPosition = {position[0] + mMovement[0], position[1] + mMovement[1], position[2] + mMovement[2]};
        mSquare.setPosition(newPosition);

        Matrix.multiplyMM(mMVPMatrix, 0, mTranslationMatrix, 0, scratch, 0);

        //Draw Shape
        mSquare.draw(mMVPMatrix);
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

    public void setRotation(float[] rotation) {
        mRotationMatrix = rotation;
    }

    public void setTranslation(float[] movement) {
        mMovement = movement;
    }

    //Generates a random Number between -0.9 and 0.9
    private float randNr() {
        float result;

        float step = 0.2f;
        float lower = 0.1f;
        float upper = 2.0f;
        float rand = (float) (lower + Math.random() * (upper - lower));
        result = rand - rand % step + lower - 1.0f;
        return result;
    }

    public boolean checkCollision() {
//        try {
//            return Arrays.equals(mSquare.getPosition(), mFood.getPosition());
//        } catch (Exception e) {
//            return false;
//        }
        return true;
//        return false;
    }
}
