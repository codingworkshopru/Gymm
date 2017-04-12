package ru.codingworkshop.gymm.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 07.01.2017.
 */

public final class GymDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Gym.db";
    private static final int DATABASE_VERSION = 1;
    private static final Pattern BEAUTIFY_PATTERN = Pattern.compile("^ +", Pattern.MULTILINE);

    private Context mContext;

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
        db.execSQL(GymContract.ProgramTrainingEntry.SQL_CREATE);
        db.execSQL(GymContract.ProgramExerciseEntry.SQL_CREATE);
        db.execSQL(GymContract.ProgramSetEntry.SQL_CREATE);
        db.execSQL(GymContract.ActualTrainingEntry.SQL_CREATE);
        db.execSQL(GymContract.ActualExerciseEntry.SQL_CREATE);
        db.execSQL(GymContract.ActualSetEntry.SQL_CREATE);

        try {
            parseExercisesXml(db);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GymContract.MuscleGroupEntry.SQL_DROP);
        db.execSQL(GymContract.ExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.SecondaryMuscleGroupLinkEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramTrainingEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.ProgramSetEntry.SQL_DROP);
        db.execSQL(GymContract.ActualTrainingEntry.SQL_DROP);
        db.execSQL(GymContract.ActualExerciseEntry.SQL_DROP);
        db.execSQL(GymContract.ActualSetEntry.SQL_DROP);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, newVersion, oldVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    private HashMap<String, MuscleGroup> parseMuscleGroupsXml(SQLiteDatabase db) throws XmlPullParserException, IOException {
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.muscle_groups);
        int eventType = parser.getEventType();

        HashMap<String, MuscleGroup> muscleGroupIds = new HashMap<>();

        MuscleGroup muscleGroup = null;
        String tag = "";

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {

                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if (tag.equals("muscle_group"))
                        muscleGroup = new MuscleGroup();

                    break;

                case XmlPullParser.TEXT:
                    String text = parser.getText();

                    switch (tag) {

                        case "name":
                            muscleGroup.setName(text);
                            break;

                        case "map_color_rgb":
                            muscleGroup.setMapColorRgb(text);
                            break;

                        case "is_anterior":
                            muscleGroup.setAnterior(text.equals("1"));
                    }

                    break;

                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("muscle_group")) {
                        muscleGroup.create(db, 0);
                        muscleGroupIds.put(muscleGroup.getName(), muscleGroup);
                    }
            }

            eventType = parser.next();
        }

        return muscleGroupIds;
    }

    private void parseExercisesXml(SQLiteDatabase db) throws XmlPullParserException, IOException {
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.exercises);
        HashMap<String, MuscleGroup> muscleGroups = parseMuscleGroupsXml(db);
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

                    switch (tag) {

                        case "name":
                            exercise.setName(text);
                            break;

                        case "video":
                            exercise.setVideo(text);
                            break;

                        case "primary_muscle":
                            if (!muscleGroups.containsKey(text))
                                throw new Resources.NotFoundException("Muscle group not found " + text);

                            exercise.setPrimaryMuscleGroup(muscleGroups.get(text));
                            break;

                        case "is_with_weight":
                            exercise.setWithWeight(text.equals("1"));
                            break;

                        case "difficulty":
                            exercise.setDifficulty(Integer.valueOf(text));
                            break;

                        case "steps":
                            exercise.setSteps(beautify(text));
                            break;

                        case "advices":
                            exercise.setAdvices(beautify(text));
                            break;

                        case "caution":
                            exercise.setCaution(beautify(text));
                            break;

                        case "variations":
                            exercise.setVariations(beautify(text));
                            break;

                        case "muscle":
                            if (!muscleGroups.containsKey(text))
                                throw new Resources.NotFoundException("Muscle group not found " + text);

                            exercise.addSecondaryMuscles(muscleGroups.get(text));
                    }

                    break;

                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("exercise"))
                        exercise.create(db, 0);
            }

            eventType = parser.next();
        }
    }

    private String beautify(String text) {
        return BEAUTIFY_PATTERN.matcher(text).replaceAll("\u2022 ");
    }
}
