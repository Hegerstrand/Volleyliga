package com.sportsapp.volleyliga.presenters;

import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.views.MatchListActivityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;

public class MatchListPresenter extends MvpBasePresenter<MatchListActivityView> {
    private static final String TAG = "MatchListPresenter";
    private List<MatchModel> matches = new ArrayList<>();
    private HashMap<Integer, MatchModel> matchDict = new HashMap<>();

    public void loadMatches() {
        matches = new ArrayList<>();

        MatchRepository.instance.getAllMatches()
                .buffer(500, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<List<MatchModel>>() {
                    @Override
                    public void onCompleted() {
                        if (isViewAttached()) {
                            getView().loadFinished();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if(getView() != null) {
                            Toast.makeText(getView().getActivity(), "Error occured while fetching match", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(List<MatchModel> matchModelReply) {
                        addMatches(matchModelReply);
                    }
                });
//                .flatMap(response -> {
//                    String stringResponse = new String(((TypedByteArray) response.getBody()).getBytes());
//                    MatchXmlPullParser parser = new MatchXmlPullParser();
//                    MatchModel match = parser.parse(stringResponse);
//                    return Observable.just(match);
//                })
//                .buffer(500, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<MatchModel>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(List<MatchModel> matches) {
//                        if(matches.size() > 0) {
//                            addMatches(matches);
//                        }
//                    }
//                });
    }

    private void addMatches(List<MatchModel> input) {
        if (isViewAttached()) {
            getView().addMatches(input);
        }
    }

    @Override
    public void attachView(MatchListActivityView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }
}
