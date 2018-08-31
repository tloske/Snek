package entw.app.android.snek.OpenGL;

import android.content.Context;
import android.opengl.GLSurfaceView;


public class GameSurface extends GLSurfaceView {


    GameSurface(Context context) {
        super(context);
    }

    public GameSurface(Context context, GameRenderer renderer) {
        super(context);

        setEGLContextClientVersion(3);

        setRenderer(renderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
