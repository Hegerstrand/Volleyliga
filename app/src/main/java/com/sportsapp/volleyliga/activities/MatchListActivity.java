package com.sportsapp.volleyliga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.MatchListFragment;
import com.sportsapp.volleyliga.fragment.MatchListFragmentBuilder;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.presenters.MatchListPresenter;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchListResultsReceivedEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchListLoadingEvent;
import com.sportsapp.volleyliga.views.MatchListActivityView;
import com.sportsapp.volleyliga.views.controllers.MatchListAdapter;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MatchListActivity extends MvpActivity<MatchListActivityView, MatchListPresenter> implements MatchListActivityView, NavigationView.OnNavigationItemSelectedListener {

    public final CustomBus bus = new CustomBus();

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.viewPager)
    public ViewPager viewPager;
    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    @Bind(R.id.nav_view)
    public NavigationView navigationView;
    @Bind(R.id.spinner)
    public Spinner spinner;


    private MatchListAdapter adapter;
    private List<MatchModel> past, live, future, allMatches;
    private MatchListPagerAdapter pagerAdapter;
    private MatchModel.League selectedLeague = MatchModel.League.MALE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLeague(position == 0 ? MatchModel.League.MALE : MatchModel.League.FEMALE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bus.register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Matches");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.inflateMenu(R.menu.match_list_menu);
        invalidateOptionsMenu();

        pagerAdapter = new MatchListPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1, false);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.match_list);
        refresh();
    }


    @Produce
    public MatchListResultsReceivedEvent produceMatchListEvent() {
        return new MatchListResultsReceivedEvent(past, live, future);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_list_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void closeNavDrawer() {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public MatchListPresenter createPresenter() {
        return new MatchListPresenter();
    }

    public void openMatch(int federationMatchNumber) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MatchActivity.FEDERATION_MATCH_NUMBER, federationMatchNumber);
        startActivity(intent);
        closeNavDrawer();
    }

    @Override
    public void setData(List<MatchModel> data) {
        allMatches = data;
        setLeague(selectedLeague);
    }

    public void setLeague(MatchModel.League league) {
        selectedLeague = league;
        past = new ArrayList<>();
        live = new ArrayList<>();
        future = new ArrayList<>();
        for (MatchModel match : allMatches) {
            if (match.league != selectedLeague) {
                continue;
            }
            MatchModel.Type type = match.getType();
            switch (type) {
                case PAST:
                    past.add(match);
                    break;
                case LIVE:
                    live.add(match);
                    break;
                case FUTURE:
                    future.add(match);
                    break;
            }
        }
        Collections.reverse(past);
        bus.post(new MatchListResultsReceivedEvent(past, live, future));
    }

    @Override
    public CustomBus getBus() {
        return bus;
    }

    @Override
    public void loadFinished() {
        pagerAdapter.setIsLoading(false);
    }

    @Subscribe
    public void loadingTriggered(TriggerMatchListLoadingEvent event) {
        refresh();
    }

    private void refresh() {
        loadData();
        pagerAdapter.setIsLoading(true);
    }

    public void loadData() {
        getPresenter().loadMatches();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.match_list:
                break;
//            case R.id.standings:
//                addFragment(new LeagueStandingFragment(), "league_standing", false, true);
//                break;
            case R.id.asv_aarhus:
                openTeam(1);
                break;
            case R.id.marienlyst:
                openTeam(2);
                break;
            case R.id.gentofte:
                openTeam(3);
                break;
            case R.id.hvidovre:
                openTeam(4);
                break;
            case R.id.ishoj:
                openTeam(5);
                break;
            case R.id.lyngby_gladsaxe:
                openTeam(6);
                break;
            case R.id.middelfart:
                openTeam(7);
                break;
            case R.id.randers:
                openTeam(8);
                break;
            case R.id.amager:
                openTeam(9);
                break;
            case R.id.brondby:
                openTeam(10);
                break;
            case R.id.odense:
                openTeam(11);
                break;
            case R.id.eliteVolleyAarhus:
                openTeam(12);
                break;
            case R.id.fortuna:
                openTeam(13);
                break;
            case R.id.holte:
                openTeam(14);
                break;
            case R.id.lyngbyVolleyFemale:
                openTeam(15);
                break;
            case R.id.koge:
                openTeam(16);
                break;
        }
        navigationView.setCheckedItem(R.id.match_list);
        closeNavDrawer();
        return true;
    }

    private void openTeam(int id) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        intent.putExtra(TeamDetailActivity.TEAM_ID, id);
        startActivity(intent);
    }

    public class MatchListPagerAdapter extends FragmentPagerAdapter {

        private HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<>();

        public MatchListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.tab_today_name);
            } else if (position == 1) {
                return getString(R.string.tab_archive_name);
            } else if (position == 2) {
                return getString(R.string.tab_future_name);
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
                result = new MatchListFragmentBuilder(MatchModel.Type.LIVE).build();
            } else if (position == 1) {
                result = new MatchListFragmentBuilder(MatchModel.Type.PAST).build();
            } else if (position == 2) {
                result = new MatchListFragmentBuilder(MatchModel.Type.FUTURE).build();
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

        public void setIsLoading(boolean value) {
            if(mPageReferenceMap.containsKey(0)){
                MatchListFragment fragment = (MatchListFragment) mPageReferenceMap.get(0);
                fragment.setIsLoading(value);
            }
            if(mPageReferenceMap.containsKey(1)){
                MatchListFragment fragment = (MatchListFragment) mPageReferenceMap.get(1);
                fragment.setIsLoading(value);
            }
            if(mPageReferenceMap.containsKey(2)){
                MatchListFragment fragment = (MatchListFragment) mPageReferenceMap.get(2);
                fragment.setIsLoading(value);
            }
        }
    }
}
