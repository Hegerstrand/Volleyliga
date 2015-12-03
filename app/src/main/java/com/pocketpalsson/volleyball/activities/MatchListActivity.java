package com.pocketpalsson.volleyball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.pocketpalsson.volleyball.NavigationDrawerFragment;
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


public class MatchListActivity extends MvpLceActivity<SwipeRefreshLayout, List<MatchModel>, MatchListView, MatchListPresenter> implements MatchListView, OnRefreshListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.contentView)
    SwipeRefreshLayout refreshLayout;

    private MatchListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        ButterKnife.bind(this);
        adapter = new MatchListAdapter(this);
        adapter.setMatchClickListener(match -> {
            if (getPresenter() != null) {
                openMatch(match.federationMatchNumber);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.match_list_item_padding);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        contentView.setOnRefreshListener(this);

        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    private void closeNavDrawer() {
        NavigationDrawerFragment drawerFragment = getNavDrawerFragment();
        if (drawerFragment != null) {
            drawerFragment.closeDrawer();
        }
    }

    private NavigationDrawerFragment getNavDrawerFragment() {
        return (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
    }

    @Override
    public MatchListPresenter createPresenter() {
        return new MatchListPresenter();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void setData(List<MatchModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

    @Override
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
        Intent intent = Henson.with(this).gotoMatchActivity().federationMatchNumber(federationMatchNumber).build();
        startActivity(intent);
//        Fragment fragment = null;
//        fragment = new MatchFragmentBuilder(federationMatchNumber).build();
//        addFragment(fragment, "" + federationMatchNumber, false);
        closeNavDrawer();
    }

    private void setIsLoading(boolean value) {
        refreshLayout.setRefreshing(value);
    }

    public MainActivityView getActivityView() {
        return (MainActivityView) this;
    }
}
