package ru.codingworkshop.gymm.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;

/**
 * Created by Радик on 04.06.2017.
 */

@Database(
        version = 1,
        entities = {
                ActualTraining.class,
                ActualExercise.class,
                ActualSet.class,
                Exercise.class,
                MuscleGroup.class,
                ProgramTraining.class,
                ProgramExercise.class,
                ProgramSet.class,
                SecondaryMuscleGroupLink.class
        },
        exportSchema = false
)
public abstract class GymmDatabase extends RoomDatabase {
    public static final long INVALID_ID = 0;

    public static boolean isValidId(long id) {
        return id != INVALID_ID;
    }

    public static boolean isValidId(@NonNull Model model) {
        Preconditions.checkNotNull(model, "checking model is null");
        return isValidId(model.getId());
    }

    public abstract ActualTrainingDao getActualTrainingDao();
    public abstract ExerciseDao getExerciseDao();
    public abstract MuscleGroupDao getMuscleGroupDao();
    public abstract ProgramTrainingDao getProgramTrainingDao();
}
