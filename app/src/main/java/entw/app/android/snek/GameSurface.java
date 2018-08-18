package entw.app.android.snek;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class GameSurface extends GLSurfaceView {

    private Timer timer;
    private TimerTask tTask;

    public GameSurface(Context context, final GameRenderer renderer, final SnekActivity activity) {
        super(context);

        setEGLContextClientVersion(3);

        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        requestRender();

        tTask = new TimerTask() {
            @Override
            public void run() {
                if (renderer.checkCollision()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.gameOver();
                        }
                    });
                } else {
                    requestRender();
                }
            }
        };

        timer = new Timer();
        timer.schedule(tTask, 0, 30);
    }

    public void pause() {
        timer.cancel();
    }

    public void resume() {
        timer = new Timer();
        timer.schedule(tTask, 0, 30);
    }
}
