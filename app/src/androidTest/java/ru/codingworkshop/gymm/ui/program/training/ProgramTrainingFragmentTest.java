package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.GeneralLocation.CENTER;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingFragmentTest {
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @InjectMocks private ProgramTrainingFragment fragment;

    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);
    private ProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.programExerciseList);
        when(viewModelFactory.create(any())).thenReturn(vm);
        tree = TreeBuilders.buildProgramTrainingTree(10);
        when(vm.getProgramTrainingTree()).thenReturn(tree);
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void trainingListTest() throws Exception {
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise100")));
        String setsCount = InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2);
        onView(exerciseAt(R.id.programSetsCount, 0)).check(matches(withText(setsCount)));
    }

    @Test
    public void removeExercise() throws Exception {
        assertEquals(10, tree.getChildren().size());
        onView(exerciseAt(R.id.programExerciseName, 0)).perform(swipeRight());
        assertEquals(9, tree.getChildren().size());
        onView(exerciseAt(R.id.programExerciseName, 0)).perform(swipeLeft());
        assertEquals(8, tree.getChildren().size());
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise102")));
    }

    @Test
    public void moveExercise() throws Exception {
        ProgramExerciseNode first = tree.getChildren().get(0);
        ProgramExerciseNode second = tree.getChildren().get(1);
        final CoordinatesProvider cp = view -> {
            float[] coords = new float[2];
            Rect globalRect = new Rect();
            view.getGlobalVisibleRect(globalRect);
            coords[0] = globalRect.centerX();
            coords[1] = globalRect.bottom + 2*globalRect.height();
            return coords;
        };
        final ViewAction viewAction = actionWithAssertions(new GeneralSwipeAction(Swipe.FAST,
                CENTER, cp, Press.THUMB));
        onView(exerciseAt(R.id.programExerciseReorderImage, 0)).perform(viewAction);
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise101")));
        assertEquals(first, tree.getChildren().get(1));
        assertEquals(second, tree.getChildren().get(0));
    }

    private Matcher<View> exerciseAt(@LayoutRes int id, int index) {
        return RecyclerViewItemMatcher.itemAtPosition(id, index);
    }
}
