package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.util.List;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToMany;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface ActualExercise extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    @ManyToOne
    Exercise getExercise();
    void setExercise(Exercise exercise);

    @ManyToOne
    ActualTraining getActualTraining();

    @OneToMany
    List<ActualSet> getActualSets();
    void setActualSets(List<? extends ActualSet> actualSets);
}
