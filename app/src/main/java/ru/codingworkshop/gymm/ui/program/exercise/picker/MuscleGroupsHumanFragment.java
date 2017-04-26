package ru.codingworkshop.gymm.ui.program.exercise.picker;


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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * A simple {@link Fragment} subclass.
 */
final public class MuscleGroupsHumanFragment extends Fragment {
    private static final String CLASS = MuscleGroupsHumanFragment.class.getSimpleName();
    static final String LAYOUT = CLASS + "_layout";
    static final String MAP = CLASS + "_map";
    static final String MUSCLES = CLASS + "_muscles";

    interface OnMuscleGroupSelectListener {
        void onMuscleGroupSelect(MuscleGroup muscleGroup);
    }

    private OnMuscleGroupSelectListener listener;

    private String title;
    private @LayoutRes int layout;
    private @IdRes int mapId;
    private List<MuscleGroup> muscleGroups;

    public MuscleGroupsHumanFragment() {

    }

    public static MuscleGroupsHumanFragment newInstance(String title, @LayoutRes int layout, @IdRes int mapId, List<MuscleGroup> muscleGroups) {
        Bundle args = new Bundle(3);
        args.putInt(LAYOUT, layout);
        args.putInt(MAP, mapId);
        args.putParcelableArrayList(MUSCLES, new ArrayList<Parcelable>(muscleGroups));

        MuscleGroupsHumanFragment fragment = new MuscleGroupsHumanFragment();
        fragment.setTitle(title); // this must be available before onCreateView call
        fragment.setArguments(args);

        return fragment;
    }

    public void init(Bundle args) {
        this.layout = args.getInt(LAYOUT);
        this.mapId = args.getInt(MAP);
        this.muscleGroups = args.getParcelableArrayList(MUSCLES);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(getArguments());
        View view = inflater.inflate(layout, container, false);

        if (getActivity() instanceof OnMuscleGroupSelectListener)
            listener = (OnMuscleGroupSelectListener) getActivity();
        else
            throw new ClassCastException("Activity must implement " + OnMuscleGroupSelectListener.class.getSimpleName() + " interface");

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    if (action == MotionEvent.ACTION_UP) {
                        View map = v.findViewById(mapId);

                        @ColorInt final int color = findColorOnMap(event, map);

                        MuscleGroup found = Iterables
                                .tryFind(muscleGroups, new Predicate<MuscleGroup>() {
                                    @Override
                                    public boolean apply(MuscleGroup muscleGroup) {
                                        return Color.parseColor(muscleGroup.getMapColorRgb()) == color;
                                    }
                                })
                                .orNull();

                        if (found != null)
                            listener.onMuscleGroupSelect(found);
                    }
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private @ColorInt int findColorOnMap(MotionEvent event, View map) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || x > map.getWidth() || y < 0 || y > map.getHeight())
            return Color.BLACK;

        map.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(map.getDrawingCache());
        map.setDrawingCacheEnabled(false);

        return b.getPixel(x, y);
    }
}
