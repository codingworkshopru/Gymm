package ru.codingworkshop.gymm.ui.program;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Радик on 20.04.2017.
 */

public class BindingHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public final B binding;

    public BindingHolder(B itemBinding) {
        super(itemBinding.getRoot());
        this.binding = itemBinding;
    }

}
