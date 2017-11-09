package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingViewModel;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingActivityTest {
    @Mock private ProgramTrainingViewModel vm;
    @Mock private ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingFragment programTraining;
    private MutableLiveData<Boolean> loaded = new MutableLiveData<>();

    @Rule
    public ActivityTestRule<ProgramTrainingActivity> activityTestRule =
            new ActivityTestRule<ProgramTrainingActivity>(ProgramTrainingActivity.class)
            {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ProgramTrainingActivity.class);
                    intent.putExtra(ProgramTrainingFragment.PROGRAM_TRAINING_ID_KEY, 1L);
                    return intent;
                }
            };

    @Before
    public void setUp() throws Exception {
        loaded.postValue(true);
    }

    @Test
    public void programTrainingNameAndExercisesTest() throws Exception {
        onView(withId(R.id.programTrainingName)).check(matches(withText("foo")));
    }
}
