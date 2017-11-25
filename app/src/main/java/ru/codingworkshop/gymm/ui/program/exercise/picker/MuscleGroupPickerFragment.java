package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;

/**
 * Created by Radik on 22.11.2017.
 */

public class MuscleGroupPickerFragment extends Fragment {
    private static final String ANTERIOR_KEY = "anteriorKey";

    public interface OnMuscleGroupPickListener {
        void onMuscleGroupPick(MuscleGroup muscleGroup);
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @VisibleForTesting
    OnMuscleGroupPickListener listener;
    private MuscleGroupPickerViewModel viewModel;
    private ImageView mapImage;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        if (listener == null) {
            if (context instanceof OnMuscleGroupPickListener) {
                listener = (OnMuscleGroupPickListener) context;
            } else if (getParentFragment() instanceof OnMuscleGroupPickListener) {
                listener = (OnMuscleGroupPickListener) getParentFragment();
            } else {
                throw new IllegalStateException("parent must implement " + OnMuscleGroupPickListener.class + " interface");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MuscleGroupPickerViewModel.class);

        viewModel.load(getArguments().getBoolean(ANTERIOR_KEY))
                .observe(this, this::onMuscleGroupsLoaded);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean isAnterior = getArguments().getBoolean(ANTERIOR_KEY);

        View root = inflater.inflate(R.layout.fragment_muscle_group_picker, container, false);

        ImageView musclesImage = root.findViewById(R.id.muscleGroupPickerHumanMuscles);
        musclesImage.setImageResource(isAnterior ? R.drawable.human_muscles_anterior : R.drawable.human_muscles_posterior);

        mapImage = root.findViewById(R.id.muscleGroupPickerMap);
        mapImage.setImageResource(isAnterior ? R.drawable.muscle_labels_map_anterior : R.drawable.muscle_labels_map_posterior);

        return root;
    }

    private void onMuscleGroupsLoaded(List<MuscleGroup> muscleGroups) {
        if (muscleGroups == null || muscleGroups.isEmpty()) return;

        getView().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    int color = findColorOnMap(event);
                    Optional<MuscleGroup> found = Iterables.tryFind(muscleGroups, mg -> {
                        int muscleGroupColor = Color.parseColor(mg.getMapColorRgb());
                        return color == muscleGroupColor;
                    });
                    if (found.isPresent()) {
                        listener.onMuscleGroupPick(found.get());
                    }
                    v.performClick();
                case MotionEvent.ACTION_DOWN:
                    return true;
            }

            return false;
        });
    }

    private @ColorInt int findColorOnMap(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || x > mapImage.getWidth() || y < 0 || y > mapImage.getHeight())
            return Color.BLACK;

        mapImage.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(mapImage.getDrawingCache());
        mapImage.setDrawingCacheEnabled(false);

        return b.getPixel(x, y);
    }

    public static MuscleGroupPickerFragment newInstance(boolean anterior) {
        Bundle args = new Bundle();
        args.putBoolean(ANTERIOR_KEY, anterior);
        MuscleGroupPickerFragment fragment = new MuscleGroupPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
