package com.pocketpalsson.volleyball.models;

import java.util.ArrayList;
import java.util.List;

public class MatchStatisticsModel {
    public StatisticModel attacks = new StatisticModel("Attacks");
    public StatisticModel blocks = new StatisticModel("Blocks");
    public StatisticModel serves = new StatisticModel("Serves");
    public StatisticModel errors = new StatisticModel("Opp. errors");
    public StatisticModel totalPoints = new StatisticModel("Total points");
    public StatisticModel receptionPercentage = new StatisticModel("Reception %");
    public List<PlayerStatisticsModel> statsByPlayerHome = new ArrayList<>();
    public List<PlayerStatisticsModel> statsByPlayerGuest = new ArrayList<>();

    public MatchStatisticsModel() {
    }

    public void processPlayerStatistics() {
        int totalHomeServeWins = 0;
        int totalHomeServeTotal = 0;

        for (PlayerStatisticsModel playerStat : statsByPlayerHome) {
            attacks.addToHomeStat(getRoundedValue(playerStat.spikeWinPercentage * playerStat.spikeTotal / 100));
            blocks.addToHomeStat(playerStat.blockWins);
            serves.addToHomeStat(playerStat.serveWins);
            totalHomeServeWins += getRoundedValue(playerStat.receptionsWinPercentage * playerStat.receptionsTotal / 100);
            totalHomeServeTotal += playerStat.receptionsTotal;

            errors.addToGuestStat(playerStat.receptionsErrors);
            errors.addToGuestStat(playerStat.serveError);
        }

        int totalGuestServeWins = 0;
        int totalGuestServeTotal = 0;
        for (PlayerStatisticsModel playerStat : statsByPlayerGuest) {
            attacks.addToGuestStat(getRoundedValue(playerStat.spikeWinPercentage * playerStat.spikeTotal / 100));
            blocks.addToGuestStat(playerStat.blockWins);
            serves.addToGuestStat(playerStat.serveWins);
            totalGuestServeWins += getRoundedValue(playerStat.receptionsWinPercentage * playerStat.receptionsTotal);
            totalGuestServeTotal += playerStat.receptionsTotal;

            errors.addToHomeStat(playerStat.receptionsErrors);
            errors.addToHomeStat(playerStat.serveError);
        }
        receptionPercentage.setHomeStat(getRoundedValue((float) totalHomeServeWins / totalHomeServeTotal));
        receptionPercentage.setGuestStat(getRoundedValue((float) totalGuestServeWins / totalGuestServeTotal));
    }

    private int getRoundedValue(double value) {
        return (int) Math.round(value);
    }
}
