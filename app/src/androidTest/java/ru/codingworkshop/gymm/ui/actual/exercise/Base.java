package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.ViewModelProvider;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.actual.Matchers.currentPageItem;

/**
 * Created by Радик on 01.10.2017 as part of the Gymm project.
 */

public abstract class Base {
    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock ActualExercisesFragment.ActualExercisesCallback callback;
    @Mock ActualTrainingViewModel vm;

    @InjectMocks ActualExercisesFragment fragment;

    ActualTrainingTree fakeTree;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.actualExerciseList);
        InstrumentationRegistry
                .getTargetContext()
                .getSharedPreferences(ActualExercisesFragment.PREFS_NAME, 0)
                .edit()
                .clear()
                .commit();
        when(viewModelFactory.create(any())).thenReturn(vm);
        beforeFragmentSet();
        setArguments(fragment);
        activityTestRule.getActivity().setFragment(fragment);
    }

    abstract void beforeFragmentSet() throws Exception;
    void setArguments(Fragment fragment) {

    }

    void setFakeTree(ActualTrainingTree tree) {
        fakeTree = tree;
        when(vm.getActualTrainingTree()).thenReturn(fakeTree);
    }

    void selectStepAtPosition(int position) {
        onView(withId(R.id.actualExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    void checkWeight(double weight) {
        onView(currentPageItem(R.id.actualSetWeightEditText)).check(matches(withText(Double.toString(weight))));
    }

    void checkRepsCount(int repsCount) {
        onView(currentPageItem(R.id.actualSetRepsCountEditText)).check(matches(withText(Integer.toString(repsCount))));
    }
}
