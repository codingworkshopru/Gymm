package ru.codingworkshop.gymm.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 26.10.2017 as part of the Gymm project.
 */

public class CustomisableNumberPicker extends NumberPicker {
    private int step;

    public CustomisableNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomisableNumberPicker, 0, 0);

        try {
            final int min = a.getInteger(R.styleable.CustomisableNumberPicker_minValue, 0);
            setMinValue(min);

            step = a.getInteger(R.styleable.CustomisableNumberPicker_step, 1);

            if (a.hasValue(R.styleable.CustomisableNumberPicker_maxValue)) {
                final int max = a.getInteger(R.styleable.CustomisableNumberPicker_maxValue, 0);
                setMaxValue((max - min) / step);
            } else {
                throw new IllegalArgumentException("max value must be presented");
            }
        } finally {
            a.recycle();
        }

        if (step != 1) {
            int min = getMinValue();
            int max = getMaxValue();
            List<Integer> labels = new LinkedList<>();
            for (int i = min; i <= max; i++) {
                labels.add(i * step);
            }
            setDisplayedIntValues(labels);
        }
    }

    public int getDisplayedValue() {
        return getValue() * step;
    }

    public void setDisplayedValue(int displayedValue) {
        setValue(displayedValue / step);
    }

    public void setDisplayedIntValues(List<Integer> values) {
        setDisplayedValues(Lists.transform(values, Object::toString));
    }

    public void setDisplayedValues(List<String> values) {
        int size = values.size();
        String[] a = new String[size];

        setDisplayedValues(values.toArray(a));
    }

    @BindingAdapter("android:value")
    public static void setValue(CustomisableNumberPicker picker, int oldValue, int newValue) {
        picker.setDisplayedValue(newValue);
    }

    @InverseBindingAdapter(attribute = "android:value")
    public static int getValue(CustomisableNumberPicker picker) {
        return picker.getDisplayedValue();
    }
}
