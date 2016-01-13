package com.sportsapp.volleyliga.utilities.busEvents;

import com.sportsapp.volleyliga.models.MatchModel;

public class MatchLoadingResultEvent {
    public MatchModel match;

    public MatchLoadingResultEvent(MatchModel match) {
        this.match = match;
    }
}
