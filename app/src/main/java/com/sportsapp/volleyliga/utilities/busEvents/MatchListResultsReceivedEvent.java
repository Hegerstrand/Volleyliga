package com.sportsapp.volleyliga.utilities.busEvents;

import com.sportsapp.volleyliga.models.MatchModel;

import java.util.List;

public class MatchListResultsReceivedEvent {

    public List<MatchModel> past;
    public List<MatchModel> live;
    public List<MatchModel> future;

    public MatchListResultsReceivedEvent(List<MatchModel> past, List<MatchModel> live, List<MatchModel> future) {
        this.past = past;
        this.live = live;
        this.future = future;
    }

    public List<MatchModel> getMatches(MatchModel.Type type) {
        switch(type){
            case PAST:
                return past;
            case LIVE:
                return live;
            case FUTURE:
                return future;
        }
        return null;
    }
}
