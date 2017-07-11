package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.codingworkshop.gymm.data.ExerciseDifficulty;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.db.Converters;

/**
 * Created by Радик on 22.05.2017.
 */

@Entity(tableName = "Exercise", indices = {@Index("primaryMuscleGroupId"), @Index(value = "name", unique = true)},
foreignKeys = @ForeignKey(
        entity = MuscleGroupEntity.class,
        parentColumns = "id",
        childColumns = "primaryMuscleGroupId")
)
@TypeConverters({Converters.class})
public class ExerciseEntity implements Exercise {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private ExerciseDifficulty difficulty;
    private long primaryMuscleGroupId;
    @SerializedName("isWithWeight")
    private boolean withWeight;
    private String youTubeVideo;
    private String steps;
    private String advices;
    private String variations;
    private String caution;

    // fields for creating muscle groups <-> exercise links by DB initializer (with Gson)
    @Ignore
    public String primaryMuscle;
    @Ignore
    public List<String> secondaryMuscles;

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public ExerciseDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(@NonNull ExerciseDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public long getPrimaryMuscleGroupId() {
        return primaryMuscleGroupId;
    }

    @Override
    public void setPrimaryMuscleGroupId(long primaryMuscleGroupId) {
        this.primaryMuscleGroupId = primaryMuscleGroupId;
    }

    @Override
    public boolean isWithWeight() {
        return withWeight;
    }

    @Override
    public void setWithWeight(boolean withWeight) {
        this.withWeight = withWeight;
    }

    @Override
    @Nullable public String getYouTubeVideo() {
        return youTubeVideo;
    }

    public void setYouTubeVideo(String youTubeVideo) {
        this.youTubeVideo = youTubeVideo;
    }

    @Override
    @Nullable public String getSteps() {
        return steps;
    }

    @Override
    public void setSteps(String steps) {
        this.steps = steps;
    }

    @Override
    @Nullable public String getAdvices() {
        return advices;
    }

    @Override
    public void setAdvices(String advices) {
        this.advices = advices;
    }

    @Override
    @Nullable public String getVariations() {
        return variations;
    }

    @Override
    public void setVariations(String variations) {
        this.variations = variations;
    }

    @Override
    @Nullable public String getCaution() {
        return caution;
    }

    @Override
    public void setCaution(String caution) {
        this.caution = caution;
    }
}
