package ru.codingworkshop.gymm.data.model;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.List;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToMany;
import io.requery.OrderBy;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface ProgramExercise extends Persistable, Parcelable, Orderable, Observable, Draftable {
    @Key
    @Generated
    long getId();

    int getSortOrder();
    void setSortOrder(int sortOrder);

    boolean isDrafting();
    void setDrafting(boolean drafting);

    @ManyToOne
    @Bindable
    Exercise getExercise();
    void setExercise(Exercise exercise);

    @ManyToOne
    ProgramTraining getProgramTraining();
    void setProgramTraining(ProgramTraining programTraining);

    @OneToMany
    @OrderBy(value = "sortOrder")
    List<ProgramSet> getSets();
    void setSets(List<? extends ProgramSet> sets);
}
