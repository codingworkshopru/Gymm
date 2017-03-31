package ru.codingworkshop.gymm;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramTraining;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Радик on 19.02.2017.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private ProgramTraining mTraining;
    private List<Exercise> mExercises;

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Class mDbHelperClass = GymDbHelper.class;

    final String TRAINING_NAME = "Training name";
    final int WEEKDAY = 5;
    final int CHILDREN_COUNT = 10;
    final int REPS_ADDITION = 1;
    final int REST_MULTIPLIER = 20;

    final String eqNames = "Training names are not equal";
    final String eqWeekdays = "Weekdays are not equal";
    final String eqOrders = "Orders are not equal";
    final String eqExercises = "Exercises are not equal";
    final String eqReps = "Reps are not equal";
    final String eqRests = "Rest times are not equal";
    final String eqCursorAndChildrenCounts = "Cursor and children counts are not equal";

    @Before
    public void populateDataModel() {
        deleteTheDatabase();

        SQLiteDatabase db = getTheDatabase(false);

        mExercises = Exercise.readAll(db);

        // creation
        mTraining = new ProgramTraining();
        mTraining.setName(TRAINING_NAME);
        mTraining.setWeekday(WEEKDAY);

        for (int i = 0; i < CHILDREN_COUNT; i++) {
            ProgramExercise exercise = new ProgramExercise();
            exercise.setExercise(mExercises.get(i % mExercises.size()));
            exercise.setOrder(i);

            for (int j = 0; j < CHILDREN_COUNT; j++) {
                ProgramSet set = new ProgramSet();
                set.setOrder(j);
                set.setReps(j + REPS_ADDITION);
                int seconds = j * REST_MULTIPLIER;
                set.setTimeForRest(seconds / 60, seconds % 60);
                exercise.addChild(set);
            }

            mTraining.addChild(exercise);
        }

        mTraining.create(db, 0);
    }

    @Test
    public void test_create() {
        SQLiteDatabase db = getTheDatabase(true);

        Cursor trainingCursor = db.query(
                GymContract.ProgramTrainingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        trainingCursor.moveToNext();
        assertEquals(eqNames, TRAINING_NAME, trainingCursor.getString(trainingCursor.getColumnIndex(GymContract.ProgramTrainingEntry.COLUMN_NAME)));
        assertEquals(eqWeekdays, WEEKDAY + 1, trainingCursor.getInt(trainingCursor.getColumnIndex(GymContract.ProgramTrainingEntry.COLUMN_WEEKDAY)));

        Cursor exercisesCursor = db.query(
                GymContract.ProgramExerciseEntry.TABLE_NAME,
                null,
                GymContract.ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID + "=" + trainingCursor.getLong(0),
                null,
                null,
                null,
                null
        );

        assertEquals(eqCursorAndChildrenCounts, CHILDREN_COUNT, exercisesCursor.getCount());

        for (int i = 0; i < CHILDREN_COUNT; i++) {
            exercisesCursor.moveToNext();

            assertEquals(eqOrders, i, exercisesCursor.getInt(exercisesCursor.getColumnIndex(GymContract.ProgramExerciseEntry.COLUMN_SORT_ORDER)));
            assertEquals(eqExercises, mExercises.get(i % mExercises.size()).getId(), exercisesCursor.getInt(exercisesCursor.getColumnIndex(GymContract.ProgramExerciseEntry.COLUMN_EXERCISE_ID)));

            Cursor setsCursor = db.query(
                    GymContract.ProgramSetEntry.TABLE_NAME,
                    null,
                    GymContract.ProgramSetEntry.COLUMN_PROGRAM_EXERCISE_ID + "=" + exercisesCursor.getLong(0),
                    null,
                    null,
                    null,
                    null
            );

            assertEquals(eqCursorAndChildrenCounts, CHILDREN_COUNT, setsCursor.getCount());

            for (int j = 0; j < CHILDREN_COUNT; j++) {
                setsCursor.moveToNext();

                assertEquals(eqOrders, j, setsCursor.getInt(setsCursor.getColumnIndex(GymContract.ProgramSetEntry.COLUMN_SORT_ORDER)));
                assertEquals(eqReps, j + REPS_ADDITION, setsCursor.getInt(setsCursor.getColumnIndex(GymContract.ProgramSetEntry.COLUMN_REPS)));
                assertEquals(eqRests, j * REST_MULTIPLIER, setsCursor.getInt(setsCursor.getColumnIndex(GymContract.ProgramSetEntry.COLUMN_SECONDS_FOR_REST)));
            }

            setsCursor.close();
        }

        trainingCursor.close();
        exercisesCursor.close();
    }

    @Test
    public void test_read() {
        SQLiteDatabase db = getTheDatabase(false);

        ProgramTraining loadedTraining = ProgramTraining.read(db, 0L).get(0);

        assertEquals(eqNames, TRAINING_NAME, loadedTraining.getName());
        assertEquals(eqWeekdays, WEEKDAY, loadedTraining.getWeekday());

        for (int i = 0; i < CHILDREN_COUNT; i++) {
            ProgramExercise loadedExercise = loadedTraining.getChild(i);

            assertEquals(eqOrders, i, loadedExercise.getOrder());
            assertEquals(eqExercises, mExercises.get(i % mExercises.size()).getId(), loadedExercise.getExercise().getId());

            for (int j = 0; j < CHILDREN_COUNT; j++) {
                ProgramSet loadedSet = loadedExercise.getChild(j);

                assertEquals(eqOrders, j, loadedSet.getOrder());
                assertEquals(eqReps, j + REPS_ADDITION, loadedSet.getReps());
                int seconds = j * REST_MULTIPLIER;
                assertEquals(eqRests, seconds / 60, loadedSet.getRestMinutes());
                assertEquals(eqRests, seconds % 60, loadedSet.getRestSeconds());
            }
        }
    }

    @Test
    public void test_update() {
        SQLiteDatabase db = getTheDatabase(true);

        ProgramExercise e = mTraining.getChild(0);
        e.setExercise(mExercises.get(mExercises.size()-1));

        mTraining.moveChild(mTraining.childrenCount()-1, 0);

        ProgramSet s = e.getChild(0);
        s.setReps(3);



        mTraining.update(db);
    }

    @Test
    public void test_delete() {
        SQLiteDatabase db = getTheDatabase(true);
        for (int i = 0; i < mTraining.childrenCount(); i++) {
            ProgramExercise exercise = mTraining.getChild(i);
            for (int j = 0; j < exercise.childrenCount(); j++) {
                exercise.removeChild(j);
            }
            mTraining.removeChild(i);
        }

        mTraining.delete(db);

        assertEquals("program sets have not deleted", 0, DatabaseUtils.queryNumEntries(db, GymContract.ProgramSetEntry.TABLE_NAME));
        assertEquals("program exercises have not deleted", 0, DatabaseUtils.queryNumEntries(db, GymContract.ProgramExerciseEntry.TABLE_NAME));
        assertEquals("Program trainings have not deleted", 0, DatabaseUtils.queryNumEntries(db, GymContract.ProgramTrainingEntry.TABLE_NAME));

        db.close();
    }

    public SQLiteDatabase getTheDatabase(boolean writable) {
        try {
            SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
            return writable ? dbHelper.getWritableDatabase() : dbHelper.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }

    public void deleteTheDatabase() {
        try {
            Field f = mDbHelperClass.getDeclaredField("DATABASE_NAME");
            f.setAccessible(true);
            String dbName = (String) f.get(null);
            mContext.deleteDatabase(dbName);
        } catch (NoSuchFieldException e) {
            fail("DATABASE_NAME field is not presented in db helper");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
