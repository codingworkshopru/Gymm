package ru.codingworkshop.gymm.ui.actual.set;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;
import ru.codingworkshop.gymm.ui.common.EditTextValidator;

public class ActualSetFragment extends Fragment {

    private static final String TAG = ActualSetFragment.class.getCanonicalName();
    private static final String ACTUAL_SET_FRAGMENT_SET_INDEX = TAG + "index";
    private static final String ACTUAL_SET_FRAGMENT_SETS_COUNT = TAG + "count";
    private static final String ACTUAL_SET_ACTUAL_SET_MODEL = TAG + "actualSet";
    private static final String ACTUAL_SET_PROGRAM_SET_MODEL = TAG + "programSet";
    private static final String ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT = TAG + "isWithWeight";

    private boolean isWithWeight;
    private EditTextValidator repsValidator;
    private EditTextValidator weightValidator;
    private FragmentActualSetBinding binding;

    @VisibleForTesting
    OnActualSetSaveListener listener;

    public interface OnActualSetSaveListener {
        void onActualSetSave(int index, ActualSetDataBindingWrapper actualSet);
    }

    public static ActualSetFragment newInstance() {
        return new ActualSetFragment();
    }

    public static ActualSetFragment newInstance(Bundle arguments) {
        ActualSetFragment actualSetFragment = newInstance();
        actualSetFragment.setArguments(arguments);
        return actualSetFragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        notifyArgumentsChanged();
    }

    public void notifyArgumentsChanged() {
        Bundle args = getArguments();

        if (args == null || binding == null) {
            return;
        }

        ActualSet actualSet = Preconditions.checkNotNull(args.getParcelable(ACTUAL_SET_ACTUAL_SET_MODEL));
        ProgramSet programSet = args.getParcelable(ACTUAL_SET_PROGRAM_SET_MODEL);
        int index = args.getInt(ACTUAL_SET_FRAGMENT_SET_INDEX);
        final int setsCount = args.getInt(ACTUAL_SET_FRAGMENT_SETS_COUNT);
        if (programSet != null) {
            if (actualSet.getReps() == 0) {
                actualSet.setReps(programSet.getReps());
            }
            index = programSet.getSortOrder();
            binding.setProgramSet(programSet);
        } else {
            Preconditions.checkArgument(index >= setsCount, "Index must be more or equal program sets count");
        }
        binding.setActualSet(new ActualSetDataBindingWrapper(actualSet));
        binding.setSetsCount(setsCount);
        binding.setIndex(index);
        isWithWeight = args.getBoolean(ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT);
    }

    @Nullable
    public FragmentActualSetBinding getBinding() {
        return binding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actual_set, container, false);
        notifyArgumentsChanged();

        View root = binding.getRoot();

        root.findViewById(R.id.actualSetDoneButton).setOnClickListener(this::onDoneButtonClick);

        repsValidator = new EditTextValidator(binding.actualSetRepsCountLayout,
                R.string.actual_training_activity_stepper_item_reps_error);

        weightValidator = new EditTextValidator(binding.actualSetWeightLayout);
        weightValidator.addValidation(
                s -> !isWithWeight || !TextUtils.isEmpty(s),
                R.string.actual_training_activity_stepper_item_weight_error);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnActualSetSaveListener) {
            listener = (OnActualSetSaveListener) parentFragment;
        } else if (listener == null) {
            throw new IllegalStateException("Activity must implement callback interface: "
                    + OnActualSetSaveListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void onDoneButtonClick(View view) {
        final View rootView = getView();
        if (rootView == null) return;

        final boolean repsValid = repsValidator.validate();
        final boolean weightValid = weightValidator.validate();
        if (repsValid && weightValid) {
            listener.onActualSetSave(binding.getIndex(), binding.getActualSet());
        }
    }

    public static final class ArgumentsBuilder {
        private ProgramSet programSet;
        private ActualSet actualSet;
        private int setIndex;
        private int setsCount;
        private boolean isWithWeight;

        public ArgumentsBuilder setProgramSet(ProgramSet programSet) {
            this.programSet = programSet;
            return this;
        }

        public ArgumentsBuilder setActualSet(ActualSet actualSet) {
            this.actualSet = actualSet;
            return this;
        }

        public ArgumentsBuilder setSetIndex(int setIndex) {
            this.setIndex = setIndex;
            return this;
        }

        public ArgumentsBuilder setSetsCount(int setsCount) {
            this.setsCount = setsCount;
            return this;
        }

        public ArgumentsBuilder setWithWeight(boolean withWeight) {
            isWithWeight = withWeight;
            return this;
        }

        public Bundle build() {
            Preconditions.checkNotNull(actualSet);
            Preconditions.checkState(setsCount != 0);

            Bundle args = new Bundle();
            args.putParcelable(ACTUAL_SET_PROGRAM_SET_MODEL, programSet);
            args.putParcelable(ACTUAL_SET_ACTUAL_SET_MODEL, actualSet);
            args.putInt(ACTUAL_SET_FRAGMENT_SET_INDEX, setIndex);
            args.putInt(ACTUAL_SET_FRAGMENT_SETS_COUNT, setsCount);
            args.putBoolean(ACTUAL_SET_FRAGMENT_IS_WITH_WEIGHT, isWithWeight);

            return args;
        }
    }

}
