package ru.codingworkshop.gymm.functionaltests.test;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ru.codingworkshop.gymm.db.initializer.DatabaseInitializer.DATABASE_NAME;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.addProgramExerciseClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.addProgramSetClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.addProgramTrainingClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.checkExerciseNotPresented;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.checkProgramExercise;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.checkProgramTraining;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.checkSet;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.clearProgramTrainingName;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.deleteProgramExerciseAt;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.editProgramExerciseClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.editProgramSetClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.editProgramTrainingClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.enterActionMode;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.exitActionMode;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.pickExercise;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.saveProgramExerciseClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.saveProgramTrainingClick;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.typeProgramTrainingName;
import static ru.codingworkshop.gymm.functionaltests.test.Operation.typeSetAndSaveIt;

/**
 * Created by Radik on 22.12.2017.
 */

public class ProgramTrainingFunctionalTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
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
        typeSetAndSaveIt(1, 0, 0);
        checkSet(0, 1, 0, 0);
        saveProgramExerciseClick();
        checkProgramExercise(0, exerciseName, 1);
        saveProgramTrainingClick();
        checkProgramTraining(trainingName);
    }

    @Test
    public void addAndEditProgramSet() throws Exception {
        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");

        editProgramTrainingClick("monday workout");
        editProgramExerciseClick("Приседания в гакк-тренажере");
        // add
        addProgramSetClick();
        typeSetAndSaveIt(7, 6, 5);
        // edit
        editProgramSetClick(0);
        typeSetAndSaveIt(8, 9, 10);

        saveProgramExerciseClick();
        saveProgramTrainingClick();

        editProgramTrainingClick("monday workout");
        editProgramExerciseClick("Приседания в гакк-тренажере");
        checkSet(0, 8, 9, 10);
        checkSet(1, 7, 6, 5);
    }

    @Test
    public void addAndEditProgramExercise() throws Exception {
        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");

        editProgramTrainingClick("monday workout");

        // edit
        editProgramExerciseClick("Приседания в гакк-тренажере");
        pickExercise(db, "Отжимания от пола");
        addProgramSetClick();
        typeSetAndSaveIt(1, 2, 5);
        saveProgramExerciseClick();

        // add
        addProgramExerciseClick();
        pickExercise(db, "Жим штанги лёжа");
        addProgramSetClick();
        typeSetAndSaveIt(1, 4, 5);
        saveProgramExerciseClick();

        saveProgramTrainingClick();
        editProgramTrainingClick("monday workout");
        checkProgramExercise(0, "Отжимания от пола", 2);
        checkProgramExercise(1, "Жим штанги лёжа", 1);
    }

    @Test
    public void editProgramTraining() throws Exception {
        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");

        editProgramTrainingClick("monday workout");
        clearProgramTrainingName();
        typeProgramTrainingName("tuesday workout");
        editProgramExerciseClick("Приседания в гакк-тренажере");
        editProgramSetClick(0);
        typeSetAndSaveIt(5, 5, 0);
        checkSet(0, 5, 5, 0);
        editProgramSetClick(0);
        typeSetAndSaveIt(1, 0, 0);
        checkSet(0, 1, 0, 0);
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();
        checkProgramExercise(0, "Приседания в гакк-тренажере", 2);
        saveProgramTrainingClick();
        checkProgramTraining("tuesday workout");
    }

    @Test
    public void deleteProgramExercise() throws Exception {
        addProgramTrainingClick();
        typeProgramTrainingName("my workout");

        addProgramExerciseClick();
        pickExercise(db, "Приседания со штангой");

        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        addProgramExerciseClick();
        pickExercise(db, "Жим штанги лёжа");

        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        enterActionMode(ru.codingworkshop.gymm.R.id.programExerciseList);
        deleteProgramExerciseAt(1);
        exitActionMode();

        addProgramExerciseClick();
        Espresso.pressBack();
        checkExerciseNotPresented("Жим штанги лёжа");
    }

    @Test
    public void checkSetsSortOrder() throws Exception {
        addProgramTrainingClick();
        addProgramExerciseClick();

        pickExercise(db, "Отжимания от пола");

        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);

        addProgramSetClick();
        typeSetAndSaveIt(2, 3, 5);

        addProgramSetClick();
        typeSetAndSaveIt(3, 4, 10);
        saveProgramExerciseClick();

        editProgramExerciseClick("Отжимания от пола");

        checkSet(1, 2, 3, 5);
        checkSet(2, 3, 4, 10);
    }

    @Test
    public void checkExercisesSortOrder() throws Exception {
        addProgramTrainingClick();

        addProgramExerciseClick();
        pickExercise(db, "Отжимания от пола");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        addProgramExerciseClick();
        pickExercise(db, "Жим штанги лёжа");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        addProgramExerciseClick();
        pickExercise(db, "Приседания со штангой");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        enterActionMode(ru.codingworkshop.gymm.R.id.programExerciseList);
        deleteProgramExerciseAt(2);
        onView(withText(android.R.string.cancel)).perform(click());

        checkProgramExercise(0, "Отжимания от пола", 1);
        checkProgramExercise(1, "Жим штанги лёжа", 1);
        checkProgramExercise(2, "Приседания со штангой", 1);
    }

    @Test
    public void undoChangesAfterExerciseAddition() throws Exception {
        addOneProgramTraining("workout is ruined", "Отжимания от пола");

        editProgramTrainingClick("workout is ruined");

        addProgramExerciseClick();
        pickExercise(db, "Приседания со штангой");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        Espresso.pressBack();
        onView(withText(android.R.string.ok)).perform(click());

        editProgramTrainingClick("workout is ruined");
        checkExerciseNotPresented("Приседания со штангой");
    }

    @Test
    public void undoChangesAfterSetAddition() throws Exception {
        addProgramTrainingClick();
        addProgramExerciseClick();
        pickExercise(db, "Отжимания от пола");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        editProgramExerciseClick("Отжимания от пола");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        Espresso.pressBack();
        onView(withText(android.R.string.ok)).perform(click());

        checkProgramExercise(0, "Отжимания от пола", 1);
    }
}
