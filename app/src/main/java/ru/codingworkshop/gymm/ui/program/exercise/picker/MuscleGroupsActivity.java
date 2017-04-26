package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import io.requery.query.Result;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.ModelLoader;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.info.exercise.ExerciseInfoActivity;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseActivity;

final public class MuscleGroupsActivity extends AppCompatActivity
        implements MuscleGroupsHumanFragment.OnMuscleGroupSelectListener,
        ExercisePickerAdapter.OnExerciseClickListener,
        ModelLoader.ModelLoaderCallbacks<MuscleGroup>
{

//    private EntityDataStore<Persistable> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        new ModelLoader<>(this, MuscleGroup.class, this).queryAll();

//        data = ((App) getApplication()).getData();

//        List<MuscleGroup> muscleGroups = data
//                .select(MuscleGroup.class)
//                .get()
//                .toList();
    }

    @Override
    public void onMuscleGroupSelect(MuscleGroup muscleGroup) {
        ExercisePickerFragment f = ExercisePickerFragment.newInstance(muscleGroup);
        f.show(getSupportFragmentManager(), ExercisePickerFragment.class.getSimpleName());
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        Intent data = new Intent();
        data.putExtra(ProgramExerciseActivity.EXERCISE_MODEL, exercise);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onExerciseInfoClick(Exercise exercise) {
        Intent exerciseInfoIntent = new Intent(this, ExerciseInfoActivity.class);
        exerciseInfoIntent.putExtra(ExerciseInfoActivity.EXERCISE_ID, exercise.getId());
        startActivity(exerciseInfoIntent);
    }

    @Override
    public void onModelLoadFinished(Result<MuscleGroup> data) {


        PagerAdapter adapter = new MuscleGroupsPagerAdapter(this, data.toList());

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
