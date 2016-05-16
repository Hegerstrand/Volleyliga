package com.sportsapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sportsapp.volleyliga.models.League;
import com.sportsapp.volleyliga.models.LeagueStandingModel;
import com.sportsapp.volleyliga.repositories.LeagueStandingRepository;
import com.sportsapp.volleyliga.views.ILeagueStandingView;

import java.util.List;

public class LeagueStandingPresenter extends MvpBasePresenter<ILeagueStandingView> {

    public void getStandings(League selectedLeague) {
        List<LeagueStandingModel> standings = LeagueStandingRepository.instance.getStandings(selectedLeague);
        if (isViewAttached()) {
            getView().setData(standings);
        }
    }
}
