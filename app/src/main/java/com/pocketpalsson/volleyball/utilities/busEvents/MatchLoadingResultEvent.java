package com.pocketpalsson.volleyball.utilities.busEvents;

import com.pocketpalsson.volleyball.models.MatchModel;

public class MatchLoadingResultEvent {
    public MatchModel match;

    public MatchLoadingResultEvent(MatchModel match) {
        this.match = match;
    }
}
