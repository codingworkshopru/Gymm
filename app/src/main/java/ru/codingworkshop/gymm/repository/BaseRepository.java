package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.LongSupplier;

import ru.codingworkshop.gymm.data.util.Consumer;

/**
 * Created by Радик on 28.07.2017.
 */

@SuppressWarnings("Guava")
class BaseRepository {
    private Executor executor;
    @VisibleForTesting InsertWithIdAsyncTask asyncTask = new InsertWithIdAsyncTask();

    BaseRepository(Executor executor) {
        this.executor = executor;
    }

    <T> LiveData<Long> insertAndGetId(T entity, Function<T, Long> insert, Consumer<T> check) {
        LongSupplier insertOperation = () -> performSync(entity, insert, BaseRepository::checkInsertion, check);
        asyncTask.execute(insertOperation);
        return asyncTask.getLiveId();
    }

    <T> void insert(T entity, Function<T, Long> insert, Consumer<T> check) {
        perform(entity, insert, BaseRepository::checkInsertion, check);
    }

    <T> void insert(Collection<T> entities, Function<Collection<T>, List<Long>> insert, Consumer<T> check) {
        performForCollection(entities, insert, BaseRepository::checkInsertions, check);
    }

    <T> void update(T entity, Function<T, Integer> update, Consumer<T> check) {
        perform(entity, update, null, check);
    }

    <T> void update(Collection<T> entities, Function<Collection<T>, Integer> update, Consumer<T> check) {
        performForCollection(entities, update, null, check);
    }

    <T> void delete(T entity, Function<T, Integer> delete) {
        perform(entity, delete, null, null);
    }

    private <T, F> void performForCollection(Collection<T> entities, Function<Collection<T>, F> operation, Consumer<F> after, Consumer<T> before) {
        perform(entities, operation, after, e -> applyToEach(entities, before));
    }

    private <T, F> void perform(T entity, Function<T, F> operation, Consumer<F> after, Consumer<T> before) {
        executor.execute(() -> performSync(entity, operation, after, before));
    }

    @WorkerThread
    private <T, F> F performSync(T entity, Function<T, F> operation, Consumer<F> after, Consumer<T> before) {
        Preconditions.checkNotNull(operation);
        Preconditions.checkNotNull(entity);

        if (before != null) {
            before.accept(entity);
        }

        F f = operation.apply(entity);
        if (after != null) {
            after.accept(f);
        }

        return f;
    }

    private static void checkInsertions(List<Long> ids) {
        applyToEach(ids, BaseRepository::checkInsertion);
    }

    private static void checkInsertion(Long id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(id != -1, "Insertion error!");
    }

    private static <T> void applyToEach(Collection<T> collection, Consumer<T> consumer) {
        for (T element : collection) {
            consumer.accept(element);
        }
    }

    static class InsertWithIdAsyncTask extends AsyncTask<LongSupplier, Void, Long> {

        private MutableLiveData<Long> liveId;

        @Override
        protected Long doInBackground(LongSupplier[] suppliers) {
            return suppliers[0].getAsLong();
        }

        @Override
        protected void onPostExecute(Long id) {
            liveId = new MutableLiveData<>();
            liveId.setValue(id);
        }

        public LiveData<Long> getLiveId() {
            return liveId;
        }
    }
}
