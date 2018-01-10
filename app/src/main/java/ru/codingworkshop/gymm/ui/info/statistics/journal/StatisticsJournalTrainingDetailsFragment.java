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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.databinding.FragmentStatisticsTrainingDetailsJournalBinding;
import ru.codingworkshop.gymm.databinding.FragmentStatisticsTrainingDetailsJournalListItemBinding;
import ru.codingworkshop.gymm.ui.common.BindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

public class StatisticsJournalTrainingDetailsFragment extends Fragment {
    @Inject ViewModelProvider.Factory viewModelFactory;
    private FragmentStatisticsTrainingDetailsJournalBinding binding;
    private BindingListAdapter<ImmutableActualExerciseNode, FragmentStatisticsTrainingDetailsJournalListItemBinding> listAdapter;

    static final String TAG = StatisticsJournalTrainingDetailsFragment.class.getName();
    private StatisticsJournalViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
                .get(StatisticsJournalViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics_training_details_journal, container, false);

        ImmutableActualTrainingTree tree = viewModel.getActualTrainingTree();
        binding.setTraining(tree.getParent());

        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_statistics_training_details_journal_list_item)
                .setOnClickListener(this::onListItemClick);
        listAdapter = new ClickableBindingListAdapter<ImmutableActualExerciseNode, FragmentStatisticsTrainingDetailsJournalListItemBinding>(tree.getChildren(), listeners) {
            @Override
            protected void bind(FragmentStatisticsTrainingDetailsJournalListItemBinding binding, ImmutableActualExerciseNode item) {
                binding.setExercise(item);
            }
        };
        binding.statisticsTrainingDetailsJournalExerciseList.setAdapter(listAdapter);
        return binding.getRoot();
    }

    private void onListItemClick(View view) {
        FragmentStatisticsTrainingDetailsJournalListItemBinding binding = DataBindingUtil.getBinding(view);
        viewModel.setCurrentExerciseNode(binding.getExercise());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(StatisticsJournalExerciseDetailsFragment.TAG) == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.statisticsJournalContainer, new StatisticsJournalExerciseDetailsFragment(), StatisticsJournalExerciseDetailsFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
