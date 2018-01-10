package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.databinding.FragmentStatisticsJournalExerciseDetailsListFooterBinding;
import ru.codingworkshop.gymm.databinding.FragmentStatisticsJournalExerciseDetailsListItemBinding;

public class StatisticsJournalExerciseDetailsFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private StatisticsJournalViewModel viewModel;

    static final String TAG = StatisticsJournalExerciseDetailsFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(StatisticsJournalViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_journal_exercise_details, container, false);
        RecyclerView rv = view.findViewById(R.id.statisticsExerciseDetailsJournalList);
        BaseAdapter adapter = new BaseAdapter(viewModel.getCurrentExerciseNode().getChildren());
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);
        return view;
    }

    private static class BaseViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        BaseViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.getBinding(itemView);
        }
    }


    private static class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {
        private static final int HEADER_ITEM_TYPE = R.layout.fragment_statistics_journal_exercise_details_list_header;
        private static final int BODY_ITEM_TYPE = R.layout.fragment_statistics_journal_exercise_details_list_item;
        private static final int FOOTER_ITEM_TYPE = R.layout.fragment_statistics_journal_exercise_details_list_footer;

        private List<ActualSet> actualSets;
        private double totalWeight;
        private int totalReps;

        public BaseAdapter(List<ActualSet> actualSets) {
            this.actualSets = actualSets;
            for (ActualSet set : actualSets) {
                totalReps += set.getReps();
                totalWeight += Optional.fromNullable(set.getWeight()).or(0.0);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_ITEM_TYPE;
            } else if (position == getItemCount() - 1) {
                return FOOTER_ITEM_TYPE;
            } else {
                return BODY_ITEM_TYPE;
            }
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case HEADER_ITEM_TYPE:
                    return new BaseViewHolder(inflater.inflate(viewType, parent, false));

                case BODY_ITEM_TYPE:
                    FragmentStatisticsJournalExerciseDetailsListItemBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
                    return new BaseViewHolder(binding.getRoot());

                case FOOTER_ITEM_TYPE:
                    FragmentStatisticsJournalExerciseDetailsListFooterBinding binding1 = DataBindingUtil.inflate(inflater, viewType, parent, false);
                    return new BaseViewHolder(binding1.getRoot());

                default:
                    throw new IllegalStateException();

            }
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == BODY_ITEM_TYPE) {
                FragmentStatisticsJournalExerciseDetailsListItemBinding binding = (FragmentStatisticsJournalExerciseDetailsListItemBinding) holder.binding;
                binding.setIndex(position);
                binding.setSet(actualSets.get(position - 1));
                binding.executePendingBindings();
            } else if (itemViewType == FOOTER_ITEM_TYPE) {
                FragmentStatisticsJournalExerciseDetailsListFooterBinding binding1 = (FragmentStatisticsJournalExerciseDetailsListFooterBinding) holder.binding;
                binding1.setTotalReps(totalReps);
                binding1.setTotalWeight(totalWeight);
                binding1.executePendingBindings();
            }
        }

        @Override
        public int getItemCount() {
            return actualSets.size() + 2;
        }
    }
}
