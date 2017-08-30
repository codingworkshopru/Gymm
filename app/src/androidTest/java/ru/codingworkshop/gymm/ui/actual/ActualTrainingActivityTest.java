package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityIsolated;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 28.08.2017 as part of the Gymm project.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActualTrainingActivityTest {

    @Rule
    public ActivityTestRule<ActualTrainingActivityIsolated> activityRule = new ActivityTestRule<ActualTrainingActivityIsolated>(ActualTrainingActivityIsolated.class) {
        @Override
        protected void beforeActivityLaunched() {
            MockitoAnnotations.initMocks(ActualTrainingActivityTest.this);

            ActualTrainingActivityIsolated.viewModelFactoryMock = ActualTrainingActivityTest.this.viewModelFactory;
            when(viewModelFactory.create(any())).thenReturn(vm);
            when(vm.startTraining(1L)).thenReturn(liveTrue);
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ActualTrainingActivityIsolated.class);
            intent.putExtra(ActualTrainingActivity.EXTRA_PROGRAM_TRAINING_ID, 1L);
            return intent;
        }
    };

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ActualTrainingViewModel vm;

    private MutableLiveData<Boolean> liveTrue = new MutableLiveData<>();

    private ActualTrainingTree tree;

    private void buildTree(int size) {
        List<ActualExerciseNode> actualExerciseNodes = Models.createProgramExercises(size).stream().map(pe -> {
            ActualExerciseNode node = new ActualExerciseNode();
            ProgramExerciseNode peNode = new ImmutableProgramExerciseNode(pe);
            final long exerciseId = peNode.getExerciseId();
            peNode.setExercise(Models.createExercise(exerciseId, "exercise" + exerciseId));
            node.setProgramExerciseNode(peNode);
            return node;
        }).collect(Collectors.toList());
        tree.setProgramTraining(Models.createProgramTraining(1L, "foo"));
        tree.setChildren(actualExerciseNodes);
    }

    @Before
    public void setUp() throws Exception {
        liveTrue.postValue(true);
        tree = new ActualTrainingTree();
        when(vm.getActualTrainingTree()).thenReturn(tree);
    }

    @Test
    public void startTrainingTest() throws Exception {
        buildTree(1);
        verify(vm).startTraining(1L);

        onView(allOf(withParent(withId(R.id.actualTrainingToolbar)))).check(matches(withText("foo")));
        onView(withText("exercise100")).check(matches(isDisplayed()));

        verify(vm).getActualTrainingTree();
    }

    @Test
    public void verifyItemCircle() throws Exception {
        buildTree(1);
        onView(withText("1")).check(matches(isDisplayed()));
        onView(withId(R.id.stepperItemCircle)).check(matches(isDisplayed()));
        onView(withId(R.id.stepperItemCircle)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
    }

    @Test
    public void verifyVerticalLinesOnOneItem() throws Exception {
        buildTree(1);
        onView(withId(R.id.stepperItemTopLine)).check(matches(not(isDisplayed())));
        onView(withId(R.id.stepperItemBottomLine)).check(matches(not(isDisplayed())));
    }

    @Test
    public void verifyVerticalLinesOnMultipleItems() throws Exception {
        buildTree(2);
        onView(both(withId(R.id.stepperItemTopLine)).and(isDisplayed()));
        onView(both(withId(R.id.stepperItemBottomLine)).and(isDisplayed()));
    }

    @Test
    public void verifyActiveStatusSet() throws Exception {
        buildTree(1);
        onView(withId(R.id.actualExerciseSteps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.stepperItemCircle)).check(matches(hasBackground(R.drawable.ic_circle_primary_24dp)));
        verify(vm).createActualExercise(0);
    }

    private static Matcher<View> hasBackground(@DrawableRes int drawableId) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified drawable");
            }

            @Override
            protected boolean matchesSafely(View item) {
                Drawable drawable = ContextCompat.getDrawable(item.getContext(), drawableId);

                Bitmap expected = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(expected);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                Bitmap actual = Bitmap.createBitmap(expected.getWidth(), expected.getHeight(), expected.getConfig());
                canvas.setBitmap(actual);
                item.getBackground().draw(canvas);

                return expected.sameAs(actual);
            }
        };
    }
}
