package ru.codingworkshop.gymm.ui.common;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.common.base.Predicate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

public class EditTextValidator {
    private TextInputLayout layout;
    private EditText editText;
    private Map<Predicate<String>, Integer> errorsWithValidators;

    public EditTextValidator(TextInputLayout textInputLayout) {
        layout = textInputLayout;
        errorsWithValidators = new HashMap<>();

        editText = layout.getEditText();
        if (editText == null) {
            throw new NullPointerException("EditText is null");
        }
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getErrorString() == null) {
                    setError(null);
//                    editText.removeTextChangedListener(this);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    public EditTextValidator(TextInputLayout textInputLayout, @StringRes int onEmptyErrorString) {
        this(textInputLayout);
        addValidation(str -> !TextUtils.isEmpty(str), onEmptyErrorString);
    }

    public boolean validate() {
        String errorString = getErrorString();
        setError(errorString);
        return errorString == null;
    }

    private String getErrorString() {
        for (Predicate<String> validator : errorsWithValidators.keySet()) {
            if (!validator.apply(editText.getText().toString())) {
                Integer errorStringId = errorsWithValidators.get(validator);
                return layout.getContext().getString(errorStringId);
            }
        }

        return null;
    }

    public void addValidation(@NonNull Predicate<String> validator, @StringRes int errorText) {
        errorsWithValidators.put(validator, errorText);
    }

    private void setError(String errorText) {
        layout.setError(errorText);
    }
}
