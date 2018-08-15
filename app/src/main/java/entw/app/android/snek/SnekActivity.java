package entw.app.android.snek;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class SnekActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snek);

        mGLView = new GameSurface(this);
        RelativeLayout rl = findViewById(R.id.snek_layout);
        rl.addView(mGLView, 0);
        //setContentView(mGLView);
    }
}
