package com.sportsapp.volleyliga.repositories;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.sportsapp.volleyliga.models.CachedMatch;
import com.sportsapp.volleyliga.models.MatchModel;

import java.io.File;
import java.util.List;

import rx.Observable;

public class MatchCache {
    private File cacheDirectory;

    public MatchCache(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public void save(MatchModel data) {
        CachedMatch cachedMatch = new Select()
                .from(CachedMatch.class)
                .where("matchNumber = ?", data.federationMatchNumber)
                .executeSingle();
        if(cachedMatch != null){
            cachedMatch.matchJson = new Gson().toJson(data);
            cachedMatch.save();
        }
    }

    public void save(MatchModel data, MatchModel.Type type) {
        CachedMatch cachedMatch = new Select()
                .from(CachedMatch.class)
                .where("matchNumber = ?", data.federationMatchNumber)
                .executeSingle();
        data.matchType = type;
        if(cachedMatch != null){
            cachedMatch.matchJson = new Gson().toJson(data);
            cachedMatch.matchType = type.ordinal();
            cachedMatch.save();
        } else {
            cachedMatch = new CachedMatch();
            cachedMatch.matchNumber = data.federationMatchNumber;
            cachedMatch.matchType = type.ordinal();
            cachedMatch.matchJson = new Gson().toJson(data);
            cachedMatch.save();
        }
    }

    public MatchModel retrieve(int matchNumber) {
        CachedMatch cachedMatch = new Select()
                .from(CachedMatch.class)
                .where("matchNumber = ?", matchNumber)
                .executeSingle();
        if(cachedMatch != null){
            return cachedMatch.getMatch();
        }
        return null;
    }

    public Observable<MatchModel> getMatchesByType(MatchModel.Type matchType) {
        List<CachedMatch> cachedMatches = new Select()
                .from(CachedMatch.class)
                .where("matchType = ?", matchType.ordinal())
                .execute();
        return Observable.from(cachedMatches).map(CachedMatch::getMatch);
    }
}
