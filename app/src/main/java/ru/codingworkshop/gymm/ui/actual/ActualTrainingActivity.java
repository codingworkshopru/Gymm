package ru.codingworkshop.gymm.ui.actual;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.common.eventbus.Subscribe;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.ProgramTrainingEntity;
import ru.codingworkshop.gymm.service.RestTimer;
import ru.codingworkshop.gymm.service.TrainingTimeService;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingActivity;

public class ActualTrainingActivity extends AppCompatActivity implements
        ActualTrainingAdapter.OnStepClickListener,
        ServiceConnection
{
    private static final String TAG = ActualTrainingActivity.class.getSimpleName();

    private RecyclerView mStepsView;
    private ActualTrainingAdapter mAdapter;
    private BlankFragment mFragment;
    private EntityDataStore<Persistable> data;
    private TrainingTimeService timeService;
    private boolean isBound = false;
    static final @IdRes int FRAGMENT_CONTAINER_ID = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? 1 : View.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_training);

        data = ((App) getApplication()).getData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStepsView = (RecyclerView) findViewById(R.id.list_view);
        mStepsView.setLayoutManager(new LinearLayoutManager(this));
        mStepsView.setHasFixedSize(true);
        mAdapter = new ActualTrainingAdapter(this);
        mStepsView.setAdapter(mAdapter);

        mAdapter.setModel(
                data.select(ProgramTraining.class)
                        .where(ProgramTrainingEntity.ID.eq(getIntent().getLongExtra(ProgramTrainingActivity.PROGRAM_TRAINING_ID, 0L)))
                        .get()
                        .first()
        );
    }

    private void l(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        l("onStart");
        if (TrainingTimeService.isRunning(this)) {
            l("\tbind");
            bindService(new Intent(this, TrainingTimeService.class), this, 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        l("onStop");
        if (isBound) {
            l("\tunbind");
            unbindService(this);
            timeService.unregisterObserver(this);
            isBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actual_training_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isServiceRunning = TrainingTimeService.isRunning(this);

        Intent serviceIntent = new Intent(this, TrainingTimeService.class);

        switch (item.getItemId()) {
            case R.id.actual_training_start_action:
                if (!isServiceRunning) {
                    serviceIntent.putExtra("model", mAdapter.getModel());
                    startService(serviceIntent);
                    bindService(serviceIntent, this, 0);
                }
                break;

            case R.id.actual_training_finish_action:
                stopService(serviceIntent);
                break;

            case R.id.actual_training_some_action:
                if (!timeService.isRestInProgress()) {
                    timeService.startRest(90);
                }
                break;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @UiThread
    @Override
    public void onStepClick(View view) {
        if (mFragment == null)
            mFragment = (BlankFragment) getSupportFragmentManager().getFragments().get(0);

        int position = view == null ? 0 : mStepsView.getChildAdapterPosition(view);
        mAdapter.setActivePosition(position);
    }

    @Subscribe
    public void onRestTimerTick(RestTimer.TickEvent event) {
        Log.d(TAG, "onRestTimerTick" + event.millisUntilFinished);
    }

    @Subscribe
    public void onRestFinished(RestTimer.FinishEvent event) {
        Log.d(TAG, "onRestFinished");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        l("onServiceConnected");
        timeService = ((TrainingTimeService.TimeServiceBinder) service).getService();
        timeService.registerObserver(this);
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        l("onServiceDisconnected");
        unbindService(this);
        isBound = false;
    }
}
