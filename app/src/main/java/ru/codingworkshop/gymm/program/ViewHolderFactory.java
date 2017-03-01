package ru.codingworkshop.gymm.program;

import android.view.ViewGroup;

/**
 * Created by Радик on 28.02.2017.
 */

public interface ViewHolderFactory<VH extends ProgramViewHolder> {
    VH createViewHolder(ViewGroup root, ProgramAdapter adapter);
}
