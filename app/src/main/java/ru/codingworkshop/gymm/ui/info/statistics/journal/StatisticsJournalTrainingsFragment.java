package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.databinding.FragmentStatisticsTrainingsJournalListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

public class StatisticsJournalTrainingsFragment extends Fragment {

    @Inject ViewModelProvider.Factory viewModelFactory;
    private RecyclerView trainingsView;

    static final String TAG = StatisticsJournalTrainingsFragment.class.getName();
    private StatisticsJournalViewModel viewModel;
    private ClickableBindingListAdapter<ActualTraining, FragmentStatisticsTrainingsJournalListItemBinding> adapter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_trainings_journal, container, false);
        trainingsView = view.findViewById(R.id.statisticsJournalTrainings);
        ListItemListeners listItemListeners = new ListItemListeners(R.layout.fragment_statistics_trainings_journal_list_item)
                .setOnClickListener(this::onListItemClick);
        adapter = new ClickableBindingListAdapter<ActualTraining, FragmentStatisticsTrainingsJournalListItemBinding>(null, listItemListeners) {
            @Override
            protected void bind(FragmentStatisticsTrainingsJournalListItemBinding binding, ActualTraining item) {
                binding.setActualTraining(item);
            }
        };
        trainingsView.setAdapter(adapter);
        viewModel.getActualTrainings().observe(this, adapter::setItems);
        return view;
    }

    private void onListItemClick(View view) {
        FragmentStatisticsTrainingsJournalListItemBinding binding = DataBindingUtil.getBinding(view);
        ActualTraining actualTraining = binding.getActualTraining();
        LiveDataUtil.getOnce(viewModel.loadTree(actualTraining.getId()), unused -> startDetailsFragment());
    }

    private void startDetailsFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(StatisticsJournalTrainingDetailsFragment.TAG) == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.statisticsJournalContainer, new StatisticsJournalTrainingDetailsFragment(), StatisticsJournalTrainingDetailsFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
