package ru.codingworkshop.gymm.data.wrapper;

import android.support.v7.util.DiffUtil;

import java.util.List;

import ru.codingworkshop.gymm.data.model.common.Model;

/**
 * Created by Радик on 26.06.2017.
 */
final class DiffIdCallback extends DiffUtil.Callback {
    private List<? extends Model> oldCollection;
    private List<? extends Model> newCollection;

    public DiffIdCallback(List<? extends Model> oldCollection, List<? extends Model> newCollection) {
        this.oldCollection = oldCollection;
        this.newCollection = newCollection;
    }

    @Override
    public int getOldListSize() {
        return oldCollection.size();
    }

    @Override
    public int getNewListSize() {
        return newCollection.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCollection.get(oldItemPosition).getId() == newCollection.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
