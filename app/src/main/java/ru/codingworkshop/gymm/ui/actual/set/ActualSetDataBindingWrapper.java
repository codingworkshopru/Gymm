package ru.codingworkshop.gymm.ui.actual.set;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.common.Model;

/**
 * Created by Радик on 01.09.2017 as part of the Gymm project.
 */

// TODO replace with binding adapters when gradle plugin 3+ will be released
public class ActualSetDataBindingWrapper extends BaseObservable implements Model {
    private ActualSet actualSet;

    public ActualSetDataBindingWrapper(@NonNull ActualSet actualSet) {
        this.actualSet = Preconditions.checkNotNull(actualSet);
    }

    public ActualSet unwrap() {
        return actualSet;
    }

    @Bindable
    @Override
    public long getId() {
        return actualSet.getId();
    }

    @Override
    public void setId(long id) {
        actualSet.setId(id);
        notifyChange();
    }

    public String getReps() {
        return actualSet.getReps() == 0 ? "" : Integer.toString(actualSet.getReps());
    }

    public void setReps(String reps) {
        actualSet.setReps(TextUtils.isEmpty(reps) ? 0 : Integer.valueOf(reps));
    }

    @Nullable
    public String getWeight() {
        return actualSet.getWeight() != null ? actualSet.getWeight().toString() : null;
    }

    public void setWeight(String weight) {
        actualSet.setWeight(TextUtils.isEmpty(weight) ? null : Double.valueOf(weight));
    }
}
