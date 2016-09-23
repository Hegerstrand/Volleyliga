package com.volleyapp.volleyliga.utilities.busEvents;

import com.volleyapp.volleyliga.models.MatchModel;

public class MatchLoadingResultEvent {
    public MatchModel match;

    public MatchLoadingResultEvent(MatchModel match) {
        this.match = match;
    }
}
