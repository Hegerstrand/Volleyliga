package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;

import java.util.List;

public class MatchCollection {
    public List<MatchModel> matches;

    public MatchCollection(List<MatchModel> matches) {
        this.matches = matches;
    }
}
