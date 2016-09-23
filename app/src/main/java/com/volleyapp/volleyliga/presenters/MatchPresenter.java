package com.volleyapp.volleyliga.presenters;

import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.repositories.MatchRepository;
import com.volleyapp.volleyliga.views.MatchView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MatchPresenter extends MvpBasePresenter<MatchView> {

    private int federationMatchNumber;

    public MatchPresenter(int federationMatchNumber) {
        this.federationMatchNumber = federationMatchNumber;
    }

    public void loadData() {
        MatchRepository.instance.getMatch(federationMatchNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MatchModel>() {
                    @Override
                    public void onCompleted() {
                        String s = "";
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (getView() != null) {
                            Toast.makeText(getView().getActivity(), "Error occured while fetching match", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(MatchModel match) {
                        if (isViewAttached()) {
                            getView().setMatch(match);
                        }
                    }
                });
    }
}
