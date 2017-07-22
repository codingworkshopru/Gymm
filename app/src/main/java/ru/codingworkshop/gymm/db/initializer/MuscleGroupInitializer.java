package ru.codingworkshop.gymm.db.initializer;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

/**
 * Created by Радик on 01.06.2017.
 */

public class MuscleGroupInitializer extends EntityInitializer<List<MuscleGroup>> {
    private Lazy<MuscleGroupsRepository> muscleGroupsRepository;

    @Inject
    MuscleGroupInitializer(Context context, Lazy<MuscleGroupsRepository> muscleGroupsRepository) {
        super(context);
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    @Override
    boolean needToInitialize() {
        return muscleGroupsRepository.get().isEmpty();
    }

    @Override
    Type getType() {
        return new TypeToken<List<MuscleGroup>>(){}.getType();
    }

    @Override
    int getJsonResourceId() {
        return R.raw.muscle_groups;
    }

    @Override
    void saveToDatabase(List<MuscleGroup> data) {
        muscleGroupsRepository.get().insertMuscleGroups(data);
    }
}