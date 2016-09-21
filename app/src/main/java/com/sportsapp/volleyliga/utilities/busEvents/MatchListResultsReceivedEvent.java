package com.volleyapp.volleyliga.utilities.busEvents;

import com.volleyapp.volleyliga.models.MatchModel;

import java.util.ArrayList;
import java.util.List;

public class MatchListResultsReceivedEvent {

    public List<MatchModel> past = new ArrayList<>();
    public List<MatchModel> today = new ArrayList<>();
    public List<MatchModel> future = new ArrayList<>();

    public MatchListResultsReceivedEvent(List<MatchModel> past, List<MatchModel> today, List<MatchModel> future) {
        this.past = past;
        this.today = today;
        this.future = future;
    }

    public List<MatchModel> getMatches(MatchModel.Type type) {
        switch (type) {
            case PAST:
                return past;
            case TODAY:
                return today;
            case FUTURE:
                return future;
        }
        return null;
    }
}
