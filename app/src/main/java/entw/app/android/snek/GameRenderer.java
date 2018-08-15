package entw.app.android.snek;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private Snek mSnek;
    private Square mSquare;

    private float[] tempMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //initialize Snek
        mSnek = new Snek();

        //initialize a square
        mSquare = new Square();

        Matrix.setIdentityM(tempMatrix, 0);
    }

    //mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES31.glViewport(0, 0, i, i1);

        float ratio = (float) i / i1;

        //this projection matrix is applied to object coordinates
        //in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    private float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] scratch = new float[16];

        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        //Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //Create a rotation transformation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 1.5708f;
        Matrix.setRotateM(mRotationMatrix, 0, 90, 0, 0, 1.0f);

        //Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        //Draw Shape
        mSnek.draw(scratch);
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

    private volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}
