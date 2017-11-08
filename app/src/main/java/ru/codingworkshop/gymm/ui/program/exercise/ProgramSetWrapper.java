package ru.codingworkshop.gymm.ui.program.exercise;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;

/**
 * Created by Radik on 08.11.2017.
 */
public class ProgramSetWrapper {
    private Context context;
    private ProgramSet programSet;

    ProgramSetWrapper(Context c, @NonNull ProgramSet programSet) {
        context = c;
        this.programSet = programSet;
    }

    public ProgramSet getProgramSet() {
        return programSet;
    }

    public int getReps() {
        return programSet.getReps();
    }

    public String getRestTime() {
        if (programSet.getSecondsForRest() == null) {
            return null;
        }

        return getPlural(context, R.plurals.minutes, programSet.getMinutes()) + ' ' +
                getPlural(context, R.plurals.seconds, programSet.getSeconds());
    }

    private String getPlural(Context context, @PluralsRes int plural, int arg) {
        return context.getResources().getQuantityString(plural, arg, arg);
    }
}
