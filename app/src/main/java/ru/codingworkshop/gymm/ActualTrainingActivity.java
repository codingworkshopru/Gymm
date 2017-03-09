package ru.codingworkshop.gymm;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.program.activity.training.TrainingAsyncLoader;

public class ActualTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ProgramTraining> {
    private ProgramTraining mProgramTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new BaseAdapter() {
            List<ViewHolder> viewHolders = new ArrayList<>();
            int activeStepPosition = 0;
            @Override
            public int getCount() {
                return mProgramTraining != null ? mProgramTraining.childrenCount() : 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                ViewHolder vh = findViewHolder(convertView);

                if (vh == null)
                    vh = new ViewHolder(inflater.inflate(R.layout.activity_actual_training_stepper_item, parent, false));

                vh.setIndexNumber(position + 1);
                vh.setActiveStep(position == activeStepPosition);
                vh.setTitle(mProgramTraining.getChild(position).getExercise().getName());
                if (position == getCount() - 1)
                    vh.setVerticalLineVisible(false);


                return vh.itemView;
            }

            private ViewHolder findViewHolder(View view) {
                if (view == null)
                    return null;

                for (ViewHolder vh : viewHolders) {
                    if (vh.itemView == view)
                        return vh;
                }

                return null;
            }
        });

        getSupportLoaderManager().initLoader(TrainingAsyncLoader.LOADER_TRAINING_LOAD, getIntent().getExtras(), this);
}

    @Override
    public Loader<ProgramTraining> onCreateLoader(int id, Bundle args) {
        return new TrainingAsyncLoader(this, id, args, null);
    }

    @Override
    public void onLoadFinished(Loader<ProgramTraining> loader, ProgramTraining data) {
        mProgramTraining = data;
        ((BaseAdapter) ((ListView) findViewById(R.id.list_view)).getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ProgramTraining> loader) {

    }

    static class ViewHolder {
        TextView indexNumber;
        TextView title;
        View itemView;
        ImageView verticalLine;
        ImageView stepperCircle;

        public ViewHolder(View view) {
            itemView = view;
            indexNumber = (TextView) view.findViewById(R.id.textView4);
            verticalLine = (ImageView) view.findViewById(R.id.imageView3);
            stepperCircle = (ImageView) view.findViewById(R.id.imageView2);
            title = (TextView) view.findViewById(R.id.textView3);
        }

        public void setIndexNumber(int number) {
            indexNumber.setText(String.valueOf(number));
        }

        public void setVerticalLineVisible(boolean visible) {
            verticalLine.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setActiveStep(boolean active) {
            @DrawableRes int imageId;
            @StyleRes int mainTextStyle;

            if (active) {
                imageId = R.drawable.ic_circle_primary_24dp;
                mainTextStyle = R.style.TextAppearance_AppCompat_Body2;
            } else {
                imageId = R.drawable.ic_circle_grey_24dp;
                mainTextStyle = R.style.TextAppearance_AppCompat_Small;
            }

            stepperCircle.setImageResource(imageId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                title.setTextAppearance(itemView.getContext(), mainTextStyle);
            else
                title.setTextAppearance(mainTextStyle);
        }

        public void setTitle(String text) {
            title.setText(text);
        }
    }
}
