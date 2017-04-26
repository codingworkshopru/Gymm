package ru.codingworkshop.gymm.data;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.common.collect.Lists;

import java.util.List;

import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.query.Result;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;

/**
 * Created by Радик on 26.04.2017.
 */

public class ModelLoader<M extends Persistable> implements LoaderManager.LoaderCallbacks<Result<M>> {
    public interface ModelLoaderCallbacks<M> {
        void onModelLoadFinished(Result<M> data);
    }

    private final static int QUERY_ALL = 0;
    private final static int QUERY_BY_ID = 1;

    private final AppCompatActivity activity;
    private final EntityDataStore<Persistable> data;
    private Class<M> model;
    private List<Result<M>> results = Lists.newArrayList();
    private ModelLoaderCallbacks<M> listener;

    private QueryAttribute<M, Long> attribute;

    public ModelLoader(AppCompatActivity activity, Class<M> modelType, ModelLoaderCallbacks<M> listener) {
        this.activity = activity;
        this.model = modelType;
        this.listener = listener;

        data = ((App) activity.getApplication()).getData();
    }

    private void query(int loaderId, Bundle args) {
        LoaderManager loaderManager = activity.getLoaderManager();
        Loader l = loaderManager.getLoader(loaderId);
        if (l == null)
            loaderManager.initLoader(loaderId, args, this);
        else
            loaderManager.restartLoader(loaderId, args, this);
    }

    public void queryById(QueryAttribute<M, Long> attr, long id) {
        attribute = attr;
        Bundle args = new Bundle();
        args.putLong(null, id);

        query(QUERY_BY_ID, args);
    }

    public void queryAll() {
        query(QUERY_ALL, null);
    }

    @Override
    public Loader<Result<M>> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Result<M>>(activity) {
            private Result<M> result;

            @Override
            protected void onStartLoading() {
                if (result == null)
                    forceLoad();
                else
                    deliverResult(result);
            }

            @Override
            public Result<M> loadInBackground() {
                if (id == QUERY_BY_ID)
                    result = data.select(model).where(attribute.eq(args.getLong(null))).get();
                else
                    result = data.select(model).get();
                return result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Result<M>> loader, Result<M> data) {
        listener.onModelLoadFinished(data);
        results.add(data);
    }

    @Override
    public void onLoaderReset(Loader<Result<M>> loader) {
        for (Result<M> r : results)
            r.close();
    }
}
