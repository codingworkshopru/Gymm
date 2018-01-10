package ru.codingworkshop.gymm.ui.info.statistics.plot;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

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
import ru.codingworkshop.gymm.databinding.ActivityStatisticsPlotBinding;
import ru.codingworkshop.gymm.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class StatisticsPlotActivity extends AppCompatActivity {
    @Inject ViewModelFactory viewModelFactory;

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        StatisticsPlotViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(StatisticsPlotViewModel.class);
        viewModel.getChartEntries().observe(this, this::drawPlot);

        ActivityStatisticsPlotBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics_plot);
        binding.setLifecycleOwner(this);
        binding.setExercise(viewModel.getExerciseId());
        binding.setPeriod(viewModel.getRangeId());
        binding.setDataType(viewModel.getDataTypeId());
        binding.setActualExercises(viewModel.getActualExerciseNames());

        Toolbar toolbar = binding.statisticsPlotToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineChart = binding.statisticsPlot;

        initPlot();
    }

    private void initPlot() {
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
            Timber.d("There is no data to draw");
        }

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private int getColorFromAttr(@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
