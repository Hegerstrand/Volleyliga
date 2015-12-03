package com.pocketpalsson.volleyball.presenters;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.pocketpalsson.volleyball.views.MatchView;

public class MatchPresenter extends MvpBasePresenter<MatchView> implements MvpPresenter<MatchView> {

    @Override
    public void attachView(MatchView view) {
    }
}
