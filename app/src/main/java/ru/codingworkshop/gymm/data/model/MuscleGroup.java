package ru.codingworkshop.gymm.data.model;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.model.common.NamedModel;

/**
 * Created by Радик on 22.05.2017.
 */

public interface MuscleGroup extends NamedModel {
    String getMapColorRgb();
    void setMapColorRgb(@NonNull String mapColorRgb);

    boolean isAnterior();
    void setAnterior(boolean anterior);
}
