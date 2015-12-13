package com.pocketpalsson.volleyball.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.MatchModel;
import com.pocketpalsson.volleyball.presenters.MatchListPresenter;
import com.pocketpalsson.volleyball.views.MainActivityView;
import com.pocketpalsson.volleyball.views.MatchListView;
import com.pocketpalsson.volleyball.views.controllers.MatchListAdapter;
import com.pocketpalsson.volleyball.views.controllers.SpacesItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MatchListFragment extends MvpFragment<MatchListView, MatchListPresenter> implements MatchListView, OnRefreshListener {

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;

    private MatchListAdapter adapter;

    private MainActivityView activityListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityView) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = new MatchListAdapter(getActivity());
        adapter.setMatchClickListener(match -> {
            if (getPresenter() != null) {
                openMatch(match.federationMatchNumber);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.default_card_padding);
        recyclerView.addItemDecoration(new SpacesItemDecoration(getActivity(), SpacesItemDecoration.VERTICAL_LIST, spacingInPixels));
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    @Override
    public MatchListPresenter createPresenter() {
        return new MatchListPresenter();
    }


    public void setData(List<MatchModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

    public void loadData(boolean pullToRefresh) {
        if (getPresenter() != null) {
            getPresenter().loadMatches();
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

    public void openMatch(int federationMatchNumber) {
        if(activityListener != null) {
            activityListener.openMatch(federationMatchNumber);
        }
    }

    public MainActivityView getActivityView() {
        return activityListener;
    }

    private void setIsLoading(boolean value) {
        refreshLayout.setRefreshing(value);
    }
}
