package com.pocketpalsson.volleyball.models;

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
    public int blockWins; //Number of Winning Blocks performed by the Player
}
