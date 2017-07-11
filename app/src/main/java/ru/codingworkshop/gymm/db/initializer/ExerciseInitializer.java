package ru.codingworkshop.gymm.db.initializer;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.ExerciseDifficulty;
import ru.codingworkshop.gymm.data.entity.ExerciseEntity;
import ru.codingworkshop.gymm.db.Converters;
import ru.codingworkshop.gymm.repository.ExercisesRepository;

/**
 * Created by Радик on 01.06.2017.
 */

public class ExerciseInitializer extends EntityInitializer<List<ExerciseEntity>> {
    private Lazy<ExercisesRepository> exercisesRepository;

    @Inject
    ExerciseInitializer(Context context, Lazy<ExercisesRepository> exercisesRepository) {
        super(context);
        this.exercisesRepository = exercisesRepository;
    }

    @Override
    Type getType() {
        return new TypeToken<List<ExerciseEntity>>(){}.getType();
    }

    @Override
    int getJsonResourceId() {
        return R.raw.exercises;
    }

    @Override
    Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        ExerciseDifficulty.class,
                        (JsonDeserializer<ExerciseDifficulty>) (json, a, b) ->
                                Converters.fromIntToEnum(json.getAsInt())
                )
                .create();
    }

    @Override
    boolean needToInitialize() {
        return exercisesRepository.get().isEmpty();
    }

    @Override
    void saveToDatabase(List<ExerciseEntity> exercises) {
        exercisesRepository.get().createWithSecondaryMuscleGroups(exercises);
    }
}