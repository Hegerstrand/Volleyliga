package com.pocketpalsson.volleyball.models;

public class StatisticModel {
    public int homeStat;
    public int guestStat;
    public int maxValue;
    public String name;
    public boolean isHomeValueMax = true;

    public StatisticModel(String name) {
        this.name = name;
    }

    public int getHomeStat() {
        return homeStat;
    }

    public void setHomeStat(int homeStat) {
        this.homeStat = homeStat;
        updateMax();
    }

    public int getGuestStat() {
        return guestStat;
    }

    public void setGuestStat(int guestStat) {
        this.guestStat = guestStat;
        updateMax();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addToHomeStat(int value) {
        homeStat += value;
        updateMax();
    }

    public void addToGuestStat(int value) {
        guestStat += value;
        updateMax();
    }

    private void updateMax() {
        maxValue = Math.max(homeStat, guestStat);
        isHomeValueMax = homeStat == maxValue;
    }
}
