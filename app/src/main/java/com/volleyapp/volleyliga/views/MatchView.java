package com.volleyapp.volleyliga.views;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.volleyapp.volleyliga.models.MatchModel;

public interface MatchView extends MvpView {
    Context getActivity();

    void setMatch(MatchModel match);
}
