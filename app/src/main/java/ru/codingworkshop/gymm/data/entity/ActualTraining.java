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
import ru.codingworkshop.gymm.data.entity.common.Named;
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
public class ActualTraining implements Model, Named {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private Date startTime;
    private Date finishTime;
    private String comment;
    @Nullable private Long programTrainingId;

    public ActualTraining(@Nullable Long programTrainingId, String name) {
        this.programTrainingId = programTrainingId;
        this.startTime = new Date();
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public @Nullable Long getProgramTrainingId() {
        return programTrainingId;
    }

    public void setProgramTrainingId(Long programTrainingId) {
        this.programTrainingId = programTrainingId;
    }
}
