package com.sportsapp.volleyliga.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.MatchListView;
import com.sportsapp.volleyliga.models.League;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.presenters.MatchListPresenter;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.Preferences;
import com.sportsapp.volleyliga.utilities.busEvents.DestructionEvent;
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


public class MatchListFragment extends Fragment implements DrawerFragment, MatchListActivityView {

    private MainActivityListener activityListener;
    public final CustomBus bus = new CustomBus();

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;

    @Bind(R.id.viewPager)
    public ViewPager viewPager;
    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    @Bind(R.id.spinner)
    public Spinner spinner;

    private MatchListPresenter presenter = new MatchListPresenter();
    private MatchListAdapter adapter;
    private List<MatchModel> past = new ArrayList<>(), today = new ArrayList<>(), future = new ArrayList<>(), allMatches = new ArrayList<>();
    private MatchListPagerAdapter pagerAdapter;
    private League selectedLeague = League.MALE;
    private ActionBarDrawerToggle toggle;
    private HashMap<Integer, MatchModel> allMatchesDict = new HashMap<>();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityListener) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender_type, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                League league = position == 0 ? League.MALE : League.FEMALE;
                Preferences.with(getActivity()).saveLeague(league);
                setLeague(league);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectedLeague = Preferences.with(getActivity()).getLeague();

        spinner.setSelection(League.getIndex(selectedLeague));
        bus.register(this);
        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.inflateMenu(R.menu.match_list_menu);

        pagerAdapter = new MatchListPagerAdapter(getActivity());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1, false);

        presenter.attachView(this);
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityListener != null && activityListener.isFragmentShown(this)) {
            activityListener.setupContainerUI(toolbar, navigationView, R.id.match_list);
            ActionBar actionBar = activityListener.getSupportActionbar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        bus.unregister(this);
        bus.post(new DestructionEvent());
        super.onDestroyView();
    }

    @Produce
    public MatchListResultsReceivedEvent produceMatchListEvent() {
        return new MatchListResultsReceivedEvent(past, today, future);
    }

    @Override
    public void closeNavDrawer() {
        if (drawer != null) {
            drawer.closeDrawers();
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return drawer != null && drawer.isDrawerOpen(GravityCompat.START);
    }

    public boolean isShown() {
        if (activityListener != null) {
            return activityListener.isFragmentShown(this);
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        if (isShown()) {
            getActivity().getMenuInflater().inflate(R.menu.match_list_menu, menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isShown()) {
            if (toggle.onOptionsItemSelected(item)) {
                return true;
            }
            switch (item.getItemId()) {
                case R.id.refresh:
                    refresh();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void openMatch(int federationMatchNumber) {
        if (activityListener != null) {
            activityListener.openMatch(federationMatchNumber);
        }
        closeNavDrawer();
    }

    @Override
    public void addMatches(List<MatchModel> data) {
        for (MatchModel match : data) {
            allMatchesDict.put(match.federationMatchNumber, match);
        }
        allMatches = new ArrayList<>(allMatchesDict.values());
        Collections.sort(allMatches, new MatchModel.MatchComparator());
        setLeague(selectedLeague);
    }

    public void setLeague(League league) {
        selectedLeague = league;
        past = new ArrayList<>();
        today = new ArrayList<>();
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
                case TODAY:
                    today.add(match);
                    break;
                case FUTURE:
                    future.add(match);
                    break;
            }
        }
        Collections.reverse(past);
        bus.post(new MatchListResultsReceivedEvent(past, today, future));
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
        presenter.loadMatches();
        pagerAdapter.setIsLoading(true);
    }

    private void openTeam(int id) {
        if (activityListener != null) {
            activityListener.openTeam(id);
        }
    }


    public class MatchListPagerAdapter extends PagerAdapter {

        private final MatchListView todayMatchView, pastMatchView, futureMatchView;

        public MatchListPagerAdapter(Context context) {
            super();
            todayMatchView = new MatchListView(context, bus, MatchModel.Type.TODAY);
            pastMatchView = new MatchListView(context, bus, MatchModel.Type.PAST);
            futureMatchView = new MatchListView(context, bus, MatchModel.Type.FUTURE);
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
        public Object instantiateItem(ViewGroup collection, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = todayMatchView;
                    break;
                case 1:
                    view = pastMatchView;
                    break;
                case 2:
                    view = futureMatchView;
                    break;
                default:
                    return null;
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            collection.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        public void setIsLoading(boolean value) {
            todayMatchView.setIsLoading(value);
            pastMatchView.setIsLoading(value);
            futureMatchView.setIsLoading(value);
        }
    }
}
