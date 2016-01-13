package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchListResultsReceivedEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchListLoadingEvent;
import com.sportsapp.volleyliga.views.MatchListActivityView;
import com.sportsapp.volleyliga.views.controllers.DividerItemDecoration;
import com.sportsapp.volleyliga.views.controllers.MatchListAdapter;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class MatchListFragment extends Fragment implements OnRefreshListener {

    @Arg
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

    private MatchListActivityView activityListener;
    private CustomBus bus;
    private List<MatchModel> matches;
    private boolean isRegistered = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MatchListActivityView) activity;
        bus = activityListener.getBus();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (bus != null && isRegistered) {
            bus.unregister(this);
            isRegistered = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
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

        if (bus != null && !isRegistered) {
            bus.register(this);
            isRegistered = true;
        }

        adapter = new MatchListAdapter(getActivity());
        adapter.setMatchClickListener(match -> openMatch(match.federationMatchNumber));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);

        contentView.setOnRefreshListener(this);
        setData(matches);
    }

    @Subscribe
    public void matchDataReceived(MatchListResultsReceivedEvent event) {
        matches = event.getMatches(matchType);
        if (matches != null) {
            setData(matches);
        }
    }

    public void setData(List<MatchModel> data) {
        if (adapter != null && data != null) {
            if (data.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                tvNoMatchesFound.setText("No " + getTypeName() + " matches found");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            adapter.setItems(data);
        }
        setIsLoading(false);
    }

    private String getTypeName() {
        switch (matchType) {
            case PAST:
                return "archived";
            case LIVE:
                return "live";
            case FUTURE:
                return "future";
        }
        return "";
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchListLoadingEvent());
    }


    public void openMatch(int federationMatchNumber) {
        if (activityListener != null) {
            activityListener.openMatch(federationMatchNumber);
        }
    }

    private void setIsLoading(boolean value) {
        if (contentView != null) {
            contentView.setRefreshing(value);
        }
    }
}
