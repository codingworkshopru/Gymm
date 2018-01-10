package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;

public class StatisticsJournalActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_journal);

        Toolbar toolbar = findViewById(R.id.statisticsJournalToolbar);
        toolbar.setNavigationOnClickListener(unused -> onBackPressed());

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(StatisticsJournalTrainingsFragment.TAG) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.statisticsJournalContainer, new StatisticsJournalTrainingsFragment(), StatisticsJournalTrainingsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
