package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.List;

import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.OneToMany;
import io.requery.OneToOne;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface ActualTraining extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    @Column(value = "CURRENT_TIMESTAMP")
    Timestamp getStartTime();
    void setStartTime(Timestamp startTime);

    Timestamp getFinishTime();
    void setFinishTime(Timestamp finishTime);

    @OneToOne
    @ForeignKey
    @Nullable
    ProgramTraining getProgramTraining();
    void setProgramTraining(ProgramTraining programTraining);

    @OneToMany
    List<ActualExercise> getExercises();
    void setExercises(List<? extends ActualExercise> actualExercises);
}
