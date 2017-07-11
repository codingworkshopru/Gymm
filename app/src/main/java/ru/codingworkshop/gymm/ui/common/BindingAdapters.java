package ru.codingworkshop.gymm.ui.common;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import ru.codingworkshop.gymm.data.ExerciseDifficulty;

/**
 * Created by Радик on 03.06.2017.
 */

public class BindingAdapters {
    @BindingAdapter(value = {"android:value", "android:valueAttrChanged"}, requireAll = false)
    public static void setExerciseDifficulty(Spinner spinner, ExerciseDifficulty a, final InverseBindingListener l) {
        if (a != null) {
            setSpinnerValue(spinner, a.ordinal());
        }
        setSpinnerItemSelectedListener(spinner, l);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged")
    public static ExerciseDifficulty getExerciseDifficulty(Spinner spinner) {
        return ExerciseDifficulty.values()[spinner.getSelectedItemPosition()];
    }

    @BindingAdapter(value = {"android:value", "android:valueAttrChanged"}, requireAll = false)
    public static void setPrimaryMuscleGroup(Spinner spinner, long primaryMuscleGroup, final InverseBindingListener l) {
        setSpinnerValue(spinner, primaryMuscleGroup);
        setSpinnerItemSelectedListener(spinner, l);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged")
    public static long getPrimaryMuscleGroup(Spinner spinner) {
        return spinner.getSelectedItemId();
    }

    private static void setSpinnerValue(Spinner spinner, long value) {
        int position = getAdapterPositionById(spinner.getAdapter(), value);
        spinner.setSelection(position, false);
    }

    private static int getAdapterPositionById(SpinnerAdapter adapter, long id) {
        int position = -1;

        if (adapter == null || id == 0)
            return position;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItemId(i) == id) {
                position = i;
                break;
            }
        }

        return position;
    }

    private static void setSpinnerItemSelectedListener(Spinner spinner, InverseBindingListener listener) {
        if (spinner.getOnItemSelectedListener() == null && listener != null) {
            spinner.setOnItemSelectedListener(new SpinnerItemChangedListener(listener));
        }
    }

    private static class SpinnerItemChangedListener implements AdapterView.OnItemSelectedListener {
        private InverseBindingListener listener;

        private SpinnerItemChangedListener(InverseBindingListener listener) {
            this.listener = listener;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            listener.onChange();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
