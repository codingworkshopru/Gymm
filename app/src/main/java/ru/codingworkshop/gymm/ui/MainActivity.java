package ru.codingworkshop.gymm.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.common.LifecycleDaggerActivity;

public class MainActivity extends LifecycleDaggerActivity {
    private static final int TEST_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // menu
    //-----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_program:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------
}
