package ru.codingworkshop.gymm;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.program.activity.training.TrainingAsyncLoader;

public class ActualTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ProgramTraining>,
        ActualTrainingAdapter.OnActiveStepChangeListener,
        BlankFragment.OnFragmentInteractionListener
{

    private ActualTrainingAdapter mAdapter;
    private BlankFragment mFragment;

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
    public void onLoaderReset(Loader<ProgramTraining> loader) {

    }

    @Override
    public void onActiveStepChange(View view) {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, mFragment, null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
