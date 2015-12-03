package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface MainActivityView extends MvpView {
    void openMatch(int federationMatchNumber);
}
