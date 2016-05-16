package com.sportsapp.volleyliga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.LeagueStandingFragment;
import com.sportsapp.volleyliga.fragment.SettingsFragment;
import com.sportsapp.volleyliga.utilities.BusProvider;
import com.sportsapp.volleyliga.utilities.ToolbarColorizer;
import com.sportsapp.volleyliga.utilities.busEvents.OpenMatchEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityListener, FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String FEDERATION_MATCH_NUMBER = "federationMatchNumber";

    public static DateTime referenceTime = new DateTime(2016, 1, 9, 0, 0);
    private int activeMenuItemId = -1;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int federationMatchNumber = intent.getIntExtra(FEDERATION_MATCH_NUMBER, 0);
        if (!isFinishing() && federationMatchNumber > 0) {
            openMatch(federationMatchNumber);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BusProvider.getInstance().register(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int federationMatchNumber = getIntent().getIntExtra(FEDERATION_MATCH_NUMBER, 0);
        if (federationMatchNumber > 0) {
            openMatch(federationMatchNumber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFragmentCount() == 0) {
            addFragment(new MatchListFragment(), "match_list", true);
        }

    }

    @Override
    public void setupContainerUI(Toolbar toolbar, @IdRes int activeMenuItemId) {
        setupContainerUI(toolbar, null, activeMenuItemId);
    }

    @Override
    public void setupContainerUI(Toolbar toolbar, NavigationView navigationView, @IdRes int activeMenuItemId) {
        super.setSupportActionBar(toolbar);
        if (toolbar != null) {
            int color = ContextCompat.getColor(this, R.color.toolbarAccentColor);
            ToolbarColorizer.colorizeToolbar(toolbar, color, this);
        }
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
        }
        if (navigationView != null && navigationView.getMenu().findItem(activeMenuItemId) != null) {
            navigationView.setCheckedItem(activeMenuItemId);
            this.activeMenuItemId = activeMenuItemId;
        } else {
            this.activeMenuItemId = -1;
        }
    }

    @Override
    public ActionBar getSupportActionbar() {
        return super.getSupportActionBar();
    }

    @Override
    public void setActionbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean isFragmentShown(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        return fm != null && fm.findFragmentById(R.id.fragment_container) == fragment;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getActiveFragment();
        if (fragment instanceof DrawerFragment && ((DrawerFragment) fragment).isDrawerOpen()) {
            ((DrawerFragment) fragment).closeNavDrawer();
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

    private Fragment getActiveFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void addFragment(Fragment frag, String fragmentId) {
        addFragment(frag, fragmentId, true);
    }

    private void addFragment(Fragment frag, String fragmentId, boolean shouldAddToBackstack) {
        FragmentManager fm = getSupportFragmentManager();

        Fragment existingFragment = fm.findFragmentByTag(fragmentId);
        if (existingFragment != null) {
            frag = existingFragment;
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(existingFragment).commit();
            fm.executePendingTransactions();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, frag, fragmentId);
        if (shouldAddToBackstack) {
            ft.addToBackStack(null);
        }
        try {
            ft.commit();
        } catch (IllegalStateException e) {
            //This is most likely due to addFragment being called via an onClick method after onPause has been called.
            //The framework executes onClick and FragmentTransactions asynchronously which is weird
            //https://www.reddit.com/r/androiddev/comments/4d2aje/_/
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onBackStackChanged() {
        Fragment activeFragment = getActiveFragment();
        if (activeFragment != null) {
            activeFragment.onResume();
        }
    }

    public void openMatchList() {
        addFragment(new MatchListFragment(), "match_list", true);
    }

    private void openLeagueStanding() {
        addFragment(new LeagueStandingFragment(), "league_standing", true);
    }

    private void openSettings() {
        addFragment(new SettingsFragment(), "settings", true);
    }

    @Subscribe
    public void openMatch(OpenMatchEvent event) {
        openMatch(event.federationMatchNumber);
    }

    @Override
    public void openMatch(int federationMatchNumber) {
        MatchFragment fragment = MatchFragment.newInstance(federationMatchNumber);
        addFragment(fragment, "match_" + federationMatchNumber, true);
    }

    @Subscribe
    public void openTeam(OpenTeamEvent event) {
        openTeam(event.id);
    }

    @Override
    public void openTeam(int id) {
        TeamFragment fragment = TeamFragment.newInstance(id);
        addFragment(fragment, "team_" + id, true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        closeActiveFragmentDrawer();
        if (item.getItemId() == activeMenuItemId) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.match_list:
                openMatchList();
                break;
            case R.id.standings:
                openLeagueStanding();
                break;
            case R.id.settings:
                openSettings();
                break;
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
        return false;
    }

    private void closeActiveFragmentDrawer() {
        Fragment activeFragment = getActiveFragment();
        if (activeFragment instanceof DrawerFragment) {
            ((DrawerFragment) activeFragment).closeNavDrawer();
        }
    }
}
