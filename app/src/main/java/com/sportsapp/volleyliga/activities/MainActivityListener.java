package com.volleyapp.volleyliga.activities;

import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

public interface MainActivityListener {
    void setupContainerUI(Toolbar toolbar, NavigationView navigationView, @IdRes int selectedMenuItemId);

    void setupContainerUI(Toolbar toolbar, @IdRes int selectedMenuItemId);

    ActionBar getSupportActionbar();

    void setActionbarTitle(String title);

    boolean isFragmentShown(Fragment fragment);

    void openMatch(int federationMatchNumber);

    void openTeam(int id);
}
