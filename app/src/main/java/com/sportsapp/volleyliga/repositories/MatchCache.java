package com.sportsapp.volleyliga.repositories;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.sportsapp.volleyliga.models.CachedMatch;
import com.sportsapp.volleyliga.models.MatchModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        if (cachedMatch != null) {
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
        if (cachedMatch != null) {
            cachedMatch.matchJson = new Gson().toJson(data);
            cachedMatch.matchType = type.ordinal();
            cachedMatch.save();
        } else {
            cachedMatch = new CachedMatch();
            cachedMatch.matchNumber = data.federationMatchNumber;
            cachedMatch.homeTeam = data.teamHome.id;
            cachedMatch.guestTeam = data.teamGuest.id;
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
        if (cachedMatch != null) {
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

    public Observable<MatchModel> getMatchesForTeam(int teamId) {
        List<CachedMatch> cachedMatches = new Select()
                .from(CachedMatch.class)
                .where("homeTeam = ? OR guestTeam = ?", teamId, teamId)
                .execute();
        return Observable.from(cachedMatches).map(CachedMatch::getMatch);
    }

    public List<MatchModel> getFutureMatchesForTeam(int teamId) {
        //HACK: Change type to FUTURE
        List<CachedMatch> cachedMatches = new Select()
                .from(CachedMatch.class)
                .where("matchType = ? AND (homeTeam = ? OR guestTeam = ?)", MatchModel.Type.PAST.ordinal(), teamId, teamId)
                .execute();
        List<MatchModel> result = new ArrayList<>();
        for (CachedMatch cachedMatch : cachedMatches) {
            result.add(cachedMatch.getMatch());
        }
        Collections.sort(result, new MatchModel.MatchComparator());
        return result;
    }

    public MatchModel getMostRecentMatchBetweenTeams(int teamId1, int teamId2, Date comparisonDate) {
        List<CachedMatch> cachedMatches = new Select()
                .from(CachedMatch.class)
                .where("matchType = 0 AND ((homeTeam = ? AND guestTeam = ?) OR (homeTeam = ? AND guestTeam = ?))", teamId1, teamId2, teamId2, teamId1)
                .execute();
        List<MatchModel> result = new ArrayList<>();
        for (CachedMatch cachedMatch : cachedMatches) {
            MatchModel match = cachedMatch.getMatch();
            if (match.matchDateTime.before(comparisonDate)) {
                result.add(match);
            }
        }
        if (result.size() == 0) {
            return null;
        }
        Collections.sort(result, new MatchModel.MatchComparator());
        return result.get(result.size() - 1);
    }
}
