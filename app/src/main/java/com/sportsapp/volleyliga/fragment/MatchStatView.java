package com.sportsapp.volleyliga.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.utilities.BusProvider;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.DestructionEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenMatchEvent;
import com.sportsapp.volleyliga.utilities.busEvents.OpenTeamEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.sportsapp.volleyliga.views.MatchStatsView;
import com.sportsapp.volleyliga.views.MatchWithScoreView;
import com.sportsapp.volleyliga.views.SetFullDetailView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchStatView extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener {

    public MatchModel match;

    @Bind(R.id.stats_view)
    public MatchStatsView statsView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.allSetsView)
    public SetFullDetailView allSetsView;
    @Bind(R.id.lastMatchCard)
    public CardView lastMatchCard;
    @Bind(R.id.lastMatch)
    public MatchWithScoreView lastMatch;

    @Bind(R.id.ivHomeLogo)
    public ImageView ivHomeLogo;
    @Bind(R.id.ivGuestLogo)
    public ImageView ivGuestLogo;


    private boolean initialized = false;
    private boolean isRegistered = false;
    private CustomBus bus;
    private MatchModel mostRecentMatch;

    public MatchStatView(Context context, CustomBus bus) {
        super(context);
        this.bus = bus;

        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.fragment_match_stats, this);
        ButterKnife.bind(this);

        if (bus != null && !isRegistered) {
            bus.register(this);
            isRegistered = true;
        }
        initialized = true;
        refreshLayout.setOnRefreshListener(this);
        updateUI();
    }

    @Subscribe
    public void onDestroy(DestructionEvent event) {
        if (bus != null && isRegistered) {
            bus.unregister(this);
            isRegistered = false;
        }
    }

    public void setMatch(MatchModel match) {
        this.match = match;
        statsView.setMatchStats(match.statistics);
        ivHomeLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamHome.logoRef));
        ivGuestLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamGuest.logoRef));
        allSetsView.setStats(match.getSetList());
        mostRecentMatch = MatchRepository.instance.getMostRecentMatchBetweenTeams(match.teamHome.id, match.teamGuest.id, match.matchDateTime);
        if (mostRecentMatch != null) {
            lastMatchCard.setVisibility(VISIBLE);
            lastMatch.setMatchModel(mostRecentMatch, false);
        }
        setIsLoading(false);
        updateUI();
    }


    private void updateUI() {
        if (initialized && match != null) {
            statsView.setMatchStats(match.statistics);
        }
    }

    @OnClick(R.id.lastMatchCard)
    public void lastMatchClicked(){
        if(mostRecentMatch != null) {
            BusProvider.getInstance().post(new OpenMatchEvent(mostRecentMatch.federationMatchNumber));
        }
    }

    @OnClick(R.id.ivHomeLogo)
    public void homeTeamClicked() {
        if (match.teamHome.id > 0) {
            BusProvider.getInstance().post(new OpenTeamEvent(match.teamHome.id));
        }
    }

    @OnClick(R.id.ivGuestLogo)
    public void guestTeamClicked() {
        if (match.teamGuest.id > 0) {
            BusProvider.getInstance().post(new OpenTeamEvent(match.teamGuest.id));
        }
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchLoadingEvent());
    }

    public void setIsLoading(boolean value) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(value);
        }
    }
}
