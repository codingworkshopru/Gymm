package ru.codingworkshop.gymm.program;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.codingworkshop.gymm.data.model.Model;

/**
 * Created by Радик on 28.02.2017.
 */
public abstract class ProgramViewHolder<T extends Model> extends RecyclerView.ViewHolder {
    private View mReorderActionView;

    public ProgramViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setModel(T model);

    public View getReorderActionView() {
        return mReorderActionView;
    }

    public void setReorderActionView(View reorderActionView) {
        mReorderActionView = reorderActionView;
    }
}
