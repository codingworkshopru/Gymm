package ru.codingworkshop.gymm.ui.actual;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;

public class ActualSetFragment extends Fragment {

    private static final String TAG = ActualSetFragment.class.getCanonicalName();
    private static final String ACTUAL_SET_FRAGMENT_INDEX = TAG + "index";
    private static final String ACTUAL_SET_FRAGMENT_COUNT = TAG + "count";
    private static final String ACTUAL_SET_FRAGMENT_REPS = TAG + "reps";

    private int index;
    private int count;
    private int reps;

    public static ActualSetFragment newInstance(int index, int count) {
        return newInstance(index, count, 0);
    }

    public static ActualSetFragment newInstance(int index, int count, int reps) {
        final ActualSetFragment actualSetFragment = new ActualSetFragment();
        Bundle args = new Bundle();
        args.putInt(ACTUAL_SET_FRAGMENT_INDEX, index);
        args.putInt(ACTUAL_SET_FRAGMENT_COUNT, count);
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
        return binding.getRoot();
    }
}
