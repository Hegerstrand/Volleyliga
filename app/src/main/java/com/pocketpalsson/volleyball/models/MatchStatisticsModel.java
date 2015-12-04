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
        int totalHomeReceptionWins = 0;
        int totalHomeReceptionTotal = 0;

        for (PlayerStatisticsModel playerStat : statsByPlayerHome) {
            attacks.addToHomeStat(getRoundedValue(playerStat.spikeWinPercentage * playerStat.spikeTotal / 100));
            blocks.addToHomeStat(playerStat.blockWins);
            serves.addToHomeStat(playerStat.serveWins);
            totalHomeReceptionWins += getRoundedValue(playerStat.receptionsWinPercentage * playerStat.receptionsTotal / 100);
            totalHomeReceptionTotal += playerStat.receptionsTotal;

            errors.addToGuestStat(playerStat.receptionsErrors);
            errors.addToGuestStat(playerStat.serveError);
        }

        int totalGuestReceptionWins = 0;
        int totalGuestReceptionTotal = 0;
        for (PlayerStatisticsModel playerStat : statsByPlayerGuest) {
            attacks.addToGuestStat(getRoundedValue(playerStat.spikeWinPercentage * playerStat.spikeTotal / 100));
            blocks.addToGuestStat(playerStat.blockWins);
            serves.addToGuestStat(playerStat.serveWins);
            totalGuestReceptionWins += getRoundedValue(playerStat.receptionsWinPercentage * playerStat.receptionsTotal / 100);
            totalGuestReceptionTotal += playerStat.receptionsTotal;

            errors.addToHomeStat(playerStat.receptionsErrors);
            errors.addToHomeStat(playerStat.serveError);
        }
        receptionPercentage.setHomeStat(getRoundedValue((float) totalHomeReceptionWins / totalHomeReceptionTotal * 100));
        receptionPercentage.setGuestStat(getRoundedValue((float) totalGuestReceptionWins / totalGuestReceptionTotal * 100));
    }

    private int getRoundedValue(double value) {
        return (int) Math.round(value);
    }
}
