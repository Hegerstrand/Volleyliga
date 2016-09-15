package com.sportsapp.volleyliga.models;

import com.sportsapp.volleyliga.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeamModel {
    public String name = "";
    public int logoRef = R.drawable.ic_cloud_off;
    public int id = 0;
    public String shortName = "", initials = "", homePage = "", facebookId = "", email = "", stadium = "", stadiumAddress = "", mapsUrl = "";
    //, phoneNumber = "";
    public double lat = 0, lon = 0;
    public List<TeamPlayer> players = new ArrayList<>();

    public TeamModel(String name) {
        setName(name);
    }

    public TeamModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        logoRef = getLogoRef();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamModel teamModel = (TeamModel) o;

        return name.equalsIgnoreCase(teamModel.name);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    private int getLogoRef() {
        switch (name.toLowerCase()) {
            case "asv århus":
                return R.drawable.asvaarhus;
            case "boldklubben marienlyst":
                return R.drawable.marienlyst;
            case "gentofte volley":
                return R.drawable.gentofte;
            case "dhv odense":
                return R.drawable.dhv;
            case "hvidovre vk":
                return R.drawable.hvidovre;
            case "ishøj volley":
                return R.drawable.ishoj;
            case "lyngby volley":
            case "lyngby-gladsaxe volley":
                return R.drawable.lyngby;
            case "middelfart vk":
                return R.drawable.middelfart;
            case "randers":
                return R.drawable.randers;
            case "vestsjælland":
                return R.drawable.vestsjalland;
            case "amager vk":
                return R.drawable.amager;
            case "fortuna odense volley":
                return R.drawable.fortuna;
            case "holte if":
            case "holte":
                return R.drawable.holte;
            case "team køge":
            case "team køge volley":
                return R.drawable.koge;
            case "elite volley aarhus":
                return R.drawable.eva;
            case "brøndby vk":
                return R.drawable.brondby;
            default:
                return R.drawable.ic_error_outline_black_48dp;
        }
    }

    public static class TeamComparator implements Comparator<TeamModel> {
        public int compare(TeamModel entry1, TeamModel entry2) {
            return entry1.name.compareTo(entry2.name);
        }
    }
}
