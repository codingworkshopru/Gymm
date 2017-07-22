package ru.codingworkshop.gymm.data.model;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.model.common.Model;
import ru.codingworkshop.gymm.data.model.common.Named;

/**
 * Created by Радик on 22.05.2017.
 */

public interface MuscleGroup extends Named, Model {
    String getMapColorRgb();
    void setMapColorRgb(@NonNull String mapColorRgb);

    boolean isAnterior();
    void setAnterior(boolean anterior);
}
