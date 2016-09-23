package com.volleyapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.volleyapp.volleyliga.models.League;
import com.volleyapp.volleyliga.models.LeagueStandingModel;
import com.volleyapp.volleyliga.repositories.LeagueStandingRepository;
import com.volleyapp.volleyliga.views.ILeagueStandingView;

import java.util.List;

public class LeagueStandingPresenter extends MvpBasePresenter<ILeagueStandingView> {

    public void getStandings(League selectedLeague) {
        List<LeagueStandingModel> standings = LeagueStandingRepository.instance.getStandings(selectedLeague);
        if (isViewAttached()) {
            getView().setData(standings);
        }
    }
}
