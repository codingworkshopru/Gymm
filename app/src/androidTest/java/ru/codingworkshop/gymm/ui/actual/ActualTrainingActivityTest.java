package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityIsolated;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.any;
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
            intent.putExtra("foo", 1L);
            return intent;
        }
    };

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ActualTrainingViewModel vm;

    private MutableLiveData<Boolean> liveTrue = new MutableLiveData<>();

    private ActualTrainingTree buildTree() {
        ActualTrainingTree tree = new ActualTrainingTree();
        tree.setProgramTraining(Models.createProgramTraining(1L, "foo"));
        ActualExerciseNode node = new ActualExerciseNode();
        ProgramExerciseNode programExerciseNode = new ImmutableProgramExerciseNode(Models.createProgramExercise(2L, 1, 100L, false));
        programExerciseNode.setExercise(Models.createExercise(100L, "my cool exercise"));
        node.setProgramExerciseNode(programExerciseNode);
        tree.addChild(node);

        return tree;
    }

    @Before
    public void setUp() throws Exception {
        when(vm.getActualTrainingTree()).thenReturn(buildTree());
    }

    @Test
    public void startTraining() throws Exception {
        liveTrue.postValue(true);
        onView(allOf(withParent(withId(R.id.actualTrainingToolbar)))).check(matches(withText("foo")));
        onView(withText("my cool exercise")).check(matches(isDisplayed()));
        onView(withText("1")).check(matches(isDisplayed()));
    }
}
