package com.volleyapp.volleyliga.models;

import com.volleyapp.volleyliga.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeamModel {
    public String name = "";
    public int logoRef = R.drawable.ic_cloud_off;
    public int id = 0;
    public String shortName = "", initials = "", homePage = "", facebookId = "", email = "", stadium = "", stadiumAddress = "", mapsUrl = "";
    //, phoneNumber = "";

//    private String LOGO_BASE_URL = "http://res.cloudinary.com/volleyapp/image/upload/w_200/";

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


    private int getLogoRef() {

        switch (name.toLowerCase()) {
            case "asv århus":
                return R.drawable.asvaarhus;
            case "boldklubben marienlyst":
                return R.drawable.marienlyst;
            case "gentofte volley":
                return R.drawable.gentofte;
            case "ikast kfum":
                return R.drawable.ikast;
            case "hvidovre vk":
                return R.drawable.hvidovre;
            case "ishøj volley":
                return R.drawable.ishoj;
            case "lyngby volley":
            case "lyngby-gladsaxe volley":
                return R.drawable.lyngby;
            case "middelfart vk":
                return R.drawable.middelfart;
            case "vk vestsjælland":
                return R.drawable.vestsjalland;
            case "amager vk":
                return R.drawable.amager;
            case "fortuna odense volley":
                return R.drawable.fortuna;
//            case "holte":
            case "holte if":
                return R.drawable.holte;
//            case "team køge volley":
            case "team køge":
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
