package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.hannesdorfmann.fragmentargs.bundler.ParcelerArgsBundler;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.presenters.TeamDetailMatchPresenter;
import com.sportsapp.volleyliga.views.MainActivityView;
import com.sportsapp.volleyliga.views.TeamDetailMatchListView;
import com.sportsapp.volleyliga.views.controllers.DividerItemDecoration;
import com.sportsapp.volleyliga.views.controllers.MatchListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class TeamDetailMatchFragment extends MvpFragment<TeamDetailMatchListView, TeamDetailMatchPresenter> implements TeamDetailMatchListView{

    @Arg(bundler = ParcelerArgsBundler.class)
    public TeamModel team;

    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;

    private MatchListAdapter adapter;

    private MainActivityView activityListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityView) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_matches, container, false);
    }

    @Override
    public TeamDetailMatchPresenter createPresenter() {
        return new TeamDetailMatchPresenter(team);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new MatchListAdapter(getContext());
        adapter.setMatchClickListener(match -> openMatch(match.federationMatchNumber));
        recyclerView.setAdapter(adapter);
        if(team != null) {
            getPresenter().loadMatches();
        }
    }

    public void setTeamModel(TeamModel team) {
        this.team = team;
        if(getPresenter() != null) {
            getPresenter().setTeam(team);
        }
    }

    @Override
    public void openMatch(int federationMatchNumber) {
        if (activityListener != null) {
            activityListener.openMatch(federationMatchNumber);
        }
    }

    @Override
    public void setData(List<MatchModel> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
    }
}
