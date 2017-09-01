package ru.codingworkshop.gymm.ui.actual;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.ActualSet;

/**
 * Created by Радик on 01.09.2017 as part of the Gymm project.
 */

// TODO replace with binding adapters when gradle plugin 3+ will be released
public class ActualSetObservable {
    private ActualSet actualSet;

    public ActualSetObservable(@NonNull ActualSet actualSet) {
        this.actualSet = Preconditions.checkNotNull(actualSet);
    }

    public ActualSet unwrap() {
        return actualSet;
    }

    public String getReps() {
        final int reps = actualSet.getReps();
        return reps == 0 ? "" : Integer.toString(reps);
    }

    public void setReps(String reps) {
        actualSet.setReps(Integer.valueOf(reps));
    }

    @Nullable
    public String getWeight() {
        return actualSet.getWeight() != null ? actualSet.getWeight().toString() : null;
    }

    public void setWeight(String weight) {
        actualSet.setWeight(Double.valueOf(weight));
    }
}
