package ru.codingworkshop.gymm.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.codingworkshop.gymm.data.entity.ActualExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ActualSetEntity;
import ru.codingworkshop.gymm.data.entity.ActualTrainingEntity;
import ru.codingworkshop.gymm.data.entity.ExerciseEntity;
import ru.codingworkshop.gymm.data.entity.MuscleGroupEntity;
import ru.codingworkshop.gymm.data.entity.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;
import ru.codingworkshop.gymm.data.entity.ProgramTrainingEntity;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLinkEntity;
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
                ActualTrainingEntity.class,
                ActualExerciseEntity.class,
                ActualSetEntity.class,
                ExerciseEntity.class,
                MuscleGroupEntity.class,
                ProgramTrainingEntity.class,
                ProgramExerciseEntity.class,
                ProgramSetEntity.class,
                SecondaryMuscleGroupLinkEntity.class
        },
        exportSchema = false
)
public abstract class GymmDatabase extends RoomDatabase {
    public static final long INVALID_ID = 0;
    public abstract ActualTrainingDao getActualTrainingDao();
    public abstract ExerciseDao getExerciseDao();
    public abstract MuscleGroupDao getMuscleGroupDao();
    public abstract ProgramTrainingDao getProgramTrainingDao();
}
