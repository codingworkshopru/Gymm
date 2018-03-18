package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ExercisePickerViewModel viewModel;
    private ImageView mapImage;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
                .get(ExercisePickerViewModel.class);

        viewModel.getMuscleGroups(getArguments().getBoolean(ANTERIOR_KEY))
                .observe(this, this::onMuscleGroupsLoaded);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                        int muscleGroupColor;
                        try {
                            muscleGroupColor = Color.parseColor(mg.getMapColorRgb());
                        } catch (Throwable e) {
                            return false;
                        }
                        return color == muscleGroupColor;
                    });
                    if (found.isPresent()) {
                        viewModel.setMuscleGroup(found.get());
                    }
                    v.performClick();
                case MotionEvent.ACTION_DOWN:
                    return true;
                default:
                    return false;
            }
        });
    }

    @ColorInt
    private int findColorOnMap(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || x > mapImage.getWidth() || y < 0 || y > mapImage.getHeight())
            return Color.BLACK;

        mapImage.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(mapImage.getDrawingCache(false));
        mapImage.setDrawingCacheEnabled(false);
        int pixel = b.getPixel(x, y);
        b.recycle();
        return pixel;
    }

    public static MuscleGroupPickerFragment newInstance(boolean anterior) {
        Bundle args = new Bundle();
        args.putBoolean(ANTERIOR_KEY, anterior);
        MuscleGroupPickerFragment fragment = new MuscleGroupPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
