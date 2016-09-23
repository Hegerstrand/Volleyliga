package com.volleyapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.models.TeamModel;
import com.volleyapp.volleyliga.repositories.MatchRepository;
import com.volleyapp.volleyliga.views.TeamDetailMatchListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TeamDetailMatchPresenter extends MvpBasePresenter<TeamDetailMatchListView> {
    private List<MatchModel> matches = new ArrayList<>();
    private TeamModel team;

    public TeamDetailMatchPresenter() {
    }

    public void loadMatches() {
        matches = new ArrayList<>();

        MatchRepository.instance.getMatchesForTeamFromCache(team.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MatchModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String s = "";
                    }

                    @Override
                    public void onNext(MatchModel match) {
                        addMatch(match);
                    }
                });
    }

    private void addMatch(MatchModel input) {
        matches.add(input);
        Collections.sort(matches, new MatchModel.MatchComparator());
        if (isViewAttached()) {
            getView().setMatches(matches);
        }
    }

    @Override
    public void attachView(TeamDetailMatchListView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

    public void setTeam(TeamModel team) {
        this.team = team;
        matches.clear();
        if (team != null) {
            loadMatches();
        }
    }
}
