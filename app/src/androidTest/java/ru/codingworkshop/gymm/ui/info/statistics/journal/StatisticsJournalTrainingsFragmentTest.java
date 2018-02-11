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

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsJournalTrainingsFragmentTest {
    private static final Date START_TIME = new GregorianCalendar(2000, 0, 1, 15, 5, 30).getTime();

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private StatisticsJournalViewModel vm;

    @InjectMocks private StatisticsJournalTrainingsFragment fragment;

    @Before
    public void setUp() throws Exception {
        when(viewModelFactory.create(any())).thenReturn(vm);
        ActualTraining actualTraining = new ActualTraining(1L, "foo");
        actualTraining.setStartTime(START_TIME);
        when(vm.getActualTrainings()).thenReturn(LiveDataUtil.getLive(Collections.singletonList(actualTraining)));

        RecyclerViewItemMatcher.setRecyclerViewId(R.id.statisticsJournalTrainings);

        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void checkListItem() {
        onView(RecyclerViewItemMatcher.itemAtPosition(R.id.statisticsJournalTrainingsDate, 0))
                .check(matches(withText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(START_TIME))));
        onView(RecyclerViewItemMatcher.itemAtPosition(R.id.statisticsJournalTrainingsName, 0))
                .check(matches(withText("foo")));
    }
}