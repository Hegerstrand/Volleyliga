package com.volleyapp.volleyliga.models;

import com.volleyapp.volleyliga.utilities.Util;

import org.parceler.Parcel;

@Parcel
public class PlayerStatisticsModel {
    public int shirtNumber; //Player Shirt Number
    public String name; //Player Name
    public String surname; //Player Surname
    public int playedSet; //Set played by the Player
    public int serveTotal; //Total Number of Serve performed by the Player
    public int serveError; //Number of Serve Errors performed by the Player
    public int serveWins; //Number of Serve Points performed by the Player
    public int receptionsTotal; //Number of Total Receptions performed by the Player
    public int receptionsErrors; //Number of Reception Errors performed by the Player
    public double receptionsWinPercentage; // of Perfect Receptions performed by the Player
    public int spikeTotal; //Total Number of Spikes performed by the Player
    public double spikeWinPercentage; // % of Spike Points performed by the Player
    public int spikeWins;
    public int blockWins; //Number of Winning Blocks performed by the Player
    public int receptionWins;
    public int errorsTotal;

    public void postProcess() {
        spikeWins = Util.getRoundedValue(spikeWinPercentage * spikeTotal / 100);
        receptionWins = Util.getRoundedValue(receptionsWinPercentage * receptionsTotal / 100);
        errorsTotal = serveError + receptionsErrors;
    }

    public String getPlayerName() {
        return name + " " + surname;
    }
}
