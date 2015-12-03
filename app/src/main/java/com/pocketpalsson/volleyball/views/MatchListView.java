package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.pocketpalsson.volleyball.models.MatchModel;

import java.util.List;

public interface MatchListView extends MvpView, MvpLceView<List<MatchModel>> {
    MainActivityView getActivityView();
}
