package com.sportsapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sportsapp.volleyliga.views.TeamDetailView;

public class TeamDetailPresenter extends MvpBasePresenter<TeamDetailView> {

    private String teamXml = "<team><teamID>3</teamID><teamName>Gentofte Volley</teamName><shortTeamName>Gentofte</shortTeamName><teamInitials>GV</teamInitials><teamHomePage>http://www.gentoftevolley.dk/</teamHomePage><teamFb>gentoftevolley</teamFb><teamMail>info@gentoftevolley.dk</teamMail><teamPhone>24254846</teamPhone><teamGym>Kildeskovshallen</teamGym><teamGymAdress>Adolphsvej 25, 2820 Gentofte</teamGymAdress><teamMaps>https://www.google.dk/maps/place/Kildeskovshallen/@55.7462775,12.5499378,17z/data=!3m1!4b1!4m2!3m1!1s0x46524df33640513b:0x8951549494bb486f</teamMaps><teamGymAddress>Adolphsvej 25, 2820 Gentofte</teamGymAddress><teamLat>55.74632</teamLat><teamLon>12.551912</teamLon></team>";

    @Override
    public void attachView(TeamDetailView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }
}
