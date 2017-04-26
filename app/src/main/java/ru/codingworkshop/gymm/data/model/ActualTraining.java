package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.util.Date;
import java.util.List;

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

    Date getStartTime();
    void setStartTime(Date startTime);

    Date getFinishTime();
    void setFinishTime(Date finishTime);

    @OneToOne
    @ForeignKey
    @Nullable
    ProgramTraining getProgramTraining();
    void setProgramTraining(ProgramTraining programTraining);

    @OneToMany
    List<ActualExercise> getExercises();
    void setExercises(List<? extends ActualExercise> actualExercises);
}
