package com.pocketpalsson.volleyball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.fragment.LeagueStandingFragment;
import com.pocketpalsson.volleyball.fragment.MatchListFragment;
import com.pocketpalsson.volleyball.presenters.MainActivityPresenter;
import com.pocketpalsson.volleyball.views.MainActivityView;
import com.pocketpalsson.volleyball.views.controllers.MatchListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends MvpActivity<MainActivityView, MainActivityPresenter> implements MainActivityView, NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;

    private MatchListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.match_list);

        adapter = new MatchListAdapter(this);
        adapter.setMatchClickListener(match -> {
            if (getPresenter() != null) {
                openMatch(match.federationMatchNumber);
            }
        });

        if (savedInstanceState == null) {
            addFragment(new MatchListFragment(), "match_list", true, false);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentCount() == 1) {
                finish();
            }
            super.onBackPressed();
            invalidateOptionsMenu();
        }
    }

    private int getFragmentCount() {
        FragmentManager fm = getSupportFragmentManager();
        return fm.getBackStackEntryCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.nav_drawer_menu, menu);
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
    public MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    public void openMatch(int federationMatchNumber) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MatchActivity.FEDERATION_MATCH_NUMBER, federationMatchNumber);
        startActivity(intent);
        closeNavDrawer();
    }

    private void addFragment(Fragment frag, String fragmentId, boolean allowStateLoss, boolean shouldAddToBackstack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.fade_in, R.anim.pause_then_fade_out);
        ft.replace(R.id.fragment_container, frag, fragmentId);
        if (shouldAddToBackstack) {
            ft.addToBackStack(null);
        }
        if (allowStateLoss) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.match_list:
                addFragment(new MatchListFragment(), "match_list", false, true);
                break;
            case R.id.standings:
                addFragment(new LeagueStandingFragment(), "league_standing", false, true);
                break;
            case R.id.asv_aarhus:
                openTeam(1);
                break;
        }
        closeNavDrawer();
        return true;
    }

    private void openTeam(int id) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        intent.putExtra(TeamDetailActivity.TEAM_ID, id);
        startActivity(intent);
    }
}
