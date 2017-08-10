package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.Converters;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ActualTraining",
        foreignKeys = {
                @ForeignKey(
                        entity = ProgramTraining.class,
                        parentColumns = "id",
                        childColumns = "programTrainingId",
                        onDelete = SET_NULL,
                        onUpdate = CASCADE
                )
        },
        indices = @Index("programTrainingId")
)
@TypeConverters(Converters.class)
public class ActualTraining implements Model {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Date startTime;
    private Date finishTime;
    private Long programTrainingId;

    public ActualTraining(long programTrainingId, @NonNull Date startTime) {
        this.programTrainingId = programTrainingId;
        this.startTime = startTime;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull Date startTime) {
        this.startTime = startTime;
    }

    public @Nullable Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public @Nullable Long getProgramTrainingId() {
        return programTrainingId;
    }

    public void setProgramTrainingId(Long programTrainingId) {
        this.programTrainingId = programTrainingId;
    }
}
