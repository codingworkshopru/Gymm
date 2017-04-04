package ru.codingworkshop.gymm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.codingworkshop.gymm.data.GymContract.ExerciseEntry;
import ru.codingworkshop.gymm.data.GymContract.MuscleGroupEntry;
import ru.codingworkshop.gymm.data.GymContract.ProgramExerciseEntry;
import ru.codingworkshop.gymm.data.GymContract.ProgramTrainingEntry;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.program.activity.training.ProgramTrainingActivity;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PROGRAM_TRAINING_ID_KEY = ProgramTraining.class.getCanonicalName() + ".id";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TEST_LOADER = 0;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(TEST_LOADER, null, this);

        RecyclerView rv = ((RecyclerView) findViewById(R.id.rv_test_main));
        rv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter();
        mAdapter.setHasStableIds(true);
        rv.setAdapter(mAdapter);

        Log.d(TAG, "onCreate");
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        Cursor mCursor;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.VH holder, int position) {
            mCursor.moveToPosition(position);
            holder.setText1(mCursor.getString(1));
            holder.setText2(mCursor.getString(3));
        }

        public void swapCursor(Cursor cursor) {
            if (mCursor != null)
                mCursor.close();
            mCursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(0);
        }

        @Override
        public int getItemCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        public static class VH extends RecyclerView.ViewHolder {
            public VH(final View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ActualTrainingActivity.class);
                        intent.putExtra(PROGRAM_TRAINING_ID_KEY, getItemId());
                        itemView.getContext().startActivity(intent);
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ProgramTrainingActivity.class);
                        intent.putExtra(PROGRAM_TRAINING_ID_KEY, getItemId());
                        itemView.getContext().startActivity(intent);
                        return true;
                    }
                });
            }

            public void setText1(String text) {
                ((TextView) itemView.findViewById(android.R.id.text1)).setText(text);
            }

            public void setText2(String text) {
                ((TextView) itemView.findViewById(android.R.id.text2)).setText(text);
            }
        }
    }


    // menu
    //-----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_program:
                Intent intent = new Intent(this, ProgramTrainingActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------

    // loader
    //-----------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                GymDbHelper dbHelper = new GymDbHelper(MainActivity.this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                QueryBuilder.QueryPart training = new QueryBuilder.QueryPart(ProgramTrainingEntry.TABLE_NAME)
                        .setColumns(ProgramTrainingEntry._ID, ProgramTrainingEntry.COLUMN_NAME, ProgramTrainingEntry.COLUMN_WEEKDAY)
                        .setGroup(ProgramTrainingEntry._ID);

                QueryBuilder.QueryPart programExercise = new QueryBuilder.QueryPart(ProgramExerciseEntry.TABLE_NAME)
                        .setThisJoinColumn(ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID);

                QueryBuilder.QueryPart exercise = new QueryBuilder.QueryPart(ExerciseEntry.TABLE_NAME)
                        .setThisJoinColumn(ExerciseEntry._ID)
                        .setOtherJoinColumn(ProgramExerciseEntry.COLUMN_EXERCISE_ID);

                QueryBuilder.QueryPart muscleGroup = new QueryBuilder.QueryPart(MuscleGroupEntry.TABLE_NAME)
                        .setColumns("group_concat(DISTINCT "+ MuscleGroupEntry.COLUMN_NAME + ")")
                        .setThisJoinColumn(MuscleGroupEntry._ID)
                        .setOtherJoinColumn(ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID);

                String query = QueryBuilder.build(training, programExercise, exercise, muscleGroup);
                Log.d(TAG, query);
                Cursor cursor = db.rawQuery(query, null);
                while (cursor.moveToNext()) {
                    Log.d(TAG, cursor.getLong(0) + " " + cursor.getString(1) + " " + cursor.getInt(2) + " " + cursor.getString(3));
                }
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        Log.d(TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        Log.d(TAG, "onLoaderReset");
    }
    //-----------------------------------------
}
