package com.volleyapp.volleyliga.models;

import com.volleyapp.volleyliga.repositories.TeamRepository;
import com.volleyapp.volleyliga.utilities.Util;

import java.util.Comparator;

public class LeagueStandingModel {
    public String teamName;
    public int points, position, matchesWon, matchesLost, pointsWon, pointsLost, disqualifications, penalties, matches30, matches31, matches32, matches23, matches13, matches03, setRatio, pointsRatio;

    public LeagueStandingModel() {
    }

    public TeamModel getTeam() {
        if(Util.isNullOrEmpty(teamName)){
            return null;
        }
        return TeamRepository.instance.getTeam(teamName);
    }

    public String getPositionText() {
        String suffix;
        switch(position % 10){
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix = "nd";
                break;
            case 3:
                suffix = "rd";
                break;
            default:
                suffix = "th";
        }
        return position + suffix;
    }

    public static class LeagueStandingComparator implements Comparator<LeagueStandingModel> {
        public int compare(LeagueStandingModel entry1, LeagueStandingModel entry2) {
            return entry1.position - entry2.position;
        }
    }
}
