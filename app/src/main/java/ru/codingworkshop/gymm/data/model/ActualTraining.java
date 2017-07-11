package ru.codingworkshop.gymm.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import ru.codingworkshop.gymm.data.model.common.Model;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ActualTraining extends Model {
    Date getStartTime();
    void setStartTime(@NonNull Date startTime);

    @Nullable Date getFinishTime();
    void setFinishTime(Date finishTime);

    @Nullable Long getProgramTrainingId();
    void setProgramTrainingId(Long programTrainingId);
}
