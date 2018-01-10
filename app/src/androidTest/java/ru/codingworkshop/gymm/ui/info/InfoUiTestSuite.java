package ru.codingworkshop.gymm.ui.info;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragmentTest;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalExerciseDetailsFragmentTest;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalTrainingDetailsFragmentTest;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalTrainingsFragmentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExerciseInfoFragmentTest.class,
        StatisticsJournalTrainingsFragmentTest.class,
        StatisticsJournalTrainingDetailsFragmentTest.class,
        StatisticsJournalExerciseDetailsFragmentTest.class
})
public class InfoUiTestSuite {
}
