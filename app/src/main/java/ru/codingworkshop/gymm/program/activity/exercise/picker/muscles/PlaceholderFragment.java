package ru.codingworkshop.gymm.program.activity.exercise.picker.muscles;

/**
 * Created by Радик on 21.03.2017.
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private List<MuscleGroup> muscleGroups;
    private int position;
    private OnMuscleGroupSelectListener muscleGroupSelectListener;

    public interface OnMuscleGroupSelectListener {
        void onMuscleGroupSelect(MuscleGroup muscleGroup);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MUSCLE_GROUPS = "muscle_groups";
    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, List<MuscleGroup> muscleGroups) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MUSCLE_GROUPS, new ArrayList<Parcelable>(muscleGroups));
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private @ColorInt int findColorOnMap(MotionEvent event, View map) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight())
            return 0;

        map.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(map.getDrawingCache());
        map.setDrawingCacheEnabled(false);

        return b.getPixel(x, y);
    }

    private MuscleGroup findMuscleGroupByColor(int color) {
        for (MuscleGroup mg : muscleGroups) {
            int anteriorGroup = mg.isAnterior() ? 1 : 0;
            if ((position + anteriorGroup == 1) && Color.parseColor(mg.getMapColorRgb()) == color)
                return mg;
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        muscleGroups = getArguments().getParcelableArrayList(ARG_MUSCLE_GROUPS);
        position = getArguments().getInt(ARG_SECTION_NUMBER);
        muscleGroupSelectListener = (OnMuscleGroupSelectListener) getActivity();

        @LayoutRes int fragmentLayoutId;
        @IdRes final int mapImage;

        switch (position) {
            case 0:
                fragmentLayoutId = R.layout.activity_program_exercise_picker_muscles_anterior;
                mapImage = R.id.imageView6;
                break;
            case 1:
                fragmentLayoutId = R.layout.activity_program_exercise_picker_muscles_posterior;
                mapImage = R.id.imageView8;
                break;
            default:
                throw new IllegalArgumentException("Wrong tab position");
        }

        View view = inflater.inflate(fragmentLayoutId, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        View map = v.findViewById(mapImage);

                        int color = findColorOnMap(event, map);
                        MuscleGroup muscleGroup = findMuscleGroupByColor(color);

                        if (muscleGroup != null) {
                            muscleGroupSelectListener.onMuscleGroupSelect(muscleGroup);
//                            Toast.makeText(PlaceholderFragment.this.getContext(), muscleGroup.getName(), Toast.LENGTH_SHORT).show();
                        }

                    case MotionEvent.ACTION_DOWN:
                        return true;

                    default:
                        return false;
                }
            }
        });
        return view;
    }
}
