package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;

import java.util.List;

public interface MatchListFragmentView extends MvpView {
    void setData(List<MatchModel> data);
}
