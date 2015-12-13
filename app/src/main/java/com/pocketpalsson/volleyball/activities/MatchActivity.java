package com.pocketpalsson.volleyball.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.MatchModel;
import com.pocketpalsson.volleyball.presenters.MatchPresenter;
import com.pocketpalsson.volleyball.utilities.volley.VolleyQueue;
import com.pocketpalsson.volleyball.utilities.volley.match.GetMatchVolleyRequest;
import com.pocketpalsson.volleyball.views.MatchStatsView;
import com.pocketpalsson.volleyball.views.MatchView;
import com.pocketpalsson.volleyball.views.SetFullDetailView;
import com.r0adkll.slidr.Slidr;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchActivity extends MvpActivity<MatchView, MatchPresenter> implements MatchView, SwipeRefreshLayout.OnRefreshListener {

    public static final String FEDERATION_MATCH_NUMBER = "federationMatchNumber";
    @Bind(R.id.stats_view)
    public MatchStatsView statsView;
    //    @Bind(R.id.tvTeamHome)
//    public TextView tvTeamHome;
//    @Bind(R.id.tvTeamGuest)
//    public TextView tvTeamGuest;
    @Bind(R.id.scoreHome)
    public TextView tvHomeScore;
    @Bind(R.id.scoreGuest)
    public TextView tvGuestScore;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.allSetsView)
    public SetFullDetailView allSetsView;
    //    @Bind(R.id.ivHomeLogo)
//    public ImageView ivHomeLogo;
//    @Bind(R.id.ivGuestLogo)
//    public ImageView ivGuestLogo;
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbarLayout;

    public int federationMatchNumber;

    private MatchModel match;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_match);
        federationMatchNumber = getIntent().getExtras().getInt(FEDERATION_MATCH_NUMBER);

        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        Slidr.attach(this);
        refreshLayout.setOnRefreshListener(this);
        loadData(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Icepick.saveInstanceState(this, outState);
    }

//    @Override
//    public void showError(Throwable e, boolean pullToRefresh) {
//        super.showError(e, pullToRefresh);
//        setIsLoading(false);
//    }
//
//    @Override
//    public void showLoading(boolean pullToRefresh) {
//        super.showLoading(pullToRefresh);
//    }
//
//    @Override
//    public void showContent() {
//        super.showContent();
//        setIsLoading(false);
//    }

    private void setIsLoading(boolean value) {
        refreshLayout.setRefreshing(value);
    }

    public void setData(MatchModel data) {
        setMatchModel(data);
        setIsLoading(false);
//        showContent();
    }

    public void setMatchModel(MatchModel match) {
        statsView.setMatchStats(match.statistics);
//        ivHomeLogo.setImageDrawable(ContextCompat.getDrawable(this, match.teamHome.logoRef));
//        ivGuestLogo.setImageDrawable(ContextCompat.getDrawable(this, match.teamGuest.logoRef));
//        tvTeamHome.setText(match.teamHome.getName());
//        tvTeamGuest.setText(match.teamGuest.getName());
        tvHomeScore.setText("" + match.setsWonByHome);
        tvGuestScore.setText("" + match.setsWonByGuest);
        allSetsView.setStats(match.getSetList());
        collapsingToolbarLayout.setTitle(match.getTitle());
    }

    @NonNull
    @Override
    public MatchPresenter createPresenter() {
        return new MatchPresenter();
    }

//    @Override
//    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
//        return e.getMessage();
//    }


    public void loadData(boolean pullToRefresh) {
        VolleyQueue.instance.add(new GetMatchVolleyRequest(VolleyQueue.instance, "http://caspermunk.dk/livescore/xml/" + federationMatchNumber + ".xml", UUID.randomUUID().toString(), true,
                (value, isLastResponse) -> setData(value)));
    }

    @Override
    public void onRefresh() {
        loadData(true);
        setIsLoading(true);
    }
}
