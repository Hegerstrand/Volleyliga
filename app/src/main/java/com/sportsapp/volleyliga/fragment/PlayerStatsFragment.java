package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.PlayerStatisticModelWrapper;
import com.sportsapp.volleyliga.models.PlayerStatisticsModel;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.MatchLoadingResultEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
import com.sportsapp.volleyliga.views.MatchView;
import com.sportsapp.volleyliga.views.controllers.DividerItemDecoration;
import com.sportsapp.volleyliga.views.controllers.PlayerStatsListAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

@FragmentWithArgs
public class PlayerStatsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Arg
    public boolean isHomeTeam;

    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;

    private MatchView activityListener;
    private CustomBus bus;
    private PlayerStatsListAdapter adapter;
    private MatchModel match;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MatchView) activity;
        bus = activityListener.getBus();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (bus != null) {
            bus.unregister(this);
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
        return inflater.inflate(R.layout.fragment_player_stats, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        bus.register(this);
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new PlayerStatsListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        setMatch(match);
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchLoadingEvent());
    }

    @Subscribe
    public void matchDataReceived(MatchLoadingResultEvent event) {
        if (event.match != null) {
            setMatch(event.match);
        }
    }

    public void setMatch(MatchModel match) {
        if(match != null) {
            this.match = match;
            List<PlayerStatisticsModel> input;
            if (isHomeTeam) {
                input = match.statistics.statsByPlayerHome;
            } else {
                input = match.statistics.statsByPlayerGuest;
            }
            List<PlayerStatisticModelWrapper> result = wrapStatistics(input);
            if (adapter != null) {
                adapter.setItems(result);
            }
            setIsLoading(false);
        }
    }

    private List<PlayerStatisticModelWrapper> wrapStatistics(List<PlayerStatisticsModel> input) {
        BlockingObservable<PlayerStatisticsModel> attacks = Observable.from(input)
                .filter(x -> x.spikeWins > 0)
                .toSortedList((stat1, stat2) -> compare(stat1.spikeWins, stat2.spikeWins))
                .flatMap(Observable::from).toBlocking();
        BlockingObservable<PlayerStatisticsModel> errors = Observable.from(input)
                .filter(x -> x.errorsTotal > 0)
                .toSortedList((stat1, stat2) -> compare(stat1.errorsTotal, stat2.errorsTotal))
                .flatMap(Observable::from).toBlocking();
        BlockingObservable<PlayerStatisticsModel> serves = Observable.from(input)
                .filter(x -> x.serveWins > 0)
                .toSortedList((stat1, stat2) -> compare(stat1.serveWins, stat2.serveWins))
                .flatMap(Observable::from).toBlocking();
        BlockingObservable<PlayerStatisticsModel> blocks = Observable.from(input)
                .filter(x -> x.blockWins > 0)
                .toSortedList((stat1, stat2) -> compare(stat1.blockWins, stat2.blockWins))
                .flatMap(Observable::from).toBlocking();

        List<PlayerStatisticModelWrapper> result = new ArrayList<>();
        addStats("Attack", attacks, result, stat -> stat.spikeWins);
        addStats("Serves", serves, result, stat -> stat.serveWins);
        addStats("Blocks", blocks, result, stat -> stat.blockWins);
        addStats("Errors", errors, result, stat -> stat.errorsTotal, true);

        return result;
    }

    private void addStats(String title, BlockingObservable<PlayerStatisticsModel> stats, List<PlayerStatisticModelWrapper> result, Func1<PlayerStatisticsModel, Integer> mappingFunction) {
        addStats(title, stats, result, mappingFunction, false);
    }

    private void addStats(String title, BlockingObservable<PlayerStatisticsModel> stats, List<PlayerStatisticModelWrapper> result, Func1<PlayerStatisticsModel, Integer> mappingFunction, boolean isNegativeStat) {
        List<PlayerStatisticsModel> statList = blockingObservableToList(stats);
        if (statList.size() > 0) {
            PlayerStatisticModelWrapper header = new PlayerStatisticModelWrapper(title, isNegativeStat);
            result.add(header);
            int sum = 0;
            int count = 0;
            for (PlayerStatisticsModel stat : stats.toIterable()) {
                if(count < 5){
                    result.add(new PlayerStatisticModelWrapper(stat, mappingFunction, isNegativeStat));
                }
                sum += mappingFunction.call(stat);
                count++;
            }
            header.value = sum;
        }
    }

    private List<PlayerStatisticsModel> blockingObservableToList(BlockingObservable<PlayerStatisticsModel> stats) {
        List<PlayerStatisticsModel> result = new ArrayList<>();
        for (PlayerStatisticsModel stat : stats.toIterable()) {
            result.add(stat);
        }
        return result;
    }

    private Integer compare(int value1, int value2) {
        return value1 < value2 ? 1 : -1;
    }

    private void setIsLoading(boolean value) {
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(value);
        }
    }
}
