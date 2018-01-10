package ru.codingworkshop.gymm.ui.info.statistics.plot;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;

import com.github.mikephil.charting.data.Entry;
import com.google.common.base.Function;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.codingworkshop.gymm.data.entity.ExercisePlotTuple;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import timber.log.Timber;

/**
 * Created by Radik on 11.12.2017.
 */

public class StatisticsPlotViewModel extends ViewModel {
    private final MutableLiveData<Long> exerciseId = new MutableLiveData<>();
    private final MutableLiveData<Long> rangeId = new MutableLiveData<>();
    private final MutableLiveData<Long> dataTypeId = new MutableLiveData<>();

    private final MediatorLiveData<List<Entry>> chartEntries = new MediatorLiveData<>();

    private final ActualTrainingRepository repository;
    private LiveData<List<String>> actualExerciseNames;
    private Disposable subscribe;

    @Inject
    StatisticsPlotViewModel(ActualTrainingRepository repository) {
        this.repository = repository;
        chartEntries.addSource(exerciseId, this::onFilterEvent);
        chartEntries.addSource(rangeId, this::onFilterEvent);
        chartEntries.addSource(dataTypeId, this::onFilterEvent);
    }

    public MutableLiveData<Long> getExerciseId() {
        return exerciseId;
    }

    public MutableLiveData<Long> getRangeId() {
        return rangeId;
    }

    public MutableLiveData<Long> getDataTypeId() {
        return dataTypeId;
    }

    public MediatorLiveData<List<Entry>> getChartEntries() {
        return chartEntries;
    }

    private void onFilterEvent(@Nullable Long id) {
        Long exercise = exerciseId.getValue();
        Long range = rangeId.getValue();
        Long data = dataTypeId.getValue();

        if (exercise == null || range == null || data == null) {
            return;
        }

        Timber.d("onFilterEvent()");

        onCleared();

        subscribe = repository.getStatisticsForExercise(getExerciseNameById(exercise), getStartDateById(range))
                .take(1)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(tuple -> Pair.create(tuple.getTrainingTime(), getDataFunctionById(data).apply(tuple)))
                .filter(pair -> pair.second != null)
                .groupBy(pair -> pair.first, pair -> pair.second.floatValue())
                .flatMap(dateNumberGroupedFlowable ->
                        MathObservable
                                .sumFloat(dateNumberGroupedFlowable)
                                .map(n -> new Entry(dateNumberGroupedFlowable.getKey().getTime(), n)))
                .toSortedList((o1, o2) -> Float.compare(o1.getX(), o2.getX()))
                .subscribe(chartEntries::postValue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }

    public LiveData<List<String>> getActualExerciseNames() {
        if (actualExerciseNames == null) {
            actualExerciseNames = repository.getActualExerciseNames();
        }
        return actualExerciseNames;
    }

    @VisibleForTesting
    String getExerciseNameById(@Nullable Long actualExerciseNamePosition) {
        return actualExerciseNames.getValue().get(longInstanceToInt(actualExerciseNamePosition));
    }

    /**
     * Returns start date for statistics by position in the array.
     * @param rangeSizePosition position in arrays.xml for periods
     * @return start date for statistics. Default is null.
     */
    @VisibleForTesting
    @Nullable static Date getStartDateById(@Nullable Long rangeSizePosition) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        switch (longInstanceToInt(rangeSizePosition)) {
            case 0:
                c.add(Calendar.MONTH, -1);
                break;

            case 1:
                c.add(Calendar.MONTH, -3);
                break;

            case 2:
                c.add(Calendar.MONTH, -6);
                break;

            case 3:
                c.add(Calendar.MONTH, -9);
                break;

            case 4:
                c.add(Calendar.YEAR, -1);
                break;

            case 5:
                return null;

            default:
                throw new IllegalArgumentException("this range option is not provided");
        }
        return c.getTime();
    }

    /**
     *
     * @param dataProviderFunctionId id of data type function corresponding in arrays.xml
     * @return function computing value for one row (workout set)
     */
    @VisibleForTesting
    @SuppressWarnings("Guava")
    static Function<ExercisePlotTuple, Number> getDataFunctionById(@Nullable Long dataProviderFunctionId) {
        switch (longInstanceToInt(dataProviderFunctionId)) {
            case 0:
                return (tuple) -> {
                    return tuple == null || tuple.getWeight() == null
                            ? null
                            : tuple.getReps() * tuple.getWeight();
                };

            case 1:
                return ExercisePlotTuple::getWeight;

            case 2:
                return ExercisePlotTuple::getReps;

            default:
                throw new IllegalArgumentException("this function is not provided");
        }
    }

    private static int longInstanceToInt(@Nullable Long value) {
        return value != null ? value.intValue() : 0;
    }
}
