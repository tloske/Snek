package entw.app.android.snek;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(myToolbar);
    }
}
