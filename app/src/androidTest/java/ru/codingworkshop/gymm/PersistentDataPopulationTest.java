package ru.codingworkshop.gymm;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.data.GymDatabaseSource;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ExerciseEntity;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.data.model.MuscleGroupEntity;

/**
 * Created by Радик on 19.04.2017.
 */

@RunWith(AndroidJUnit4.class)
public class PersistentDataPopulationTest {
    interface JsonObjectToPersistable {
        Persistable convert(JSONObject object) throws JSONException;
    }

    private Context mContext = InstrumentationRegistry.getTargetContext();
    private EntityDataStore<Persistable> data = GymDatabaseSource.createDataStore(mContext);

    @Before
    public void deleteDatabase() {
        mContext.deleteDatabase(GymDatabaseSource.DATABASE_NAME);
    }

    @Test
    public void test_persistentDataPopulation() throws IOException, JSONException {
        List<Persistable> muscleGroups = createFromRaw(R.raw.muscle_groups, new JsonObjectToPersistable() {
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

        Assert.assertNotEquals("Muscle groups haven't been copied", 0L, data.count(MuscleGroup.class).get().value().longValue());

        final Map<String, MuscleGroupEntity> muscleGroupsMap = new HashMap<>(muscleGroups.size());
        for (Persistable p : muscleGroups) {
            MuscleGroupEntity mg = (MuscleGroupEntity) p;
            muscleGroupsMap.put(mg.getName(), mg);
        }

        List<Persistable> exercises = createFromRaw(R.raw.exercises, new JsonObjectToPersistable() {
            @Override
            public Persistable convert(JSONObject object) throws JSONException {
                ExerciseEntity ex = new ExerciseEntity();
                ex.setName(object.getString("name"));
                ex.setDifficulty(object.optInt("difficulty"));
                ex.setIsWithWeight(object.optBoolean("isWithWeight", true));
                ex.setYouTubeVideo(object.optString("youTubeVideo", null));
                ex.setSteps(object.optString("steps", null));
                ex.setAdvices(object.optString("advices", null));
                ex.setCaution(object.optString("caution", null));
                ex.setVariations(object.optString("variations", null));

                ex.setPrimaryMuscleGroup(muscleGroupsMap.get(object.getString("primaryMuscle")));

                JSONArray secondaryMuscles = object.optJSONArray("secondaryMuscles");
                if (secondaryMuscles != null) {
                    for (int i = 0; i < secondaryMuscles.length(); i++) {
                        ex.getSecondaryMuscleGroups().add(muscleGroupsMap.get(secondaryMuscles.getString(i)));
                    }
                }

                return ex;
            }
        });
        data.insert(exercises);

        Assert.assertNotEquals("Exercises haven't been copied", 0L, data.count(Exercise.class).get().value().longValue());

        Assert.assertNotEquals("Exercises for primary muscle group list is empty", 0, data.select(MuscleGroup.class).get().first().getExercisesForPrimary().size());
    }

    private List<Persistable> createFromRaw(@RawRes int rawResId, JsonObjectToPersistable converter) throws IOException, JSONException {
        InputStream rawJsonStream = mContext.getResources().openRawResource(rawResId);
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
