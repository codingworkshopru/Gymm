package ru.codingworkshop.gymm.ui.info;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragmentViewModelTest;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalViewModelTest;
import ru.codingworkshop.gymm.ui.info.statistics.plot.StatisticsPlotViewModelTest;

/**
 * Created by Radik on 12.12.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExerciseInfoFragmentViewModelTest.class,
        StatisticsJournalViewModelTest.class,
        StatisticsPlotViewModelTest.class
})
public class InfoUiSuite {
}
