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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.MatchStatFragment;
import com.sportsapp.volleyliga.fragment.PlayerStatsFragment;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.presenters.MatchPresenter;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchLoadingResultEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.sportsapp.volleyliga.views.MatchView;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private Subscription updateSubscription;

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
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        loadData();
        viewPager.setCurrentItem(1, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (match != null && match.getType() == MatchModel.Type.LIVE) {
            updateSubscription = Observable.interval(20, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(timestamp -> refresh());
        }
    }

    @Override
    protected void onPause() {
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            updateSubscription.unsubscribe();
            updateSubscription = null;
        }
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_list_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setIsLoading(boolean value) {
        pagerAdapter.setIsLoading(value);
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


    public void loadData() {
        MatchRepository.instance.getMatch(federationMatchNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MatchModel>() {
                    @Override
                    public void onCompleted() {
                        String s = "";
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(MatchActivity.this, "Error occured while fetching match", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(MatchModel matchModelReply) {
                        setData(matchModelReply);
                    }
                });

//        VolleyQueue.instance.add(new GetMatchVolleyRequest(VolleyQueue.instance, "http://caspermunk.dk/livescore/xml/" + federationMatchNumber + ".xml", UUID.randomUUID().toString(), true,
//                (value, isLastResponse) -> setData(value)));
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        loadData();
        setIsLoading(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void loadingTriggered(TriggerMatchLoadingEvent event) {
        loadData();
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
                result = PlayerStatsFragment.newInstance(true);
            } else if (position == 1) {
                result = new MatchStatFragment();
            } else if (position == 2) {
                result = PlayerStatsFragment.newInstance(false);
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

        public void setIsLoading(boolean value) {
            PlayerStatsFragment guestStatFragment = getGuestStatFragment();
            if (guestStatFragment != null) {
                guestStatFragment.setIsLoading(value);
            }
            PlayerStatsFragment getHomeStatFragment = getHomeStatFragment();
            if (getHomeStatFragment != null) {
                getHomeStatFragment.setIsLoading(value);
            }
            MatchStatFragment matchStatFragment = getMatchStatFragment();
            if (matchStatFragment != null) {
                matchStatFragment.setIsLoading(value);
            }
        }
    }
}
