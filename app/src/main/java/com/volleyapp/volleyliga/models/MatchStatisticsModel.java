package com.volleyapp.volleyliga.models;

import com.volleyapp.volleyliga.utilities.Util;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
//JOLN
public class MatchStatisticsModel {
    public StatisticModel attacks = new StatisticModel("Attacks");
    public StatisticModel blocks = new StatisticModel("Blocks");
    public StatisticModel serves = new StatisticModel("Serves");
    public StatisticModel errors = new StatisticModel("Opp. errors");
//    public StatisticModel totalPoints = new StatisticModel("Total points");
    public StatisticModel receptionPercentage = new StatisticModel("Rec. %");
    public List<PlayerStatisticsModel> statsByPlayerHome = new ArrayList<>();
    public List<PlayerStatisticsModel> statsByPlayerGuest = new ArrayList<>();

    public void processPlayerStatistics() {
        int totalHomeReceptionWins = 0;
        int totalHomeReceptionTotal = 0;

        for (PlayerStatisticsModel playerStat : statsByPlayerHome) {
            attacks.addToHomeStat(playerStat.spikeWins);
            blocks.addToHomeStat(playerStat.blockWins);
            serves.addToHomeStat(playerStat.serveWins);
            totalHomeReceptionWins += playerStat.receptionWins;
            totalHomeReceptionTotal += playerStat.receptionsTotal;

            errors.addToGuestStat(playerStat.receptionsErrors);
            errors.addToGuestStat(playerStat.serveError);
        }

        int totalGuestReceptionWins = 0;
        int totalGuestReceptionTotal = 0;
        for (PlayerStatisticsModel playerStat : statsByPlayerGuest) {
            attacks.addToGuestStat(playerStat.spikeWins);
            blocks.addToGuestStat(playerStat.blockWins);
            serves.addToGuestStat(playerStat.serveWins);
            totalGuestReceptionWins += playerStat.receptionWins;
            totalGuestReceptionTotal += playerStat.receptionsTotal;

            errors.addToHomeStat(playerStat.receptionsErrors);
            errors.addToHomeStat(playerStat.serveError);
        }
        receptionPercentage.setHomeStat(Util.getRoundedValue((float) totalHomeReceptionWins / totalHomeReceptionTotal * 100));
        receptionPercentage.setGuestStat(Util.getRoundedValue((float) totalGuestReceptionWins / totalGuestReceptionTotal * 100));
        receptionPercentage.absoluteMaxValue = receptionPercentage.maxValue;

        int maxValue = computeMaxValue();
//        totalPoints.absoluteMaxValue = maxValue;
        attacks.absoluteMaxValue = maxValue;
        blocks.absoluteMaxValue = maxValue;
        serves.absoluteMaxValue = maxValue;
        errors.absoluteMaxValue = maxValue;

    }

    private int computeMaxValue() {
        int result = 0;
//        result = totalPoints.maxValue;
        result = Math.max(result, attacks.maxValue);
        result = Math.max(result, blocks.maxValue);
        result = Math.max(result, serves.maxValue);
        result = Math.max(result, errors.maxValue);
        return result;
    }

}
