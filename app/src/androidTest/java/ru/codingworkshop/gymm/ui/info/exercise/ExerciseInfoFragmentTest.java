package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.ViewModelProvider;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.ExerciseDifficulty;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.Matchers.hasBackground;

/**
 * Created by Radik on 20.11.2017.
 */

public class ExerciseInfoFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ExerciseInfoFragmentViewModel vm;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(viewModelFactory.create(any())).thenReturn(vm);
    }

    @Test
    public void checkTextLabels() throws Exception {
        initFragmentWithExercise(createExercise());
        onView(withId(R.id.exerciseInfoName)).check(matches(withText("foo")));
        onView(withId(R.id.exerciseInfoMainMuscleGroup)).check(matches(withText("main muscle group")));
        onView(withId(R.id.exerciseInfoSecondaryMuscleGroups)).check(matches(withText("muscle group 201 • muscle group 202")));
        onView(withId(R.id.exerciseInfoSteps)).check(matches(withText("steps")));
        onView(withId(R.id.exerciseInfoAdvices)).check(matches(withText("advices")));
        onView(withId(R.id.exerciseInfoCaution)).check(matches(withText("caution")));
        onView(withId(R.id.exerciseInfoVariations)).check(matches(withText("variations")));
    }

    @Test
    public void checkDifficultyPicture() throws Exception {
        initFragmentWithExercise(createExercise());
        onView(withId(R.id.exerciseInfoDifficulty)).check(matches(hasBackground(R.drawable.ic_signal_cellular_2_bar_primary_24dp)));
    }

    @Test
    public void withoutYouTubeVideoTest() throws Exception {
        initFragmentWithExercise(createExerciseWithoutYouTubeVideo());
        onView(withId(R.id.exerciseInfoDifficulty)).check(matches(isDisplayed()));
    }

    @Test
    public void onlyStepsVisibleTest() throws Exception {
        initFragmentWithExercise(createExerciseWithStepsOnly());

        onView(withId(R.id.exerciseInfoStepsLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.exerciseInfoAdviceLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.exerciseInfoCautionLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.exerciseInfoVariationsLayout)).check(matches(not(isDisplayed())));
    }

    private void initFragmentWithExercise(Exercise exercise) {
        ExerciseNode exerciseNode = createExerciseNode(exercise);
        when(vm.load(100L)).thenReturn(LiveDataUtil.getLive(exerciseNode));

        ExerciseInfoFragment fragment = ExerciseInfoFragment.newInstance(100L);
        fragment.viewModelFactory = viewModelFactory;

        fragment.show(activityTestRule.getActivity().getSupportFragmentManager(), "");
    }

    private ExerciseNode createExerciseNode(Exercise exercise) {
        ExerciseNode node = new ExerciseNode();
        node.setParent(exercise);
        node.setPrimaryMuscleGroup(Models.createMuscleGroup(200L, "main muscle group"));
        node.setChildren(Models.createMuscleGroups(201L, 202L));

        return node;
    }

    private Exercise createExercise() {
        Exercise exercise = Models.createExercise(100L, "foo");

        exercise.setPrimaryMuscleGroupId(200L);
        exercise.setDifficulty(ExerciseDifficulty.INTERMEDIATE);
        exercise.setYouTubeVideo("b4mBmi1QNF0");
        exercise.setWithWeight(true);

        exercise.setSteps("steps");
        exercise.setAdvices("advices");
        exercise.setCaution("caution");
        exercise.setVariations("variations");

        return exercise;
    }

    private Exercise createExerciseWithoutYouTubeVideo() {
        Exercise exercise = createExercise();

        exercise.setYouTubeVideo(null);

        return exercise;
    }

    private Exercise createExerciseWithStepsOnly() {
        Exercise exercise = createExercise();

        exercise.setAdvices(null);
        exercise.setVariations(null);
        exercise.setCaution(null);

        return exercise;
    }
}