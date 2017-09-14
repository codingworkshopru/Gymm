package ru.codingworkshop.gymm.testing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import ru.codingworkshop.gymm.R;

public class SimpleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);
        content.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        content.setId(R.id.container);
        setContentView(content);
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }
}
