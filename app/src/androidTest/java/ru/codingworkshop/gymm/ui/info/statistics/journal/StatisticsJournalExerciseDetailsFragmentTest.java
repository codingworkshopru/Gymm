package ru.codingworkshop.gymm.ui.info.statistics.journal;

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

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.util.RecyclerViewItemMatcher.itemAtPosition;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsJournalExerciseDetailsFragmentTest {
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private StatisticsJournalViewModel vm;
    @InjectMocks private StatisticsJournalExerciseDetailsFragment fragment;

    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule = new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Before
    public void setUp() throws Exception {
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.statisticsExerciseDetailsJournalList);

        ActualSet set = new ActualSet(12L, 10);
        set.setWeight(2.125);

        ImmutableActualExerciseNode node = new ImmutableActualExerciseNode();
        node.setChildren(Arrays.asList(set, set));
        when(vm.getCurrentExerciseNode()).thenReturn(node);

        when(viewModelFactory.create(any())).thenReturn(vm);
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void headerTest() {
        onView(itemAtPosition(R.id.statisticsJournalExerciseDetailsListHeaderSet, 0)).check(matches(withText(R.string.set)));
        onView(itemAtPosition(R.id.statisticsJournalExerciseDetailsListHeaderReps, 0)).check(matches(withText(R.string.reps)));
        onView(itemAtPosition(R.id.statisticsJournalExerciseDetailsListHeaderWeight, 0)).check(matches(withText(R.string.weight)));
    }

    @Test
    public void setsTest() {
        onView(itemAtPosition(R.id.statisticsJournalSetIndex, 1)).check(matches(withText("1")));
        onView(itemAtPosition(R.id.statisticsJournalSetRepsCount, 1)).check(matches(withText("10")));
        onView(itemAtPosition(R.id.statisticsJournalSetWeight, 1)).check(matches(withText(String.format("%.2f", 2.125))));
    }

    @Test
    public void totalsTest() {
        onView(itemAtPosition(R.id.statisticsJournalExerciseDetailsListFooterTotalReps, 3)).check(matches(withText("20")));
        onView(itemAtPosition(R.id.statisticsJournalExerciseDetailsListFooterTotalWeight, 3)).check(matches(withText(String.format("%.2f", 4.25))));
    }
}