package ru.codingworkshop.gymm.data.model;

import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.common.Draftable;
import ru.codingworkshop.gymm.data.model.common.NamedModel;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ProgramTraining extends NamedModel, Draftable {
    @Nullable Integer getWeekday();
    void setWeekday(Integer weekday);
}
