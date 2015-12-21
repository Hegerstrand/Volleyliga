package com.pocketpalsson.volleyball.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.pocketpalsson.volleyball.models.LeagueStandingModel;
import com.pocketpalsson.volleyball.repositories.TeamRepository;
import com.pocketpalsson.volleyball.views.LeagueStandingView;

import java.util.ArrayList;
import java.util.List;

public class LeagueStandingPresenter extends MvpBasePresenter<LeagueStandingView> {
    private List<LeagueStandingModel> standings = new ArrayList<>();


    public void loadStandings() {
        standings = new ArrayList<>();
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Gentofte Volley"), 5, 5, 0, 14, 1));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Boldklubben Marienlyst"), 5, 4, 1, 11, 2));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Hvidovre VK"), 5, 3, 2, 10, 3));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("ASV Århus"), 5, 3, 2, 8, 4));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Ishøj Volley"), 5, 2, 3, 6, 5));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Randers Novo Volley"), 5, 1, 4, 4, 6));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Lyngby-Gladsaxe Volley"), 5, 1, 4, 4, 7));
        standings.add(new LeagueStandingModel(TeamRepository.instance.getTeam("Middelfart VK"), 5, 1, 4, 3, 8));
        if (isViewAttached()) {
            getView().setData(standings);
        }
//        List<String> matchIds = Arrays.asList("110236");
    }

    @Override
    public void attachView(LeagueStandingView view) {
        standings = new ArrayList<>();
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

}
