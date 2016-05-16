package com.sportsapp.volleyliga.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.TeamInfoView;
import com.sportsapp.volleyliga.fragment.TeamMatchesView;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.presenters.TeamDetailPresenter;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.Preferences;
import com.sportsapp.volleyliga.views.TeamDetailView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TeamFragment extends Fragment implements DrawerFragment, TeamDetailView {

    public static final String TEAM_ID = "teamId";

    @Bind(R.id.coordinatorLayout)
    public CoordinatorLayout coordinatorLayout;
    
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
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.ivTeamPhoto)
    public ImageView ivTeamPhoto;
    @Bind(R.id.teamPhotoContainer)
    public FrameLayout teamPhotoContainer;
    @Bind(R.id.btnFavorite)
    public LikeButton btnFavorite;


    private TeamDetailPagerAdapter pagerAdapter;
    private String PHOTO_BASE_URL = "http://www.pocketpalsson.com/volleyball/";

    private TeamDetailPresenter presenter;
    private MainActivityListener activityListener;
    private ActionBarDrawerToggle toggle;
    private int teamId;
    private boolean isFavorite;
    private TeamModel team;
    private Snackbar snackbar;


    public static TeamFragment newInstance(int teamId) {
        TeamFragment myFragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putInt(TEAM_ID, teamId);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityListener) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        teamId = getArguments().getInt(TEAM_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        collapsingToolbar.setTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        pagerAdapter = new TeamDetailPagerAdapter();
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        isFavorite = Preferences.with(getActivity()).isFavoriteTeam(teamId);
        if(isFavorite){
            btnFavorite.setLiked(true);
        }
        btnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                favoriteClicked();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                favoriteClicked();
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        presenter = new TeamDetailPresenter();
        presenter.attachView(this);
        presenter.setTeamId(teamId);
    }

    public void favoriteClicked() {
        isFavorite = !isFavorite;
        btnFavorite.setLiked(isFavorite);
        TeamRepository.instance.setIsFavoriteTeam(getActivity(), teamId, isFavorite);
        if(isFavorite) {
            snackbar = Snackbar.make(coordinatorLayout, "Favorited! You will now receive notifications prior to this teams games.", Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            ViewGroup group = (ViewGroup) snackbar.getView();
            TextView tv = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.snackbar_background_color));
            snackbar.show();
        } else {
            if(snackbar != null && snackbar.isShownOrQueued()){
                snackbar.dismiss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityListener != null && activityListener.isFragmentShown(this)) {
            activityListener.setupContainerUI(toolbar, navigationView, TeamRepository.instance.getTeamMenuItemId(teamId));
            if (team != null) {
                activityListener.getSupportActionbar().setTitle(team.name);
            }
        }
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
    public void setTeamModel(TeamModel team) {
        if (activityListener != null) {
            activityListener.getSupportActionbar().setTitle(team.name);
        }
        this.team = team;
        pagerAdapter.infoView.setTeamModel(team);
        pagerAdapter.teamMatchView.setTeamModel(team);

        Ion.with(ivTeamPhoto)
                .load(PHOTO_BASE_URL + team.initials + "/team.jpg")
                .then((e, result) -> {
                    teamPhotoContainer.animate().alpha(1).setDuration(100).start();
                });
    }

    @Override
    public void setData(List<MatchModel> data) {
        pagerAdapter.teamMatchView.setMatches(data);
        pagerAdapter.infoView.setMatches(data);
    }


    public class TeamDetailPagerAdapter extends PagerAdapter {
        public final TeamMatchesView teamMatchView;
        public final TeamInfoView infoView;

        public TeamDetailPagerAdapter() {
            super();
            teamMatchView = new TeamMatchesView(getActivity());
            infoView = new TeamInfoView(getActivity());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Information";
            } else if (position == 1) {
                return "Matches";
            }
            return "";
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = infoView;
                    break;
                case 1:
                    view = teamMatchView;
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
    }

}
