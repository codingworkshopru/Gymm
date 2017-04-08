package ru.codingworkshop.gymm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.program.activity.training.TrainingAsyncLoader;
import ru.codingworkshop.gymm.service.TrainingTimeService;

public class ActualTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ProgramTraining>,
        ActualTrainingAdapter.OnActiveStepChangeListener,
        BlankFragment.OnFragmentInteractionListener
{
    private static final String TAG = ActualTrainingActivity.class.getSimpleName();

    private ActualTrainingAdapter mAdapter;
    private BlankFragment mFragment;
    private FrameLayout mFragmentContainer;
    private @IdRes int mFragmentContainerId = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? 1 : View.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_training);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list_view);
        mAdapter = new ActualTrainingAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setActiveViewHolder(mAdapter.findViewHolder(view));
            }
        });

        mFragment = new BlankFragment();

        getSupportLoaderManager().initLoader(TrainingAsyncLoader.LOADER_TRAINING_LOAD, getIntent().getExtras(), this);

        mFragmentContainer = new FrameLayout(this);
        mFragmentContainer.setId(mFragmentContainerId);
    }

    @Override
    public Loader<ProgramTraining> onCreateLoader(int id, Bundle args) {
        return new TrainingAsyncLoader(this, id, args);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<ProgramTraining> loader) {

    }

    @Override
    public void onActiveStepChange(View oldActiveView, View newActiveView) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (oldActiveView != null) {
            transaction.detach(mFragment);
            ((ViewGroup) oldActiveView).removeView(mFragmentContainer);
        } else {
            transaction.add(mFragmentContainer.getId(), mFragment);
        }

        @IdRes int aboveViewId = R.id.linearLayout;

        ConstraintLayout newActiveConstraint = (ConstraintLayout) newActiveView;

        newActiveConstraint.addView(mFragmentContainer);
        ConstraintSet s = new ConstraintSet();
        s.constrainHeight(mFragmentContainerId, ConstraintSet.WRAP_CONTENT);
        s.constrainWidth(mFragmentContainerId, 0);
        s.connect(mFragmentContainerId, ConstraintSet.LEFT, aboveViewId, ConstraintSet.LEFT);
        s.connect(mFragmentContainerId, ConstraintSet.RIGHT, aboveViewId, ConstraintSet.RIGHT);
        s.connect(mFragmentContainerId, ConstraintSet.TOP, aboveViewId, ConstraintSet.BOTTOM);
        s.applyTo(newActiveConstraint);

        transaction.attach(mFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
