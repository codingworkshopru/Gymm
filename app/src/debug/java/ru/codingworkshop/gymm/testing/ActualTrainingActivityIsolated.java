package ru.codingworkshop.gymm.testing;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;

import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;

/**
 * Created by Радик on 29.08.2017 as part of the Gymm project.
 */

public class ActualTrainingActivityIsolated extends ActualTrainingActivity {
    public static ViewModelProvider.Factory viewModelFactoryMock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModelFactory = viewModelFactoryMock;
        super.onCreate(savedInstanceState);
    }
}
