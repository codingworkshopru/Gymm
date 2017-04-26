package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.util.List;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Lazy;
import io.requery.ManyToMany;
import io.requery.OneToMany;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface MuscleGroup extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    String getName();
    void setName(String name);

    String getMapColorRgb();
    void setMapColorRgb(String mapColorRgb);

    boolean getIsAnterior();
    void setIsAnterior(boolean isAnterior);

    @Lazy
    @OneToMany(mappedBy = "primaryMuscleGroup")
    List<Exercise> getExercisesForPrimary();

    @Lazy
    @ManyToMany(mappedBy = "secondaryMuscleGroups")
    List<Exercise> getExercisesForSecondary();
}
