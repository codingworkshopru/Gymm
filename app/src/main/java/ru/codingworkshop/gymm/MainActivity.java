package ru.codingworkshop.gymm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.requery.query.Result;
import ru.codingworkshop.gymm.data.ModelLoader;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingActivity;

public class MainActivity extends AppCompatActivity implements ModelLoader.ModelLoaderCallbacks<ProgramTraining> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TEST_LOADER = 0;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));

        new ModelLoader<>(this, ProgramTraining.class, this).queryAll();

        RecyclerView rv = ((RecyclerView) findViewById(R.id.rv_test_main));
        rv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter();
        mAdapter.setHasStableIds(true);
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onModelLoadFinished(Result<ProgramTraining> data) {
        mAdapter.setTrainings(data.toList());
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        List<ProgramTraining> trainings;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.VH holder, int position) {
            ProgramTraining training = trainings.get(position);
            holder.setText1(training.getName());
            holder.setText2(holder.itemView.getResources().getStringArray(R.array.days_of_week)[training.getWeekday()]);
        }

        public void setTrainings(List<ProgramTraining> trainings) {
            this.trainings = trainings;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return trainings.get(position).getId();
        }

        @Override
        public int getItemCount() {
            return trainings != null ? trainings.size() : 0;
        }

        public static class VH extends RecyclerView.ViewHolder {
            public VH(final View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ActualTrainingActivity.class);
                        intent.putExtra(ProgramTrainingActivity.PROGRAM_TRAINING_ID, getItemId());
                        itemView.getContext().startActivity(intent);
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ProgramTrainingActivity.class);
                        intent.putExtra(ProgramTrainingActivity.PROGRAM_TRAINING_ID, getItemId());
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
}
