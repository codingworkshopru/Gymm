package ru.codingworkshop.gymm.data;

import android.provider.BaseColumns;

/**
 * Created by Радик on 07.01.2017.
 */

public final class GymContract {
    private GymContract() {}

    // convenient methods
    public static String createTableStatement(String tableName, String... columnDefinitions) {
        StringBuilder definitions = new StringBuilder(2*columnDefinitions.length);
        for (String d : columnDefinitions) {
            definitions.append(d).append(",");
        }
        definitions.deleteCharAt(definitions.length() - 1);
        return String.format("CREATE TABLE %s(%s);", tableName, definitions.toString());
    }

    public static String primaryKey() {
        return BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    public static String foreignKey(String column, String referencedTable, String referencedColumn,
                                    String onUpdate, String onDelete) {
        return String.format("FOREIGN KEY(%s) REFERENCES %s(%s) ON UPDATE %s ON DELETE %s",
                column, referencedTable, referencedColumn, onUpdate, onDelete);
    }

    public static String dropTableStatement(String tableName) {
        return String.format("DROP TABLE IF EXISTS %s;", tableName);
    }

    // tables
    public static final class MuscleGroupEntry implements BaseColumns {
        public static final String TABLE_NAME = "muscle_group";
        public static final String COLUMN_NAME = "name";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s TEXT NOT NULL", COLUMN_NAME)
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_PRIMARY_MUSCLE_GROUP_ID = "primary_muscle_group_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YOUTUBE_VIDEO = "youtube_video";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s INTEGER NOT NULL", COLUMN_PRIMARY_MUSCLE_GROUP_ID),
                String.format("%s TEXT NOT NULL", COLUMN_NAME),
                String.format("%s TEXT NULL", COLUMN_YOUTUBE_VIDEO),
                foreignKey(COLUMN_PRIMARY_MUSCLE_GROUP_ID, MuscleGroupEntry.TABLE_NAME, MuscleGroupEntry._ID, "CASCADE", "RESTRICT")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
        public static final String SQL_SELECT_ALL = "SELECT t1._id AS _id, t1.name AS exercise_name, t1.youtube_video, group_concat(t4.name) AS secondary_muscles_name" +
                " FROM exercise AS t1" +
                "   JOIN secondary_muscle_group_link AS t2 ON t2.exercise_id = t1._id" +
                "   JOIN muscle_group AS t3 ON t3._id = t2.muscle_group_id" +
                " WHERE t1.primary_muscle_group = %s" +
                " GROUP BY t1._id, t1.name, t1.youtube_video" +
                " ORDER BY t1.name";
    }

    public static final class SecondaryMuscleGroupLinkEntry {
        public static final String TABLE_NAME = "secondary_muscle_group_link";
        public static final String COLUMN_MUSCLE_GROUP_ID = "muscle_group_id";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                String.format("%s INTEGER NOT NULL", COLUMN_MUSCLE_GROUP_ID),
                String.format("%s INTEGER NOT NULL", COLUMN_EXERCISE_ID),
                foreignKey(COLUMN_MUSCLE_GROUP_ID, MuscleGroupEntry.TABLE_NAME, MuscleGroupEntry._ID, "CASCADE", "RESTRICT"),
                foreignKey(COLUMN_EXERCISE_ID, ExerciseEntry.TABLE_NAME, ExerciseEntry._ID, "CASCADE", "RESTRICT")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ActualExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "actual_exercise";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";
        public static final String COLUMN_TRAINING_DATE = "training_date";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s INTEGER NOT NULL", COLUMN_EXERCISE_ID),
                String.format("%s TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", COLUMN_TRAINING_DATE),
                foreignKey(COLUMN_EXERCISE_ID, ExerciseEntry.TABLE_NAME, ExerciseEntry._ID, "CASCADE", "RESTRICT")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ActualSetEntry implements BaseColumns {
        public static final String TABLE_NAME = "actual_set";
        public static final String COLUMN_ACTUAL_EXERCISE_ID = "actual_exercise_id";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_WEIGHT = "weight";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s INTEGER NOT NULL", COLUMN_ACTUAL_EXERCISE_ID),
                String.format("%s INTEGER NOT NULL", COLUMN_REPS),
                String.format("%s REAL NOT NULL", COLUMN_WEIGHT),
                foreignKey(COLUMN_ACTUAL_EXERCISE_ID, ActualExerciseEntry.TABLE_NAME, ActualExerciseEntry._ID, "RESTRICT", "RESTRICT")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ProgramTrainingEntry implements BaseColumns {
        public static final String TABLE_NAME = "program_training";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_WEEKDAY = "weekday";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s TEXT NOT NULL", COLUMN_NAME),
                String.format("%s INTEGER NULL", COLUMN_WEEKDAY)
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ProgramExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "program_exercise";
        public static final String COLUMN_PROGRAM_TRAINING_ID = "program_training_id";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";
        public static final String COLUMN_SORT_ORDER = "sort_order";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s INTEGER NOT NULL", COLUMN_PROGRAM_TRAINING_ID),
                String.format("%s INTEGER NOT NULL", COLUMN_EXERCISE_ID),
                String.format("%s INTEGER NOT NULL", COLUMN_SORT_ORDER),
                foreignKey(COLUMN_PROGRAM_TRAINING_ID, ProgramTrainingEntry.TABLE_NAME, ProgramTrainingEntry._ID, "CASCADE", "CASCADE"),
                foreignKey(COLUMN_EXERCISE_ID, ExerciseEntry.TABLE_NAME, ExerciseEntry._ID, "CASCADE", "RESTRICT")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }

    public static final class ProgramSetEntry implements BaseColumns {
        public static final String TABLE_NAME = "program_set";
        public static final String COLUMN_PROGRAM_EXERCISE_ID = "program_exercise_id";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_SECONDS_FOR_REST = "seconds_for_rest";
        public static final String COLUMN_SORT_ORDER = "sort_order";

        public static final String SQL_CREATE = createTableStatement(
                TABLE_NAME,
                primaryKey(),
                String.format("%s INTEGER NOT NULL", COLUMN_PROGRAM_EXERCISE_ID),
                String.format("%s INTEGER NOT NULL", COLUMN_REPS),
                String.format("%s INTEGER NULL", COLUMN_SECONDS_FOR_REST),
                String.format("%s INTEGER NOT NULL", COLUMN_SORT_ORDER),
                foreignKey(COLUMN_PROGRAM_EXERCISE_ID, ProgramExerciseEntry.TABLE_NAME, ProgramExerciseEntry._ID, "CASCADE", "CASCADE")
        );
        public static final String SQL_DROP = dropTableStatement(TABLE_NAME);
    }
}
