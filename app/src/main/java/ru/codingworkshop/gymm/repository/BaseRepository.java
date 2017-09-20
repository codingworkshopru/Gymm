package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.WorkerThread;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.util.BiConsumer;
import ru.codingworkshop.gymm.data.util.Consumer;

/**
 * Created by Радик on 28.07.2017.
 */

@SuppressWarnings("Guava")
class BaseRepository {
    private Executor executor;

    BaseRepository(Executor executor) {
        this.executor = executor;
    }

    <T extends Model> void insert(T entity, Function<T, Long> insert) {
        perform(entity, insert, BaseRepository::afterInsertion);
    }

    <T extends Model> void insert(Collection<T> entities, Function<Collection<T>, List<Long>> insert) {
        perform(entities, insert, BaseRepository::afterInsertion);
    }

    <T extends Model> LiveData<Long> insertWithResult(T model, Function<T, Long> insert) {
        MutableLiveData<Long> liveId = new MutableLiveData<>();
        executor.execute(() -> liveId.postValue(performSync(model, insert, BaseRepository::afterInsertion)));
        return liveId;
    }

    <T extends Model> void update(T entity, Function<T, Integer> update) {
        perform(entity, update, null);
    }

    <T> void update(Collection<T> entities, Function<Collection<T>, Integer> update) {
        perform(entities, update, null);
    }

    <T> void delete(T entity, Function<T, Integer> delete) {
        perform(entity, delete, null);
    }

    private <T, F> void perform(T entity, Function<T, F> operation, BiConsumer<T, F> after) {
        executor.execute(() -> performSync(entity, operation, after));
    }

    @WorkerThread
    private static  <T, F> F performSync(T entity, Function<T, F> operation, BiConsumer<T, F> after) {
        Preconditions.checkNotNull(operation);
        Preconditions.checkNotNull(entity);

        F f = operation.apply(entity);
        if (after != null) {
            after.accept(entity, f);
        }

        return f;
    }

    private static <T extends Model> void afterInsertion(T entity, Long id) {
        checkInsertion(id);
        entity.setId(id);
    }

    private static <T extends Model> void afterInsertion(Collection<T> entities, List<Long> ids) {
        applyToEach(ids, BaseRepository::checkInsertion);
        applyRespectively(entities, ids, Model::setId);
    }

    private static void checkInsertion(Long id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(id != -1, "Insertion error!");
    }

    private static <T, F> void applyRespectively(Collection<T> implicitArguments, Collection<F> explicitArguments, BiConsumer<T, F> operation) {
        Iterator<T> implicitArgumentsIterator = implicitArguments.iterator();
        Iterator<F> explicitArgumentsIterator = explicitArguments.iterator();

        while (implicitArgumentsIterator.hasNext() && explicitArgumentsIterator.hasNext()) {
            T implicitArgument = implicitArgumentsIterator.next();
            F explicitArgument = explicitArgumentsIterator.next();
            operation.accept(implicitArgument, explicitArgument);
        }
    }

    static <T> void applyToEach(Collection<T> collection, Consumer<T> consumer) {
        for (T element : collection) {
            consumer.accept(element);
        }
    }
}
