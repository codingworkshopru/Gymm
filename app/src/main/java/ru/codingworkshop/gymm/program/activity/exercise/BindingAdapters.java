package ru.codingworkshop.gymm.program.activity.exercise;

import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import ru.codingworkshop.gymm.data.model.Exercise;

import static ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISES_INDEX_ID;
import static ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISES_INDEX_NAME;

/**
 * Created by Радик on 25.02.2017.
 */

final class BindingAdapters {
    @BindingAdapter(value = {"bind:value", "bind:valueAttrChanged"}, requireAll = false)
    public static void setSpinnerValue(Spinner spinner, Exercise exercise, final InverseBindingListener bindingListener) {
        Cursor cursor = ((SimpleCursorAdapter) spinner.getAdapter()).getCursor();

        if (cursor != null && exercise != null) {

            cursor.moveToFirst();
            do {
                if (cursor.getLong(EXERCISES_INDEX_ID) == exercise.getId())
                    break;
            } while (cursor.moveToNext());

            if (!cursor.isAfterLast())
                spinner.setSelection(cursor.getPosition(), false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bindingListener != null)
                    bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @InverseBindingAdapter(attribute = "bind:value", event = "bind:valueAttrChanged")
    public static Exercise captureSpinnerValue(Spinner spinner) {
        Cursor cursor = ((SimpleCursorAdapter) spinner.getAdapter()).getCursor();

        if (cursor == null)
            return null;

        cursor.moveToPosition(spinner.getSelectedItemPosition());

        Exercise exercise = new Exercise();
        exercise.setId(cursor.getLong(EXERCISES_INDEX_ID));
        exercise.setName(cursor.getString(EXERCISES_INDEX_NAME));

        return exercise;
    }
}
