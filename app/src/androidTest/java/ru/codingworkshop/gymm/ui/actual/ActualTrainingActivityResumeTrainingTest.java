package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityIsolated;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 17.09.2017 as part of the Gymm project.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActualTrainingActivityResumeTrainingTest extends ActualTrainingActivityTest {
    @Rule
    public ActivityTestRule<ActualTrainingActivityIsolated> activityRule = new ActivityTestRule<ActualTrainingActivityIsolated>(ActualTrainingActivityIsolated.class) {
        @Override
        protected void beforeActivityLaunched() {
            vm = mock(ActualTrainingViewModel.class);
            ViewModelProvider.Factory viewModelFactory = mock(ViewModelProvider.Factory.class);
            ActualTrainingActivityIsolated.viewModelFactoryMock = viewModelFactory;
            when(viewModelFactory.create(any())).thenReturn(vm);
            when(vm.loadTraining(11L)).thenReturn(liveTrue);
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ActualTrainingActivityIsolated.class);
            intent.putExtra(ActualTrainingActivity.EXTRA_ACTUAL_TRAINING_ID, 11L);
            return intent;
        }
    };

    @Mock private ActualTrainingViewModel vm;

    private MutableLiveData<Boolean> liveTrue = new MutableLiveData<>();

    private ActualTrainingTree tree;

    private void buildTree(int size) {
        tree.setProgramTraining(Models.createProgramTraining(1L, "foo"));

        tree.setChildren(Lists.transform(Models.createProgramExercises(size), pe -> {
            ProgramExerciseNode peNode = new ImmutableProgramExerciseNode(pe);

            peNode.setChildren(Models.createProgramSets(pe.getId(), 2));

            final long exerciseId = peNode.getExerciseId();
            Exercise exercise = Models.createExercise(exerciseId, "exercise" + exerciseId);
            exercise.setWithWeight(true);
            peNode.setExercise(exercise);

            ActualExerciseNode node = new ActualExerciseNode();
            final ActualExercise actualExercise = Models.createActualExercise(
                    10L + pe.getId(), exercise.getName(), 11L, pe.getId());
            node.setParent(actualExercise);
            node.setProgramExerciseNode(peNode);
            return node;
        }));

        final ActualSet actualSet = Models.createActualSet(13L, 12L, 99);
        actualSet.setWeight(9.9);
        tree.getChildren().get(0).setChildren(Lists.newArrayList(actualSet));

        liveTrue.postValue(true);
    }

    @Before
    public void setUp() throws Exception {
        tree = new ActualTrainingTree();
        when(vm.getActualTrainingTree()).thenReturn(tree);
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.actualExerciseSteps);

        buildTree(2);
    }

    @Test
    public void resumeTrainingTest() throws Exception {
        selectStepAtPosition(0);

        checkRepsCount(99);
        checkWeight(9.9);
    }

    @Test
    public void finishTrainingTest() throws Exception {
        onView(withId(R.id.action_finish_training)).perform(click());

        final String areYouSureMessage = InstrumentationRegistry.getTargetContext()
                .getString(R.string.actual_training_activity_are_you_sure_message);
        onView(withText(areYouSureMessage)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        verify(vm).finishTraining();
    }
}
