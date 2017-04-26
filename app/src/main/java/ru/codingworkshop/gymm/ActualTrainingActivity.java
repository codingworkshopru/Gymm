package ru.codingworkshop.gymm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.service.TrainingTimeService;

public class ActualTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ProgramTraining>,
        ActualTrainingAdapter.OnStepClickListener,
        BlankFragment.OnFragmentInteractionListener
{
    private static final String TAG = ActualTrainingActivity.class.getSimpleName();

    private RecyclerView mStepsView;
    private ActualTrainingAdapter mAdapter;
    private BlankFragment mFragment;
    static final @IdRes int FRAGMENT_CONTAINER_ID = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? 1 : View.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_training);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStepsView = (RecyclerView) findViewById(R.id.list_view);
        mStepsView.setLayoutManager(new LinearLayoutManager(this));
        mStepsView.setHasFixedSize(true);
        mAdapter = new ActualTrainingAdapter(this);
        mStepsView.setAdapter(mAdapter);

//        getSupportLoaderManager().initLoader(TrainingAsyncLoader.LOADER_TRAINING_LOAD, getIntent().getExtras(), this);
    }

    @Override
    public Loader<ProgramTraining> onCreateLoader(int id, Bundle args) {
//        return new TrainingAsyncLoader(this, id, args);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ProgramTraining> loader, ProgramTraining data) {
        mAdapter.setModel(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actual_training_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrainingTimeService.class.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }

        switch (item.getItemId()) {
            case R.id.actual_training_start_action:
                if (!isServiceRunning) {
                    Intent intent = new Intent(this, TrainingTimeService.class);
                    intent.putExtra("model", mAdapter.getModel());
                    startService(intent);
                }
                break;

            case R.id.actual_training_finish_action:
                stopService(new Intent(this, TrainingTimeService.class));
                break;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<ProgramTraining> loader) {

    }

    @Override
    public void onStepClick(View view) {
        if (mFragment == null)
            mFragment = (BlankFragment) getSupportFragmentManager().getFragments().get(0);

        int position = view == null ? 0 : mStepsView.getChildAdapterPosition(view);
        mAdapter.setActivePosition(position);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
