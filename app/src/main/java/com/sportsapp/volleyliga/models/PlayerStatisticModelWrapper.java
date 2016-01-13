package com.sportsapp.volleyliga.models;

import rx.functions.Func1;

public class PlayerStatisticModelWrapper {
    public static final int STAT_ITEM = 1;
    public static final int HEADER = 2;
    public int value;

    public String title;
    public Type type;
    public boolean isNegativeStat = false;

    public String playerNumber;

    public PlayerStatisticModelWrapper(String title, boolean isNegativeStat) {
        this.title = title;
        this.isNegativeStat = isNegativeStat;
        type = Type.HEADER;
    }

    public PlayerStatisticModelWrapper(PlayerStatisticsModel stat, Func1<PlayerStatisticsModel, Integer> mappingFunction, boolean isNegativeStat) {
        this.isNegativeStat = isNegativeStat;
        value = mappingFunction.call(stat);
        title = stat.getPlayerName();
        playerNumber = "" + stat.shirtNumber;
        type = Type.STAT_ITEM;
    }

    public enum Type {
        HEADER,
        STAT_ITEM,
    }
}
