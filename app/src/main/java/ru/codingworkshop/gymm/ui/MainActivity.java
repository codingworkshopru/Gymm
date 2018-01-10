package ru.codingworkshop.gymm.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.databinding.ActivityMainListItemBinding;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalActivity;
import ru.codingworkshop.gymm.ui.info.statistics.plot.StatisticsPlotActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;

import static ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity.PROGRAM_TRAINING_ID_KEY;

public class MainActivity extends AppCompatActivity {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        viewModel = viewModelFactory.create(MainActivityViewModel.class);
        viewModel.load().observe(this, this::onProgramTrainingLoaded);
    }

    private void onProgramTrainingLoaded(List<ProgramTraining> programTrainings) {
        ListItemListeners listItemListeners = new ListItemListeners(R.layout.activity_main_list_item)
                .setOnClickListener(this::onProgramTrainingClick);

        RecyclerView programTrainingsView = findViewById(R.id.rv_test_main);
        programTrainingsView.setAdapter(new ClickableBindingListAdapter<ProgramTraining, ActivityMainListItemBinding>(programTrainings, listItemListeners) {
            @Override
            protected void bind(ActivityMainListItemBinding binding, ProgramTraining item) {
                binding.setProgramTraining(item);
                binding.mainActivityPopupMenuButton.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                    popupMenu.inflate(R.menu.program_training_item_popup_menu);
                    popupMenu.show();
                });
                binding.button.setOnClickListener(v -> {
                    Intent startTraining = new Intent(MainActivity.this, ActualTrainingActivity.class);
                    startTraining.putExtra(ActualExercisesFragment.EXTRA_PROGRAM_TRAINING_ID, item.getId());
                    startActivity(startTraining);
                });
            }
        });
    }

    private void onProgramTrainingClick(View v) {
        ActivityMainListItemBinding b = DataBindingUtil.findBinding(v);
        long programTrainingId = b.getProgramTraining().getId();
        startActivity(programTrainingActivityIntent(programTrainingId));
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
                startActivity(programTrainingActivityIntent());
                return true;

            case R.id.action_show_plot_statistics:
                startActivity(new Intent(this, StatisticsPlotActivity.class));
                return true;

            case R.id.action_show_journal_statistics:
                startActivity(new Intent(this, StatisticsJournalActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------

    private Intent programTrainingActivityIntent(long programTrainingId) {
        Intent startProgramTrainingActivity = programTrainingActivityIntent();
        startProgramTrainingActivity.putExtra(PROGRAM_TRAINING_ID_KEY, programTrainingId);
        return startProgramTrainingActivity;
    }

    private Intent programTrainingActivityIntent() {
        return new Intent(this, ProgramTrainingActivity.class);
    }
}
