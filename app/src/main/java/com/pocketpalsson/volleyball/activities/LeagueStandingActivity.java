package com.pocketpalsson.volleyball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.LeagueStandingModel;
import com.pocketpalsson.volleyball.presenters.LeagueStandingPresenter;
import com.pocketpalsson.volleyball.views.LeagueStandingView;
import com.pocketpalsson.volleyball.views.MainActivityView;
import com.pocketpalsson.volleyball.views.controllers.LeagueStandingAdapter;
import com.pocketpalsson.volleyball.views.controllers.SpacesItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LeagueStandingActivity extends MvpLceActivity<SwipeRefreshLayout, List<LeagueStandingModel>, LeagueStandingView, LeagueStandingPresenter> implements LeagueStandingView, OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;

    private LeagueStandingAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.standings);


        adapter = new LeagueStandingAdapter(this);
        adapter.setLeagueStandingClickListener(leagueStanding -> {
//            if (getPresenter() != null) {
//                openMatch(match.federationMatchNumber);
//            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.default_card_padding);
        recyclerView.addItemDecoration(new SpacesItemDecoration(this, SpacesItemDecoration.VERTICAL_LIST, spacingInPixels));
        contentView.setOnRefreshListener(this);

        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
    public LeagueStandingPresenter createPresenter() {
        return new LeagueStandingPresenter();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void setData(List<LeagueStandingModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        if (getPresenter() != null) {
            getPresenter().loadStandings();
            setIsLoading(true);
        } else {
            setIsLoading(false);
        }
    }

    @Override
    public void onRefresh() {
        loadData(true);
        setIsLoading(true);
    }

    private void setIsLoading(boolean value) {
        refreshLayout.setRefreshing(value);
    }

    public MainActivityView getActivityView() {
        return (MainActivityView) this;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.match_list:
                intent = new Intent(this, MatchListActivity.class);
                break;
//            case R.id.standings:
//                intent = new Intent(this, MatchListActivity.class);
//                break;
        }
        if(intent != null) {
            startActivity(intent);
            closeNavDrawer();
        }
        return true;
    }
}
