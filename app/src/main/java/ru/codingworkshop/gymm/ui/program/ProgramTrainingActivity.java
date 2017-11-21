package ru.codingworkshop.gymm.ui.program;

import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;

public class ProgramTrainingActivity extends AppCompatActivity {

    @VisibleForTesting
    ProgramTrainingFragment programTrainingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_training);

        programTrainingFragment = ProgramTrainingFragment.newInstance();
        programTrainingFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.programTrainingFragmentContainer, programTrainingFragment, "TAG")
                .commit();
    }
}
