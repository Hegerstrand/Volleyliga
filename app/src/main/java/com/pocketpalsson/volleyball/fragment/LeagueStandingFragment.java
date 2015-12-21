package com.pocketpalsson.volleyball.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.LeagueStandingModel;
import com.pocketpalsson.volleyball.presenters.LeagueStandingPresenter;
import com.pocketpalsson.volleyball.views.LeagueStandingView;
import com.pocketpalsson.volleyball.views.MainActivityView;
import com.pocketpalsson.volleyball.views.controllers.DividerItemDecoration;
import com.pocketpalsson.volleyball.views.controllers.LeagueStandingAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LeagueStandingFragment extends MvpFragment<LeagueStandingView, LeagueStandingPresenter> implements LeagueStandingView, OnRefreshListener {

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;

    private LeagueStandingAdapter adapter;

    private MainActivityView activityListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityView) activity;
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

        adapter = new LeagueStandingAdapter(getActivity());
        adapter.setLeagueStandingClickListener(leagueStanding -> {
//            if (getPresenter() != null) {
//                openMatch(match.federationMatchNumber);
//            }
        });
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("League standing");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    @Override
    public LeagueStandingPresenter createPresenter() {
        return new LeagueStandingPresenter();
    }


    public void setData(List<LeagueStandingModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

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

}
