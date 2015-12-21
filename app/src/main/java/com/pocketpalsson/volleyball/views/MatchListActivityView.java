package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.pocketpalsson.volleyball.models.MatchModel;
import com.pocketpalsson.volleyball.utilities.CustomBus;

import java.util.List;

public interface MatchListActivityView extends MvpView {
    void openMatch(int federationMatchNumber);

    void setData(List<MatchModel> data);
    CustomBus getBus();
}
