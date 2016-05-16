package com.sportsapp.volleyliga.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.PlayerStatisticModelWrapper;
import com.sportsapp.volleyliga.models.PlayerStatisticsModel;
import com.sportsapp.volleyliga.utilities.CustomBus;
import com.sportsapp.volleyliga.utilities.busEvents.DestructionEvent;
import com.sportsapp.volleyliga.utilities.busEvents.TriggerMatchLoadingEvent;
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

public class PlayerStatsView extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener {

    public boolean isHomeTeam;

    @Bind(R.id.contentView)
    public SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;

    private CustomBus bus;
    private boolean isRegistered = false;
    private PlayerStatsListAdapter adapter;
    private MatchModel match;

    public PlayerStatsView(Context context, CustomBus bus, boolean isHomeTeam) {
        super(context);
        this.bus = bus;
        this.isHomeTeam = isHomeTeam;

        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.player_stats_view, this);
        ButterKnife.bind(this);

        if (bus != null && !isRegistered) {
            bus.register(this);
            isRegistered = true;
        }
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new PlayerStatsListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        setMatch(match);
    }

    @Subscribe
    public void onDestroy(DestructionEvent event){
        if (bus != null && isRegistered) {
            bus.unregister(this);
            isRegistered = false;
        }
    }

    @Override
    public void onRefresh() {
        bus.post(new TriggerMatchLoadingEvent());
    }

    public void setMatch(MatchModel match) {
        if (match != null) {
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
                if (count < 5) {
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

    public void setIsLoading(boolean value) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(value);
        }
    }

}
