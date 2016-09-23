package com.volleyapp.volleyliga.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Strings;
import com.volleyapp.volleyliga.models.League;
import com.volleyapp.volleyliga.models.TimeUnit;

import java.util.HashSet;

public class Preferences {

    public static final String PREFERENCES_TAG = "default";
    private static final String SELECTED_LEAGUE = "selected_league";
    private static final String FAVORITE_TEAMS = "favoriteTeams";
    private static final String SHOULD_NOTIFY_IN_ADVANCE = "shouldNotifyInAdvance";
    private static final String NOTIFY_IN_ADVANCE_TIME_LENGTH = "notifyInAdvanceTimeLength";
    private static final String NOTIFY_IN_ADVANCE_TIME_UNIT = "notifyInAdvanceTimeUnit";
    private final SharedPreferences sp;

    private Preferences(Context context) {
        sp = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
    }

    public static Preferences with(Context context) {
        return new Preferences(context);
    }


    public League getLeague() {
        int index = sp.getInt(SELECTED_LEAGUE, 0);
        return League.values()[index];
    }

    public void saveLeague(League league) {
        sp.edit().putInt(SELECTED_LEAGUE, league.ordinal()).apply();
    }

    public void setFavoriteTeam(int teamId, boolean value) {
        HashSet<Integer> favoriteTeams = getFavoriteTeams();
        if (value && !favoriteTeams.contains(teamId)) {
            favoriteTeams.add(teamId);
        }
        if (!value && favoriteTeams.contains(teamId)) {
            favoriteTeams.remove(teamId);
        }
        saveFavoriteTeams(favoriteTeams);
    }

    public HashSet<Integer> getFavoriteTeams() {
        String[] favoriteTeams = sp.getString(FAVORITE_TEAMS, "").split("|");
        HashSet<Integer> result = new HashSet<>();
        for (String team : favoriteTeams) {
            if(!Strings.isNullOrEmpty(team) && !team.equalsIgnoreCase("|")) {
                result.add(Integer.parseInt(team));
            }
        }
        return result;
    }

    private void saveFavoriteTeams(HashSet<Integer> favoriteTeams) {
        String result = "";
        String sep = "";
        for (Integer team : favoriteTeams) {
            result += sep + team;
            sep = "|";
        }
        sp.edit().putString(FAVORITE_TEAMS, result).commit();
    }

    public boolean isFavoriteTeam(int teamId) {
        return getFavoriteTeams().contains(teamId);
    }

    public boolean getShouldNotifyInAdvance() {
        return sp.getBoolean(SHOULD_NOTIFY_IN_ADVANCE, true);
    }

    public boolean setShouldNotifyInAdvance(boolean value) {
        return sp.edit().putBoolean(SHOULD_NOTIFY_IN_ADVANCE, value).commit();
    }

    public int getNotifyInAdvanceTimeLength() {
        return sp.getInt(NOTIFY_IN_ADVANCE_TIME_LENGTH, 2);
    }

    public void setNotifyInAdvanceTimeLength(int value) {
        sp.edit().putInt(NOTIFY_IN_ADVANCE_TIME_LENGTH, value).commit();
    }

    public TimeUnit getNotifyInAdvanceTimeUnit() {
        int index = sp.getInt(NOTIFY_IN_ADVANCE_TIME_UNIT, 1);
        return TimeUnit.values()[index];
    }

    public void setNotifyInAdvanceTimeUnit(TimeUnit value) {
        sp.edit().putInt(NOTIFY_IN_ADVANCE_TIME_UNIT, value.ordinal()).commit();
    }
}
