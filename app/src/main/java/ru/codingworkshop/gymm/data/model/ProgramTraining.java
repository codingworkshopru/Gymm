package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.util.List;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.OrderBy;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface ProgramTraining extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    String getName();
    void setName(String name);

    int getWeekday();
    void setWeekday(int weekday);

    @OneToMany
    @OrderBy(value = "sortOrder")
    List<ProgramExercise> getExercises();
    void setExercises(List<? extends ProgramExercise> exercises);
}
