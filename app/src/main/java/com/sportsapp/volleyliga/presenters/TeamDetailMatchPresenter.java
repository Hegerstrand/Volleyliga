package com.sportsapp.volleyliga.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.views.TeamDetailMatchListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeamDetailMatchPresenter extends MvpBasePresenter<TeamDetailMatchListView> {
    private List<MatchModel> matches = new ArrayList<>();
    private TeamModel team;

    public TeamDetailMatchPresenter(TeamModel team) {
        this.team = team;
    }

    public void loadMatches() {
        matches = new ArrayList<>();
        List<String> matchIds = Arrays.asList("110236", "110237", "110238", "110243", "110241", "110240", "110242", "110255", "110246", "110245", "110244", "110247", "110248", "110249", "110251", "110250", "110252", "110254", "110239", "110253", "110257", "110256", "110259", "110258", "110262", "110260", "110261", "110291", "110268", "110271", "110270", "110269", "110264", "110266", "110265", "110267", "110272", "110274", "110275", "110273", "110277", "110276", "110279", "110263", "110278", "110281", "110282", "110280", "110283", "110286", "110285", "110287", "110284", "110288", "110290", "110289", "110292", "110294", "110293", "110299", "110296", "110297", "110298", "110295", "110301", "110303", "110300", "110304", "110305", "110306", "110307", "110311", "110309", "110308", "110310", "110302", "110313", "110312", "110314", "110315", "110319", "110317", "110316", "110318", "110327", "110324", "110325", "110326", "110321", "110322", "110320", "110323", "110330", "110331", "110328", "110329", "110332", "110335", "110333", "110334", "110337", "110339", "110336", "110338", "110344", "110341", "110342", "110340", "110343", "110345", "110346", "110347");
//        Observable.from(matchIds)
//                .flatMap(federationMatchNumber -> MatchApi.getInstance("http://caspermunk.dk/livescore/xml").getResponse(federationMatchNumber + ".xml"))
//                .flatMap(response -> {
//                    String stringResponse = new String(((TypedByteArray) response.getBody()).getBytes());
//                    MatchXmlPullParser parser = new MatchXmlPullParser();
//                    MatchModel match = parser.parse(stringResponse);
//                    return Observable.just(match);
//                })
//                .filter(match -> match.teamHome.equals(team) || match.teamGuest.equals(team))
//                .buffer(500, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<MatchModel>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        String s = "";
//                    }
//
//                    @Override
//                    public void onNext(List<MatchModel> matches) {
//                        if (matches.size() > 0) {
//                            addMatches(matches);
//                        }
//                    }
//                });
    }

    private void addMatches(List<MatchModel> input) {
        matches.addAll(input);
        Collections.sort(matches, new MatchModel.MatchComparator());
        if (isViewAttached()) {
            getView().setData(matches);
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
        matches.clear();
        this.team = team;
        if (team != null) {
            loadMatches();
        }
    }
}
