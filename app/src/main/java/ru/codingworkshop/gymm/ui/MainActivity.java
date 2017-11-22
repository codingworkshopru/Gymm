package ru.codingworkshop.gymm.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.databinding.ActivityMainListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;

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
        RecyclerView programTrainingsView = findViewById(R.id.rv_test_main);
        ListItemListeners listItemListeners = new ListItemListeners(R.layout.activity_main_list_item);
        programTrainingsView.setAdapter(new ClickableBindingListAdapter<ProgramTraining, ActivityMainListItemBinding>(programTrainings, listItemListeners) {
            @Override
            protected void bind(ActivityMainListItemBinding binding, ProgramTraining item) {
                binding.setProgramTraining(item);
            }
        });
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
                Intent startProgramTrainingActivity = new Intent(this, ProgramTrainingActivity.class);
                startActivity(startProgramTrainingActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------
}
