package com.volleyapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.volleyapp.volleyliga.models.MatchModel;

import java.util.List;

public interface MatchListFragmentView extends MvpView {
    void setData(List<MatchModel> data);
}
