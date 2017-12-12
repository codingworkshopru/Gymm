package ru.codingworkshop.gymm.ui.info.statistics;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.util.Consumer;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.ui.info.statistics.viewmodel.StatisticsViewModel;
import ru.codingworkshop.gymm.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class StatisticsActivity extends AppCompatActivity {
    @Inject ActualTrainingDao dao;
    @Inject ViewModelFactory viewModelFactory;
    private StatisticsViewModel viewModel;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initPlot();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StatisticsViewModel.class);
        viewModel.getActualExerciseNames().observe(this, this::onExerciseListLoaded);
        viewModel.chartEntries.observe(this, this::drawPlot);

        initSpinners();
    }

    private void initSpinners() {
        Spinner exercisesSpinner = findViewById(R.id.statisticsPlotExercises);
        exercisesSpinner.setOnItemSelectedListener(new SpinnerCallback(viewModel.exerciseId));

        Spinner dateRangeSpinner = findViewById(R.id.statisticsPlotPeriod);
        dateRangeSpinner.setOnItemSelectedListener(new SpinnerCallback(viewModel.rangeId));

        Spinner dataTypeSpinner = findViewById(R.id.statisticsPlotData);
        dataTypeSpinner.setOnItemSelectedListener(new SpinnerCallback(viewModel.dataTypeId));
    }

    private void initPlot() {
        lineChart = findViewById(R.id.statisticsPlot);

        // axis configuration
        lineChart.getAxisRight().setEnabled(false);

        final int axisLabelsColor = Color.parseColor("#97000000");

        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setValueFormatter(new LargeValueFormatter());
        axisLeft.setTextColor(axisLabelsColor);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setSpaceTop(70f);
        axisLeft.setSpaceBottom(70f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(axisLabelsColor);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisFormatter());
        xAxis.setLabelCount(3, false);

        // on empty text
        lineChart.setNoDataText(getString(R.string.statistics_activity_empty_plot_text));
        lineChart.setNoDataTextColor(getColorFromAttr(R.attr.colorPrimary));

        // other
        lineChart.setDescription(null);
        lineChart.getLegend().setEnabled(false);
    }

    private void onExerciseListLoaded(List<String> exercises) {
        Spinner exercisesSpinner = findViewById(R.id.statisticsPlotExercises);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exercisesSpinner.setAdapter(adapter);
        exercisesSpinner.setOnItemSelectedListener(new SpinnerCallback(viewModel.exerciseId));
    }

    private void drawPlot(List<Entry> entries) {
        LineData lineData = null;
        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, null);

            dataSet.setColor(getColorFromAttr(R.attr.colorPrimary));
            dataSet.setHighLightColor(getColorFromAttr(R.attr.colorAccent));
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);

            lineData = new LineData(dataSet);
        } else {
            Timber.w("There is no data to draw");
        }

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private int getColorFromAttr(@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    private static class SpinnerCallback implements AdapterView.OnItemSelectedListener {

        private Consumer<Long> idConsumer;

        SpinnerCallback(@NonNull MutableLiveData<Long> liveId) {
            this(liveId::setValue);
        }

        SpinnerCallback(@NonNull Consumer<Long> idConsumer) {
            this.idConsumer = idConsumer;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            idConsumer.accept(id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
