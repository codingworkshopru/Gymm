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
public interface ProgramSet extends Persistable, Parcelable, Orderable, Draftable {
    @Key
    @Generated
    long getId();

    int getReps();
    void setReps(int reps);

    int getSecondsForRest();
    void setSecondsForRest(int secondsForRest);

    int getSortOrder();
    void setSortOrder(int sortOrder);

    boolean isDrafting();
    void setDrafting(boolean drafting);

    @ManyToOne
    ProgramExercise getProgramExercise();
    void setProgramExercise(ProgramExercise programExercise);
}
