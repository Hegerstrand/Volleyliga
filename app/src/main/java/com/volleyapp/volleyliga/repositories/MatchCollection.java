package com.volleyapp.volleyliga.repositories;

import com.volleyapp.volleyliga.models.MatchModel;

import java.util.List;

public class MatchCollection {
    public List<MatchModel> matches;

    public MatchCollection(List<MatchModel> matches) {
        this.matches = matches;
    }
}
