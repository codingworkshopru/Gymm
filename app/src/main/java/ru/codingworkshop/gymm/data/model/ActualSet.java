package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface ActualSet extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    int getReps();
    void setReps(int reps);

    double getWeight();
    void setWeight(double weight);

    @ManyToOne
    ActualExercise getActualExercise();
}
