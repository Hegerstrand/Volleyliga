package com.volleyapp.volleyliga.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.fragment.MatchStatView;
import com.volleyapp.volleyliga.fragment.PlayerStatsView;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.presenters.MatchPresenter;
import com.volleyapp.volleyliga.utilities.CustomBus;
import com.volleyapp.volleyliga.utilities.busEvents.DestructionEvent;
import com.volleyapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.volleyapp.volleyliga.views.MatchView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MatchFragment extends Fragment implements DrawerFragment, MatchView, SwipeRefreshLayout.OnRefreshListener {

    public static final String FEDERATION_MATCH_NUMBER = "federationMatchNumber";
    public final CustomBus bus = new CustomBus();
    //    @Bind(R.id.stats_view)
//    public MatchStatsView statsView;
    //    @Bind(R.id.tvTeamHome)
//    public TextView tvTeamHome;
//    @Bind(R.id.tvTeamGuest)
//    public TextView tvTeamGuest;

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

    private MatchModel match;
    private StatPagerAdapter pagerAdapter;
    private Subscription updateSubscription;
    private BottomSheetBehavior<CardView> bottomSheet;
    private MainActivityListener activityListener;
    private ActionBarDrawerToggle toggle;

    private MatchPresenter presenter;


    public static MatchFragment newInstance(int federationMatchNumber) {
        MatchFragment myFragment = new MatchFragment();
        Bundle args = new Bundle();
        args.putInt(FEDERATION_MATCH_NUMBER, federationMatchNumber);
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
        int federationMatchNumber = getArguments().getInt(FEDERATION_MATCH_NUMBER);
        presenter = new MatchPresenter(federationMatchNumber);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        bus.register(this);

        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        pagerAdapter = new StatPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        presenter.attachView(this);
        refresh();
        viewPager.setCurrentItem(1, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityListener != null && activityListener.isFragmentShown(this)) {
            activityListener.setupContainerUI(toolbar, navigationView, -1);
        }
        if (match != null && match.getType() == MatchModel.Type.TODAY) {
            updateSubscription = Observable.interval(20, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(timestamp -> refresh());
        }
    }

    @Override
    public void onPause() {
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            updateSubscription.unsubscribe();
            updateSubscription = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        bus.unregister(this);
        bus.post(new DestructionEvent());
        super.onDestroyView();
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


    private void setIsLoading(boolean value) {
        pagerAdapter.setIsLoading(value);
    }

    @Override
    public void setMatch(MatchModel match) {
        this.match = match;
        if (activityListener != null) {
            activityListener.getSupportActionbar().setTitle(this.match.getTitle());
        }
        tabLayout.getTabAt(0).setText(this.match.teamHome.shortName);
        tabLayout.getTabAt(2).setText(this.match.teamGuest.shortName);
        pagerAdapter.notifyDataSetChanged();
        pagerAdapter.homeStatsView.setMatch(match);
        pagerAdapter.guestStatsView.setMatch(match);
        pagerAdapter.matchStatView.setMatch(match);
        setIsLoading(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        presenter.loadData();
        setIsLoading(true);
    }


    @Subscribe
    public void loadingTriggered(TriggerMatchLoadingEvent event) {
        refresh();
    }


    public class StatPagerAdapter extends PagerAdapter {

        private final PlayerStatsView homeStatsView, guestStatsView;
        private final MatchStatView matchStatView;

        public StatPagerAdapter() {
            super();
            homeStatsView = new PlayerStatsView(getActivity(), bus, true);
            guestStatsView = new PlayerStatsView(getActivity(), bus, false);
            matchStatView = new MatchStatView(getActivity(), bus);
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
                return match.teamHome.shortName;
            } else if (position == 2) {
                return match.teamGuest.shortName;
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
                    view = homeStatsView;
                    break;
                case 1:
                    view = matchStatView;
                    break;
                case 2:
                    view = guestStatsView;
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
            homeStatsView.setIsLoading(value);
            guestStatsView.setIsLoading(value);
            matchStatView.setIsLoading(value);
        }
    }
}
