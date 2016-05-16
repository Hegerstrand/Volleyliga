package com.sportsapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.views.TeamDetailView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TeamDetailPresenter extends MvpBasePresenter<TeamDetailView> {
    private List<MatchModel> matches = new ArrayList<>();
    private TeamModel team;

    public TeamDetailPresenter() {
    }

    public void setTeam(TeamModel team) {
        matches.clear();
        this.team = team;
        if (team != null) {
            loadMatches();
        }
    }

    public void setTeamId(int teamId) {
        team = TeamRepository.instance.getTeam(teamId);
        getView().setTeamModel(team);
        loadMatches();
    }

    public void loadMatches() {
        matches = new ArrayList<>();

        MatchRepository.instance.getMatchesForTeamFromCache(team.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MatchModel>() {
                    @Override
                    public void onCompleted() {
                        Collections.sort(matches, new MatchModel.MatchComparator());
                        if (isViewAttached()) {
                            getView().setData(matches);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String s = "";
                    }

                    @Override
                    public void onNext(MatchModel match) {
                        matches.add(match);
                    }
                });
    }

    @Override
    public void attachView(TeamDetailView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

}
