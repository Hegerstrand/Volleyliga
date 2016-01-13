package com.sportsapp.volleyliga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.MatchStatFragment;
import com.sportsapp.volleyliga.fragment.MatchStatFragmentBuilder;
import com.sportsapp.volleyliga.fragment.PlayerStatsFragment;
import com.sportsapp.volleyliga.fragment.PlayerStatsFragmentBuilder;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.presenters.MatchPresenter;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchLoadingResultEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.sportsapp.volleyliga.utilities.volley.VolleyQueue;
import com.sportsapp.volleyliga.utilities.volley.match.GetMatchVolleyRequest;
import com.sportsapp.volleyliga.views.MatchView;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchActivity extends MvpActivity<MatchView, MatchPresenter> implements MatchView, SwipeRefreshLayout.OnRefreshListener {

    public static final String FEDERATION_MATCH_NUMBER = "federationMatchNumber";
    public final CustomBus bus = new CustomBus();
    //    @Bind(R.id.stats_view)
//    public MatchStatsView statsView;
    //    @Bind(R.id.tvTeamHome)
//    public TextView tvTeamHome;
//    @Bind(R.id.tvTeamGuest)
//    public TextView tvTeamGuest;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.viewPager)
    public ViewPager viewPager;
    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    public int federationMatchNumber;

    private MatchModel match;
    private StatPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        bus.register(this);
        federationMatchNumber = getIntent().getExtras().getInt(FEDERATION_MATCH_NUMBER);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        pagerAdapter = new StatPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        loadData(false);
        viewPager.setCurrentItem(1, false);
    }

    private void setIsLoading(boolean value) {

    }

    public void setData(MatchModel data) {
        setMatchModel(data);
        setIsLoading(false);
//        showContent();
    }

    public void setMatchModel(MatchModel match) {
        getSupportActionBar().setTitle(match.getTitle());
        tabLayout.getTabAt(0).setText(match.teamHome.shortName);
        tabLayout.getTabAt(2).setText(match.teamGuest.shortName);
        pagerAdapter.notifyDataSetChanged();
        bus.post(new MatchLoadingResultEvent(match));
    }

    @Produce
    public MatchLoadingResultEvent produceMatchEvent() {
        return new MatchLoadingResultEvent(match);
    }

    @Override
    public CustomBus getBus() {
        return bus;
    }

    @NonNull
    @Override
    public MatchPresenter createPresenter() {
        return new MatchPresenter();
    }


    public void loadData(boolean pullToRefresh) {
        VolleyQueue.instance.add(new GetMatchVolleyRequest(VolleyQueue.instance, "http://caspermunk.dk/livescore/xml/" + federationMatchNumber + ".xml", UUID.randomUUID().toString(), true,
                (value, isLastResponse) -> setData(value)));
    }

    @Override
    public void onRefresh() {
        loadData(true);
        setIsLoading(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void loadingTriggered(TriggerMatchLoadingEvent event) {
        loadData(true);
    }


    @Subscribe
    public void openTeam(OpenTeamEvent event) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        intent.putExtra(TeamDetailActivity.TEAM_ID, event.id);
        startActivity(intent);
    }


    public class StatPagerAdapter extends FragmentPagerAdapter {
        private HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<>();

        public StatPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return "Match stats";
            }
            if (match == null) {
                return "";
            }
            if (position == 0) {
                return match.teamHome.getName();
            } else if (position == 2) {
                return match.teamGuest.getName();
            }
            return "";
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment result = null;
            if (position == 0) {
                result = new PlayerStatsFragmentBuilder(true).build();
            } else if (position == 1) {
                result = new MatchStatFragmentBuilder().build();
            } else if (position == 2) {
                result = new PlayerStatsFragmentBuilder(false).build();
            }
            if (result != null) {
                mPageReferenceMap.put(position, result);
            }
            return result;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public PlayerStatsFragment getHomeStatFragment() {
            if (mPageReferenceMap.containsKey(0)) {
                return (PlayerStatsFragment) mPageReferenceMap.get(0);
            }
            return null;
        }

        public PlayerStatsFragment getGuestStatFragment() {
            if (mPageReferenceMap.containsKey(2)) {
                return (PlayerStatsFragment) mPageReferenceMap.get(2);
            }
            return null;
        }

        public MatchStatFragment getMatchStatFragment() {
            if (mPageReferenceMap.containsKey(1)) {
                return (MatchStatFragment) mPageReferenceMap.get(1);
            }
            return null;
        }
    }
}
