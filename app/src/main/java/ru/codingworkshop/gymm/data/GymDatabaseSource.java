package ru.codingworkshop.gymm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.meta.EntityModel;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import ru.codingworkshop.gymm.BuildConfig;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ExerciseEntity;
import ru.codingworkshop.gymm.data.model.Models;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.data.model.MuscleGroupEntity;

/**
 * Created by Радик on 21.04.2017.
 */

public final class GymDatabaseSource extends DatabaseSource {
    private Context context;
    public static final String DATABASE_NAME = "Gymm.db";

    private GymDatabaseSource(Context context, EntityModel model, @Nullable String name, int version) {
        super(context, model, name, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);

        EntityDataStore<Persistable> dataStore = new EntityDataStore<>(getConfiguration());
        try {
            loadExercisesFromJson(dataStore);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static EntityDataStore<Persistable> createDataStore(Context context) {
        context.deleteDatabase(DATABASE_NAME);
        DatabaseSource source = new GymDatabaseSource(context, Models.DEFAULT, DATABASE_NAME, 1);
        if (BuildConfig.DEBUG) {
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }
        Configuration configuration = source.getConfiguration();
        return new EntityDataStore<>(configuration);
    }

    private interface JsonObjectToPersistable {
        Persistable convert(JSONObject object) throws JSONException;
    }

    @SuppressWarnings("unchecked")
    private List<MuscleGroup> loadMuscleGroupsFromJson(EntityDataStore data) throws IOException, JSONException {
        List<MuscleGroup> muscleGroups = (List<MuscleGroup>) createFromRaw(R.raw.muscle_groups, new JsonObjectToPersistable() {
            @Override
            public Persistable convert(JSONObject object) throws JSONException {
                MuscleGroupEntity mg = new MuscleGroupEntity();
                mg.setName(object.getString("name"));
                mg.setMapColorRgb(object.getString("mapColorRgb"));
                mg.setIsAnterior(object.getBoolean("isAnterior"));
                return mg;
            }
        });
        data.insert(muscleGroups);
        return muscleGroups;
    }

    @SuppressWarnings("unchecked")
    private void loadExercisesFromJson(EntityDataStore data) throws IOException, JSONException {
        List<MuscleGroup> muscleGroups = loadMuscleGroupsFromJson(data);
        final ImmutableMap<String, MuscleGroup> muscleGroupMap = Maps.uniqueIndex(muscleGroups, new Function<MuscleGroup, String>() {
            @NonNull
            @Override
            public String apply(MuscleGroup muscleGroup) {
                return muscleGroup.getName();
            }
        });

        List<Exercise> exercises = (List<Exercise>) createFromRaw(R.raw.exercises, new JsonObjectToPersistable() {
            @NonNull
            @Override
            public Persistable convert(JSONObject object) throws JSONException {
                Exercise ex = new ExerciseEntity();
                ex.setName(object.getString("name"));
                ex.setDifficulty(object.optInt("difficulty"));
                ex.setIsWithWeight(object.optBoolean("isWithWeight", true));
                ex.setYouTubeVideo(object.optString("youTubeVideo", null));
                ex.setSteps(object.optString("steps", null));
                ex.setAdvices(object.optString("advices", null));
                ex.setCaution(object.optString("caution", null));
                ex.setVariations(object.optString("variations", null));

                ex.setPrimaryMuscleGroup(muscleGroupMap.get(object.getString("primaryMuscle")));

                JSONArray secondaryMuscles = object.optJSONArray("secondaryMuscles");
                if (secondaryMuscles != null) {
                    for (int i = 0; i < secondaryMuscles.length(); i++) {
                        ex.getSecondaryMuscleGroups().add(muscleGroupMap.get(secondaryMuscles.getString(i)));
                    }
                }

                return ex;
            }
        });
        data.insert(exercises);
    }

    private List<? extends Persistable> createFromRaw(@RawRes int rawResId, JsonObjectToPersistable converter) throws IOException, JSONException {
        InputStream rawJsonStream = context.getResources().openRawResource(rawResId);
        InputStreamReader streamReader = new InputStreamReader(rawJsonStream, Charsets.UTF_8);

        String json = CharStreams.toString(streamReader);
        JSONArray jsonArray = new JSONArray(json);
        rawJsonStream.close();

        int groupsCount = jsonArray.length();

        List<Persistable> muscleGroupEntities = new ArrayList<>(groupsCount);
        for (int i = 0; i < groupsCount; i++) {
            JSONObject group = jsonArray.getJSONObject(i);
            Persistable persistable = converter.convert(group);
            muscleGroupEntities.add(persistable);
        }
        return muscleGroupEntities;
    }
}
