package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.activities.DrawerFragment;
import com.sportsapp.volleyliga.activities.MainActivityListener;
import com.sportsapp.volleyliga.models.League;
import com.sportsapp.volleyliga.models.LeagueStandingModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.presenters.LeagueStandingPresenter;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.BusProvider;
import com.sportsapp.volleyliga.utilities.Preferences;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.sportsapp.volleyliga.views.ILeagueStandingView;
import com.sportsapp.volleyliga.views.controllers.DividerItemDecoration;
import com.sportsapp.volleyliga.views.controllers.LeagueStandingAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LeagueStandingFragment extends Fragment implements DrawerFragment, ILeagueStandingView, OnRefreshListener {

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;
    @Bind(R.id.spinner)
    public Spinner spinner;

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;

    private LeagueStandingAdapter adapter;

    private MainActivityListener activityListener;
    private LeagueStandingPresenter presenter = new LeagueStandingPresenter();
    private League selectedLeague = League.MALE;
    private ActionBarDrawerToggle toggle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_league_standing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender_type, R.layout.spinner_selected_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
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

        adapter = new LeagueStandingAdapter(getActivity());
        adapter.setLeagueStandingClickListener(leagueStanding -> {
            TeamModel team = TeamRepository.instance.getTeam(leagueStanding.teamName);
            BusProvider.getInstance().post(new OpenTeamEvent(team.id));
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);

        selectedLeague = Preferences.with(getActivity()).getLeague();
        presenter.attachView(this);
        loadData();
    }

    public void setLeague(League league) {
        selectedLeague = league;
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityListener != null && activityListener.isFragmentShown(this)) {
            activityListener.setupContainerUI(toolbar, navigationView, R.id.standings);
            ActionBar actionBar = activityListener.getSupportActionbar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    public void setData(List<LeagueStandingModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

    public void loadData() {
        setIsLoading(true);
        presenter.getStandings(selectedLeague);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isShown()) {
            if (toggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        return false;
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
    public void onRefresh() {
        loadData();
    }

    private void setIsLoading(boolean value) {
        refreshLayout.setRefreshing(value);
    }

}
