package ru.codingworkshop.gymm.program.activity.exercise;

import android.app.DialogFragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;
import ru.codingworkshop.gymm.program.AlertDialogFragment;
import ru.codingworkshop.gymm.program.ProgramAdapter;
import ru.codingworkshop.gymm.program.ProgramUtils;
import ru.codingworkshop.gymm.program.activity.exercise.picker.MusclesActivity;

import static ru.codingworkshop.gymm.info.exercise.ExerciseInfoActivity.EXERCISE_ARG;

public class ProgramExerciseActivity extends AppCompatActivity
        implements ActionMode.Callback,
        SetInputDialog.SetInputDialogListener,
        ProgramAdapter.ListItemActionListener
{
    private static final String TAG = ProgramExerciseActivity.class.getSimpleName();

    private ProgramExercise mModel;
    private ProgramAdapter<SetViewHolder> mSetsAdapter;
    private RecyclerView mSetsView;
    private ActivityProgramExerciseBinding mBinding;

    public static final String EXERCISE_MODEL_KEY = ProgramExercise.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_program_exercise);

        // restore model from intent or bundle
        Intent intent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(EXERCISE_MODEL_KEY))
            mModel = savedInstanceState.getParcelable(EXERCISE_MODEL_KEY);
        else if (intent != null && intent.hasExtra(EXERCISE_MODEL_KEY))
            mModel = intent.getParcelableExtra(EXERCISE_MODEL_KEY);
        else
            mModel = new ProgramExercise();

        setSupportActionBar((Toolbar) findViewById(R.id.program_exercise_toolbar));
        // setting "up" button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        // recycler view
        mSetsView = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        mSetsView.setLayoutManager(new LinearLayoutManager(this));
        mSetsView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSetsAdapter = new ProgramAdapter<>(this, new SetViewHolderFactory());
        mSetsView.setAdapter(mSetsAdapter);
        mBinding.setAdapter(mSetsAdapter);
        mBinding.setExercise(mModel);
        mSetsAdapter.setModel(mModel);

        findViewById(R.id.program_exercise_name_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExercisePick(v);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXERCISE_MODEL_KEY, mModel);
    }

    public void onAddButtonClick(View view) {
        // TODO анимация FAB -> Dialog
        DialogFragment setInputDialog = new SetInputDialog();
        setInputDialog.show(getFragmentManager(), SetInputDialog.class.getSimpleName());
    }

    @Override
    public void onProgramSetReturn(ProgramSet model) {
        int modelPosition = model.getOrder();
        if (modelPosition == -1) {
            mModel.addChild(model);
            mSetsAdapter.notifyItemInserted(mSetsAdapter.getItemCount() - 1);
        } else {
            mModel.setChild(modelPosition, model);
            mSetsAdapter.notifyItemChanged(modelPosition);
        }
    }

    @Override
    public void onBackPressed() {
        if (mModel.isChanged()) {
            AlertDialogFragment.OnDialogButtonClickListener listener = new AlertDialogFragment.OnDialogButtonClickListener() {
                @Override
                public void onButtonClick(boolean positive) {
                    if (positive)
                        finishActivity(false);
                }
            };

            ProgramUtils.showAlert(this, listener, 0, R.string.save_changes_question, R.string.ok_button_text, R.string.cancel_button_text);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED)
            return;

        Exercise returnedExercise = data.getParcelableExtra(EXERCISE_ARG);
        mModel.setExercise(returnedExercise);
        mBinding.setExercise(mModel);
    }

    public void onExercisePick(View v) {
        Intent intent = new Intent(this, MusclesActivity.class);
        startActivityForResult(intent, 0);
    }

    private void finishActivity(boolean save) {
        if (save) {
            if (mModel.getExercise() == null || mModel.getExercise().isPhantom()) {
                ProgramUtils.showAlert(this, new AlertDialogFragment.OnDialogButtonClickListener() {
                    @Override
                    public void onButtonClick(boolean positive) {
                        if (positive)
                            onExercisePick(null);
                    }
                }, 0, R.string.program_exercise_activity_exercise_not_selected_message, R.string.add_button_text, R.string.cancel_button_text);
            } else if (mModel.childrenCount() == 0) {

                AlertDialogFragment.OnDialogButtonClickListener listener = new AlertDialogFragment.OnDialogButtonClickListener() {
                    @Override
                    public void onButtonClick(boolean positive) {
                        if (positive)
                            onAddButtonClick(null);
                    }
                };

                ProgramUtils.showAlert(this, listener, 0, R.string.program_exercise_activity_empty_list_dialog_message, R.string.add_button_text, R.string.cancel_button_text);
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXERCISE_MODEL_KEY, mModel);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    private void doActionModeChangeAnimation(final boolean actionModeOn) {
        int animationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.program_exercise_add_set);
        Animation iconsAnimation;
        if (actionModeOn) {
            fab.hide();
            iconsAnimation = AnimationUtils.makeInAnimation(this, false);
        } else {
            fab.show();
            iconsAnimation = AnimationUtils.makeOutAnimation(this, true);
        }

        iconsAnimation.setDuration(animationDuration);

        for (int i = 0; i < mSetsAdapter.getItemCount(); i++) {
            View view = mSetsView.getChildAt(i);
            if (view != null)
                view.findViewById(R.id.program_exercise_list_item_icons).startAnimation(iconsAnimation);
        }
    }

    // action mode (in fact edit mode)
    //-----------------------------------------
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        doActionModeChangeAnimation(true);
        mSetsAdapter.attachItemTouchHelper(mSetsView);
        mSetsAdapter.setEditMode(true);
        findViewById(R.id.program_exercise_name_layout).setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mSetsAdapter.attachItemTouchHelper(null);
        doActionModeChangeAnimation(false);
        mSetsAdapter.setEditMode(false);
        findViewById(R.id.program_exercise_name_layout).setVisibility(View.VISIBLE);
    }
    //-----------------------------------------

    // recycler view's items actions
    //-----------------------------------------
    @Override
    public void onListItemClick(View view) {
        Bundle arguments = new Bundle(1);
        ProgramSet set = mModel.getChild(mSetsView.getChildAdapterPosition(view));
        arguments.putParcelable(SetInputDialog.DIALOG_MODEL_KEY, set);

        DialogFragment setInputDialog = new SetInputDialog();
        setInputDialog.setArguments(arguments);
        setInputDialog.show(getFragmentManager(), SetInputDialog.class.getSimpleName());
    }

    @Override
    public boolean onListItemLongClick(View view) {
        startSupportActionMode(this);
        return true;
    }

    @Override
    public void onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        ProgramUtils.showSnackbar(
                mSetsView,
                R.string.program_exercise_activity_set_deleted_message,
                R.string.cancel_button_text,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSetsAdapter.notifyItemInserted(mModel.restoreLastRemoved());
                    }
                });
    }
    //-----------------------------------------

    // menu
    //-----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.program_training_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                finishActivity(true);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------
}
