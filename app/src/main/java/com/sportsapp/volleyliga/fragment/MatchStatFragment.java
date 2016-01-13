package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchLoadingResultEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.sportsapp.volleyliga.views.MatchStatsView;
import com.sportsapp.volleyliga.views.MatchView;
import com.sportsapp.volleyliga.views.SetFullDetailView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@FragmentWithArgs
public class MatchStatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public MatchModel match;

    @Bind(R.id.stats_view)
    public MatchStatsView statsView;
    @Bind(R.id.ivHomeLogo)
    public ImageView ivHomeLogo;
    @Bind(R.id.ivGuestLogo)
    public ImageView ivGuestLogo;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.allSetsView)
    public SetFullDetailView allSetsView;
//    @Bind(R.id.tvReferee1)
//    public TextView tvReferee1;
//    @Bind(R.id.tvReferee2)
//    public TextView tvReferee2;

    private boolean initialized = false;
    private MatchView activityListener;
    private CustomBus bus;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MatchView) activity;
        bus = activityListener.getBus();
        bus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(bus != null){
            bus.unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initialized = true;
        refreshLayout.setOnRefreshListener(this);
        updateUI();
    }

    public void setMatchModel(MatchModel match) {
        this.match = match;
        statsView.setMatchStats(match.statistics);
        ivHomeLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamHome.logoRef));
        ivGuestLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamGuest.logoRef));
        allSetsView.setStats(match.getSetList(), match.setsWonByHome, match.setsWonByGuest);
//        tvReferee1.setText(match.referee1Name);
//        tvReferee2.setText(match.referee2Name);
        updateUI();
    }


    private void updateUI() {
        if(initialized && match != null) {
            statsView.setMatchStats(match.statistics);
        }
    }

    @OnClick(R.id.ivHomeLogo)
    public void homeTeamClicked(){
        bus.post(new OpenTeamEvent(match.teamHome.id));
    }

    @OnClick(R.id.ivGuestLogo)
    public void guestTeamClicked(){
        bus.post(new OpenTeamEvent(match.teamGuest.id));
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchLoadingEvent());
    }

    @Subscribe
    public void matchDataReceived(MatchLoadingResultEvent event){
        if(event.match != null) {
            setMatchModel(event.match);
        }
        setIsLoading(false);
    }

    private void setIsLoading(boolean value) {
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(value);
        }
    }
}
