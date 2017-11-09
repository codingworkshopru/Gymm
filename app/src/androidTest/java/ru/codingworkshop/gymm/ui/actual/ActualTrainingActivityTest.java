package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityInjectedFragments;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.both;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 19.09.2017 as part of the Gymm project.
 */

public class ActualTrainingActivityTest {
    @Rule
    public ActivityTestRule<ActualTrainingActivityInjectedFragments> activityTestRule =
            new ActivityTestRule<ActualTrainingActivityInjectedFragments>(
                    ActualTrainingActivityInjectedFragments.class) {

                @Override
                protected void beforeActivityLaunched() {
                    setUp();
                }
            };

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ActualTrainingViewModel vm;
    @InjectMocks private ActualExercisesFragment fragment;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ActualTrainingActivityInjectedFragments.fragment = fragment;
        when(viewModelFactory.create(any())).thenReturn(vm);
        final ActualTrainingTree tree = TreeBuilders.buildHalfPopulatedTree(3);
        tree.getChildren().get(2).getProgramExerciseNode().getChildren().get(0).setSecondsForRest(5);
        when(vm.getActualTrainingTree()).thenReturn(tree);
        when(vm.createActualSet(anyInt(), any(ActualSet.class))).thenAnswer(invocation -> {
            int index = invocation.getArgument(0);
            ActualSet set = invocation.getArgument(1);
            vm.getActualTrainingTree().getChildren().get(index).addChild(set);
            return LiveDataUtil.getLive(13L);
        });
    }

    @After
    public void tearDown() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        final Intent intent = new Intent(context, TrainingForegroundService.class);
        context.stopService(intent);
    }

    @Test
    public void exercisesFragmentShowsTest() throws Exception {
        onView(withText("exercise100")).check(matches(isDisplayed()));
    }

    @Test
    public void startRestTest() throws Exception {
        onView(withId(R.id.actualExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(both(withId(R.id.actualSetDoneButton)).and(isDisplayed())).perform(click());
        onView(withId(R.id.restTimeLeft)).check(matches(isDisplayed()));
    }

    @Test
    public void stopRestTest() throws Exception {
        onView(withId(R.id.actualExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(both(withId(R.id.actualSetDoneButton)).and(isDisplayed())).perform(click());
        onView(withId(R.id.restTimeLeft)).check(matches(isDisplayed()));
        onView(withId(R.id.restStopButton)).perform(click());
        String numberOfSets = InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2);
        onView(withText(numberOfSets)).check(matches(isDisplayed()));
    }
}
