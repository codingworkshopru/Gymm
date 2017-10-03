package ru.codingworkshop.gymm.testing;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;

/**
 * Created by Радик on 02.10.2017 as part of the Gymm project.
 */

public class ActualTrainingActivityInjectedFragments extends ActualTrainingActivity {
    public static Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actualExercisesFragment = fragment;
        super.onCreate(savedInstanceState);
    }
}
