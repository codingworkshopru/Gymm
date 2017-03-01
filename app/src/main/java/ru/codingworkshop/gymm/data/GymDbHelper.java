package ru.codingworkshop.gymm.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 07.01.2017.
 */

public final class GymDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Gym.db";
    private static final int DATABASE_VERSION = 1;

    Context mContext;

    public GymDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating all tables
        db.execSQL(GymContract.MuscleGroupEntry.SQL_CREATE);
        db.execSQL(GymContract.ExerciseEntry.SQL_CREATE);
        db.execSQL(GymContract.SecondaryMuscleGroupLinkEntry.SQL_CREATE);
        db.execSQL(GymContract.ActualExerciseEntry.SQL_CREATE);
        db.execSQL(GymContract.ActualSetEntry.SQL_CREATE);
        db.execSQL(GymContract.ProgramTrainingEntry.SQL_CREATE);
        db.execSQL(GymContract.ProgramExerciseEntry.SQL_CREATE);
        db.execSQL(GymContract.ProgramSetEntry.SQL_CREATE);

        try {
            parseXml(db);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GymContract.MuscleGroupEntry.SQL_DROP);
        db.execSQL(GymContract.ExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.SecondaryMuscleGroupLinkEntry.SQL_DROP);
        db.execSQL(GymContract.ActualExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.ActualSetEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramTrainingEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramSetEntry.SQL_DROP);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, newVersion, oldVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
//        db.execSQL("PRAGMA foreign_keys = ON");
    }

    private void parseXml(SQLiteDatabase db) throws XmlPullParserException, IOException {
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.all_exercises);
        HashMap<String, Long> muscleGroups = new HashMap<>();
        String tag = "";
        Exercise exercise = null;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if (tag.equals("exercise"))
                        exercise = new Exercise();
                    break;
                case XmlPullParser.TEXT:
                    String text = parser.getText();

                    if (tag.equals("name")) {
                        exercise.setName(text);
                    } else if (tag.equals("video")) {
                        exercise.setVideo(text);
                    }  else if (tag.equals("primary_muscle") || tag.equals("muscle")) {
                        Long id = muscleGroups.get(text);
                        if (id == null) {
                            ContentValues cv = new ContentValues();
                            cv.put(GymContract.MuscleGroupEntry.COLUMN_NAME, text);
                            id = db.insert(GymContract.MuscleGroupEntry.TABLE_NAME, null, cv);
                            muscleGroups.put(text, id);
                        }
                        MuscleGroup createdGroup = new MuscleGroup(id, "");
                        if (tag.equals("primary_muscle"))
                            exercise.setPrimaryMuscleGroup(createdGroup);
                        else
                            exercise.addSecondaryMuscles(createdGroup);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("exercise")) {
                        ContentValues cv = new ContentValues();

                        cv.put(GymContract.ExerciseEntry.COLUMN_NAME, exercise.getName());
                        cv.put(GymContract.ExerciseEntry.COLUMN_YOUTUBE_VIDEO, exercise.getVideo());
                        cv.put(GymContract.ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID, exercise.getPrimaryMuscleGroup().getId());

                        long exerciseId = db.insert(GymContract.ExerciseEntry.TABLE_NAME, null, cv);

                        List<MuscleGroup> secondaryMuscles = exercise.getSecondaryMuscles();
                        if (secondaryMuscles != null) {
                            for (MuscleGroup secondaryMuscleGroup : secondaryMuscles) {
                                cv = new ContentValues();
                                cv.put(GymContract.SecondaryMuscleGroupLinkEntry.COLUMN_EXERCISE_ID, exerciseId);
                                cv.put(GymContract.SecondaryMuscleGroupLinkEntry.COLUMN_MUSCLE_GROUP_ID, secondaryMuscleGroup.getId());
                                db.insert(GymContract.SecondaryMuscleGroupLinkEntry.TABLE_NAME, null, cv);
                            }
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
    }
}
