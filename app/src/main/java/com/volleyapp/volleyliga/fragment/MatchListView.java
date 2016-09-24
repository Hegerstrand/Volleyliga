package com.volleyapp.volleyliga.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.utilities.BusProvider;
import com.volleyapp.volleyliga.utilities.CustomBus;
import com.volleyapp.volleyliga.utilities.busEvents.DestructionEvent;
import com.volleyapp.volleyliga.utilities.busEvents.MatchListResultsReceivedEvent;
import com.volleyapp.volleyliga.utilities.busEvents.OpenMatchEvent;
import com.volleyapp.volleyliga.utilities.busEvents.TriggerMatchListLoadingEvent;
import com.volleyapp.volleyliga.views.controllers.DividerItemDecoration;
import com.volleyapp.volleyliga.views.controllers.MatchListAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchListView extends LinearLayout implements OnRefreshListener {
    public MatchModel.Type matchType;

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.contentView)
    public SwipeRefreshLayout contentView;
    @Bind(R.id.emptyView)
    public RelativeLayout emptyView;
    @Bind(R.id.tvNoMatchesFound)
    public TextView tvNoMatchesFound;


    private MatchListAdapter adapter;

    private CustomBus bus;
    private boolean isRegistered = false;
    private List<MatchModel> matches = new ArrayList<>();

    public MatchListView(Context context, CustomBus bus, MatchModel.Type matchType) {
        super(context);
        this.bus = bus;
        this.matchType = matchType;
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.match_list_view, this);
        ButterKnife.bind(this);

        adapter = new MatchListAdapter(getContext());
        adapter.setMatchClickListener(match -> openMatch(match.federationMatchNumber));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        contentView.setOnRefreshListener(this);

        if (bus != null && !isRegistered) {
            bus.register(this);
            isRegistered = true;
        }
        setData(matches);
    }


    @Subscribe
    public void onDestroy(DestructionEvent event) {
        if (bus != null && isRegistered) {
            bus.unregister(this);
            isRegistered = false;
        }
    }


    @Subscribe
    public void matchDataReceived(MatchListResultsReceivedEvent event) {
        if (matchType != MatchModel.Type.PAST) {
            return;
        }
        matches = event.getMatches(matchType);
        setData(matches);
    }

    public void setData(List<MatchModel> data) {
        if (adapter != null && data != null) {
            if (matches.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                tvNoMatchesFound.setText(getContext().getString(R.string.no) + " " + getTypeName() + " " + getContext().getString(R.string.matches_found));
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            adapter.setItems(matches);
        }
        setIsLoading(false);
    }

    private String getTypeName() {
        switch (matchType) {
            case PAST:
                return getContext().getString(R.string.archived);
            case TODAY:
                return getContext().getString(R.string.today);
            case FUTURE:
                return getContext().getString(R.string.future);
        }
        return "";
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchListLoadingEvent());
    }

    public void openMatch(int federationMatchNumber) {
        BusProvider.getInstance().post(new OpenMatchEvent(federationMatchNumber));
    }

    public void setIsLoading(boolean value) {
        if (contentView != null) {
            contentView.setRefreshing(value);
        }
    }
}
