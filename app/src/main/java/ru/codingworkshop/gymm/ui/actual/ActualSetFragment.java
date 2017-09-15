package ru.codingworkshop.gymm.ui.actual;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;

public class ActualSetFragment extends Fragment {

    private static final String TAG = ActualSetFragment.class.getCanonicalName();
    private static final String ACTUAL_SET_FRAGMENT_INDEX = TAG + "index";
    private static final String ACTUAL_SET_FRAGMENT_COUNT = TAG + "count";
    private static final String ACTUAL_SET_FRAGMENT_REPS = TAG + "reps";
    private static final String ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT = TAG + "isWithWeight";

    private int index;
    private int count;
    private int reps;
    private boolean isWithWeight;
    private EditTextValidator repsValidator;
    private EditTextValidator weightValidator;

    public static ActualSetFragment newInstance(int index, int count, boolean isWithWeight) {
        return newInstance(index, count, isWithWeight, 0);
    }

    public static ActualSetFragment newInstance(int index, int count, boolean isWithWeight, int reps) {
        final ActualSetFragment actualSetFragment = new ActualSetFragment();
        Bundle args = new Bundle();
        args.putInt(ACTUAL_SET_FRAGMENT_INDEX, index);
        args.putInt(ACTUAL_SET_FRAGMENT_COUNT, count);
        args.putBoolean(ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT, isWithWeight);
        args.putInt(ACTUAL_SET_FRAGMENT_REPS, reps);
        actualSetFragment.setArguments(args);
        return actualSetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt(ACTUAL_SET_FRAGMENT_INDEX);
            count = arguments.getInt(ACTUAL_SET_FRAGMENT_COUNT);
            isWithWeight = arguments.getBoolean(ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT);
            reps = arguments.getInt(ACTUAL_SET_FRAGMENT_REPS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActualSetBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actual_set, container, false);
        binding.setIndex(index);
        binding.setSetsCount(count);
        binding.setReps(reps == 0 ? null : reps);

        View root = binding.getRoot();

        root.findViewById(R.id.actualSetDoneButton).setOnClickListener(this::onDoneButtonClick);

        repsValidator = new EditTextValidator(root.findViewById(R.id.actualSetRepsCountLayout),
                R.string.actual_training_activity_stepper_item_reps_error);

        weightValidator = new EditTextValidator(root.findViewById(R.id.actualSetWeightLayout),
                R.string.actual_training_activity_stepper_item_weight_error) {
            @Override
            protected boolean isValid(CharSequence textEditContents) {
                return !isWithWeight || super.isValid(textEditContents);
            }
        };

        return root;
    }

    private void onDoneButtonClick(View view) {
        final View rootView = getView();
        if (rootView == null) return;

        repsValidator.validate();
        weightValidator.validate();
    }

    private static class EditTextValidator {
        private TextInputLayout textInputLayout;
        private String errorText;

        public EditTextValidator(TextInputLayout textInputLayout, @StringRes int errorText) {
            this.textInputLayout = textInputLayout;
            this.errorText = textInputLayout.getContext().getString(errorText);

            textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isValid(s)) {
                        setShowError(false);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void afterTextChanged(Editable s) {}
            });
        }

        public void validate() {
            final EditText editText = textInputLayout.getEditText();
            if (editText == null) return;

            setShowError(!isValid(editText.getText()));
        }

        protected boolean isValid(CharSequence textEditContents) {
            return textEditContents.length() != 0;
        }

        private void setShowError(boolean show) {
            CharSequence currentError = textInputLayout.getError();
            if (show && currentError != null) return;
            if (!show && currentError == null) return;
            textInputLayout.setError(show ? errorText : null);
        }
    }
}
