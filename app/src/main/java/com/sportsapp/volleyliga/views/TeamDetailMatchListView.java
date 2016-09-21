package com.volleyapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.volleyapp.volleyliga.models.MatchModel;

import java.util.List;

public interface TeamDetailMatchListView extends MvpView {
    void openMatch(int federationMatchNumber);

    void setMatches(List<MatchModel> data);
}
