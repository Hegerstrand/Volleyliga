package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.pocketpalsson.volleyball.models.MatchModel;

import java.util.List;

public interface MatchListFragmentView extends MvpView {
    void setData(List<MatchModel> data);
}
