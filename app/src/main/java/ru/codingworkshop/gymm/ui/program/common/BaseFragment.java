package ru.codingworkshop.gymm.ui.program.common;

import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Радик on 17.10.2017 as part of the Gymm project.
 */

public abstract class BaseFragment extends Fragment {
    private ObservableBoolean inActionMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inActionMode = new ObservableBoolean();
        View containerView = createBinding(inflater, container).getRoot();

        final Toolbar toolbar = getToolbar();
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        return containerView;
    }

    public ObservableBoolean getInActionMode() {
        return inActionMode;
    }

    abstract protected ViewDataBinding createBinding(LayoutInflater inflater, ViewGroup parent);
    abstract protected Toolbar getToolbar();
}
