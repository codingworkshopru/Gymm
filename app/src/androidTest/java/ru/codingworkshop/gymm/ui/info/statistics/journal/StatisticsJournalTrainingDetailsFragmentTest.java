package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.ViewModelProvider;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.util.RecyclerViewItemMatcher.itemAtPosition;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsJournalTrainingDetailsFragmentTest {
    @Mock private StatisticsJournalViewModel vm;
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @InjectMocks private StatisticsJournalTrainingDetailsFragment fragment;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Before
    public void setUp() throws Exception {
        ImmutableActualTrainingTree tree = new ImmutableActualTrainingTree();

        ActualTraining actualTraining = new ActualTraining(1L, "foo");
        actualTraining.setStartTime(new GregorianCalendar(2000, 0, 1, 15, 5, 30).getTime());
        actualTraining.setFinishTime(new GregorianCalendar(2000, 0, 1, 15, 25, 0).getTime());
        actualTraining.setComment("comment");
        tree.setParent(actualTraining);

        List<ActualExercise> actualExercises = Models.createActualExercises(12L);
        List<ImmutableActualExerciseNode> nodes = actualExercises.stream()
                .map(ex -> {
                    ActualSet actualSet = Models.createActualSet(13L, 12L, 1);

                    ActualSet actualSetWithoutWeight = Models.createActualSet(14L, 12L, 1);

                    ImmutableActualExerciseNode node = new ImmutableActualExerciseNode();
                    node.setParent(ex);
                    node.setChildren(Arrays.asList(actualSet, actualSetWithoutWeight));
                    node.setVolume(2.125);

                    return node;
                })
                .collect(Collectors.toList());
        tree.setChildren(nodes);

        when(viewModelFactory.create(StatisticsJournalViewModel.class)).thenReturn(vm);
        when(vm.getActualTrainingTree()).thenReturn(tree);
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void treeCache() {
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        activityTestRule.getActivity().replaceFragment(fragment);
    }

    @Test
    public void actualTraining() {
        onView(withId(R.id.statisticsTrainingDetailsJournalDateTime)).check(matches(withText("01.01.2000 15:05")));
        onView(withId(R.id.statisticsTrainingDetailsJournalName)).check(matches(withText("foo")));
        onView(withId(R.id.statisticsTrainingDetailsJournalDuration)).check(matches(withText("00:19:30")));
        onView(withId(R.id.statisticsTrainingDetailsJournalComment)).check(matches(withText("comment")));
    }

    @Test
    public void actualExercises() {
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.statisticsTrainingDetailsJournalExerciseList);

        onView(itemAtPosition(R.id.statisticsTrainingDetailsJournalExerciseItem, 0)).check(matches(withText("exercise 1")));
        onView(itemAtPosition(R.id.statisticsTrainingDetailsJournalExerciseVolume, 0)).check(matches(withText(String.format("%.2f", 2.125))));
    }
}
