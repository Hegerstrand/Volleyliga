package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.pocketpalsson.volleyball.models.LeagueStandingModel;

import java.util.List;

public interface LeagueStandingView extends MvpView, MvpLceView<List<LeagueStandingModel>> {
}
