package ru.codingworkshop.gymm.integration;

import android.arch.persistence.room.Room;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static ru.codingworkshop.gymm.db.initializer.DatabaseInitializer.DATABASE_NAME;
import static ru.codingworkshop.gymm.integration.Operation.*;

/**
 * Created by Radik on 22.11.2017.
 */

public class ProgramTrainingIntegrationTest {
    @Rule public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    private static GymmDatabase db;

    @BeforeClass
    public static void initDb() throws Exception {
        db = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), GymmDatabase.class, DATABASE_NAME)
                .build();
    }

    @AfterClass
    public static void closeDb() throws Exception {
        db.close();
    }

    @Before
    public void setUp() throws Exception {
        db.compileStatement("delete from ProgramTraining").execute();
    }

    @Test
    public void addProgramTraining() throws Exception {
        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");
    }

    private void addOneProgramTraining(String trainingName, String exerciseName) {
        addProgramTrainingClick();
        typeProgramTrainingName(trainingName);
        addProgramExerciseClick();
        pickExercise(db, exerciseName);
        addProgramSetClick();
        typeSet(1, 0, 0);
        checkSet(0, 1, 0, 0);
        saveProgramExerciseClick();
        checkProgramExercise(0, exerciseName, 1);
        saveProgramTrainingClick();
        checkProgramTraining(trainingName);
    }

    @Test
    public void editProgramTraining() throws Exception {
        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");

        editProgramTrainingClick("monday workout");
        clearProgramTrainingName();
        typeProgramTrainingName("tuesday workout");
        editProgramExerciseClick("Приседания в гакк-тренажере");
        editProgramSetClick(0);
        typeSet(5, 5, 0);
        checkSet(0, 5, 5, 0);
        editProgramSetClick(0);
        typeSet(1, 0, 0);
        checkSet(0, 1, 0, 0);
        addProgramSetClick();
        typeSet(1, 0, 0);
        saveProgramExerciseClick();
        checkProgramExercise(0, "Приседания в гакк-тренажере", 2);
        saveProgramTrainingClick();
        checkProgramTraining("tuesday workout");
    }
}
