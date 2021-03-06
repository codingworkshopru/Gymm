package ru.codingworkshop.gymm.integration;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.MainActivity;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ru.codingworkshop.gymm.db.initializer.DatabaseInitializer.DATABASE_NAME;
import static ru.codingworkshop.gymm.integration.Operation.addProgramExerciseClick;
import static ru.codingworkshop.gymm.integration.Operation.addProgramSetClick;
import static ru.codingworkshop.gymm.integration.Operation.addProgramTrainingClick;
import static ru.codingworkshop.gymm.integration.Operation.checkExerciseNotPresented;
import static ru.codingworkshop.gymm.integration.Operation.checkProgramExercise;
import static ru.codingworkshop.gymm.integration.Operation.checkProgramTraining;
import static ru.codingworkshop.gymm.integration.Operation.checkSet;
import static ru.codingworkshop.gymm.integration.Operation.clearProgramTrainingName;
import static ru.codingworkshop.gymm.integration.Operation.deleteProgramExerciseAt;
import static ru.codingworkshop.gymm.integration.Operation.editProgramExerciseClick;
import static ru.codingworkshop.gymm.integration.Operation.editProgramSetClick;
import static ru.codingworkshop.gymm.integration.Operation.editProgramTrainingClick;
import static ru.codingworkshop.gymm.integration.Operation.enterActionMode;
import static ru.codingworkshop.gymm.integration.Operation.exitActionMode;
import static ru.codingworkshop.gymm.integration.Operation.pickExercise;
import static ru.codingworkshop.gymm.integration.Operation.saveProgramExerciseClick;
import static ru.codingworkshop.gymm.integration.Operation.saveProgramTrainingClick;
import static ru.codingworkshop.gymm.integration.Operation.typeProgramTrainingName;
import static ru.codingworkshop.gymm.integration.Operation.typeSetAndSaveIt;

/**
 * Created by Radik on 22.11.2017.
 */

@LargeTest
public class ProgramTrainingIntegrationTest {
    @Rule public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);
    private static GymmDatabase db;
    private String trainingName;
    private String exerciseName;

    @BeforeClass
    public static void initDb() {
        db = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), GymmDatabase.class, DATABASE_NAME)
                .build();
    }

    @AfterClass
    public static void closeDb() {
        db.close();
    }

    @Before
    public void setUp() throws Exception {
        db.compileStatement("delete from ProgramTraining").executeUpdateDelete();

        trainingName = "zoo";
        exerciseName = db.getExerciseDao().getExerciseById(1L).getName();

        db.getProgramTrainingDao().insertProgramTraining(Models.createProgramTraining(1L, trainingName));
        List<ProgramExercise> programExercises = Models.createProgramExercises(1);
        programExercises.get(0).setExerciseId(1);
        db.getProgramTrainingDao().insertProgramExercises(programExercises);
        db.getProgramTrainingDao().insertProgramSets(Models.createProgramSets(programExercises.get(0).getId(), 1));

        activityTestRule.launchActivity(null);
    }

    @LargeTest
    @Test
    public void addProgramTraining() {
        trainingName = "foo";
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

    @LargeTest
    @Test
    public void addAndEditProgramSet() {
        editProgramTrainingClick(trainingName);
        editProgramExerciseClick(exerciseName);
        // add
        addProgramSetClick();
        typeSetAndSaveIt(7, 6, 5);
        // edit
        editProgramSetClick(0);
        typeSetAndSaveIt(8, 9, 10);

        saveProgramExerciseClick();
        saveProgramTrainingClick();

        editProgramTrainingClick(trainingName);
        editProgramExerciseClick(exerciseName);
        checkSet(0, 8, 9, 10);
        checkSet(1, 7, 6, 5);
    }

    @LargeTest
    @Test
    public void addAndEditProgramExercise() {
//        addOneProgramTraining("monday workout", "Приседания в гакк-тренажере");

        editProgramTrainingClick(trainingName);

        // edit
        editProgramExerciseClick(exerciseName);
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
        editProgramTrainingClick(trainingName);
        checkProgramExercise(0, "Отжимания от пола", 2);
        checkProgramExercise(1, "Жим штанги лёжа", 1);
    }

    @LargeTest
    @Test
    public void editProgramTraining() {
        editProgramTrainingClick(trainingName);
        clearProgramTrainingName();
        typeProgramTrainingName("tuesday workout");
        editProgramExerciseClick(exerciseName);
        editProgramSetClick(0);
        typeSetAndSaveIt(5, 5, 0);
        checkSet(0, 5, 5, 0);
        editProgramSetClick(0);
        typeSetAndSaveIt(1, 0, 0);
        checkSet(0, 1, 0, 0);
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();
        checkProgramExercise(0, exerciseName, 2);
        saveProgramTrainingClick();
        checkProgramTraining("tuesday workout");
    }

    @LargeTest
    @Test
    public void deleteProgramExercise() {
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

        enterActionMode(R.id.programExerciseList);
        deleteProgramExerciseAt(1);
        exitActionMode();

        addProgramExerciseClick();
        Espresso.pressBack();
        checkExerciseNotPresented("Жим штанги лёжа");
    }

    @LargeTest
    @Test
    public void checkSetsSortOrder() {
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

    @LargeTest
    @Test
    public void checkExercisesSortOrder() {
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

        enterActionMode(R.id.programExerciseList);
        deleteProgramExerciseAt(2);
        onView(withText(android.R.string.cancel)).perform(click());

        checkProgramExercise(0, "Отжимания от пола", 1);
        checkProgramExercise(1, "Жим штанги лёжа", 1);
        checkProgramExercise(2, "Приседания со штангой", 1);
    }

    @LargeTest
    @Test
    public void undoChangesAfterExerciseAddition() {
        editProgramTrainingClick(trainingName);

        addProgramExerciseClick();
        pickExercise(db, "Приседания со штангой");
        addProgramSetClick();
        typeSetAndSaveIt(1, 0, 0);
        saveProgramExerciseClick();

        Espresso.pressBack();
        onView(withText(R.string.yes_button_text)).perform(click());

        editProgramTrainingClick(trainingName);
        checkExerciseNotPresented("Приседания со штангой");
    }

    @LargeTest
    @Test
    public void undoChangesAfterSetAddition() {
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
        onView(withText(R.string.yes_button_text)).perform(click());

        checkProgramExercise(0, "Отжимания от пола", 1);
    }

    @LargeTest
    @Test
    public void deleteAllAndAdd() {
        editProgramTrainingClick(trainingName);
        enterActionMode(R.id.programExerciseList);
        deleteProgramExerciseAt(0);
        exitActionMode();

        addProgramExerciseClick();
        pickExercise(db, "Приседания со штангой");
        addProgramSetClick();
        typeSetAndSaveIt(10, 1, 30);
        saveProgramExerciseClick();
        saveProgramTrainingClick();
    }
}
