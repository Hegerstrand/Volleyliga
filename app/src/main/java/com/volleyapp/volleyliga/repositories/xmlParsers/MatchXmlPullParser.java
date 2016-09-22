package com.volleyapp.volleyliga.repositories.xmlParsers;

import android.util.Log;
import android.util.Xml;

import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.models.PlayerStatisticsModel;
import com.volleyapp.volleyliga.models.SetInfoModel;
import com.volleyapp.volleyliga.models.TeamModel;
import com.volleyapp.volleyliga.repositories.TeamRepository;
import com.volleyapp.volleyliga.utilities.Util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchXmlPullParser {

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private MatchModel match;
    private RawSetInfo rawSetInfo;

    public MatchModel parse(String input) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        input = input.replaceAll("\n", "");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(input));
        parser.nextTag();
        readFeed(parser);
        if (match != null) {
            match.xml = input;
        }
        return match;
    }

    public MatchModel readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        match = new MatchModel();
        rawSetInfo = new RawSetInfo();
        while (!parser.getName().equalsIgnoreCase("MatchStatsResult")) {
            parser.next();
        }
        while (parser.next() == XmlPullParser.START_TAG) {
            readFromTag(parser);
        }
        postProcessMatch();
        return match;
    }

    private void postProcessMatch() {
        TeamModel teamToUse;
        if(match.teamHome.id > 0){
            teamToUse = match.teamHome;
        } else if(match.teamGuest.id > 0){
            teamToUse = match.teamGuest;
        } else {
            teamToUse = TeamRepository.instance.getTeam(1);
        }
        match.league = TeamRepository.instance.getLeagueForTeam(teamToUse);
        processSetInfo();
        match.statistics.processPlayerStatistics();
    }

    private void processSetInfo() {
        if (rawSetInfo.set1Home > 0 && rawSetInfo.set1Guest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.set1Home, rawSetInfo.set1Guest, rawSetInfo.set1Time), 1);
        }
        if (rawSetInfo.set2Home > 0 && rawSetInfo.set2Guest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.set2Home, rawSetInfo.set2Guest, rawSetInfo.set2Time), 2);
        }
        if (rawSetInfo.set3Home > 0 && rawSetInfo.set3Guest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.set3Home, rawSetInfo.set3Guest, rawSetInfo.set3Time), 3);
        }
        if (rawSetInfo.set4Home > 0 && rawSetInfo.set4Guest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.set4Home, rawSetInfo.set4Guest, rawSetInfo.set4Time), 4);
        }
        if (rawSetInfo.set5Home > 0 && rawSetInfo.set5Guest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.set5Home, rawSetInfo.set5Guest, rawSetInfo.set5Time), 5);
        }
        if (rawSetInfo.goldenSetHome > 0 && rawSetInfo.goldenSetGuest > 0) {
            match.setSetInfo(new SetInfoModel(rawSetInfo.goldenSetHome, rawSetInfo.goldenSetGuest, rawSetInfo.goldenSetTime), 6);
        }
//        match.computeTotalPoints();
    }

    private void readFromTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        switch (name) {
            case "Players_HomeTeam":
                match.statistics.statsByPlayerHome = parsePlayerStatistics(parser);
                return;
            case "Players_GuestTeam":
                match.statistics.statsByPlayerGuest = parsePlayerStatistics(parser);
                return;
//            case "Referee1Name":
//                String refeere1 = parser.getText();
//                if(refeere1 == null){
//                    //Exit right away since otherwise we go out of sync
//                    match.referee1Name = "";
//                    parser.next();
//                    return;
//                }
//                break;
//            case "Referee2Name":
//                String refeere2 = parser.getText();
//                if(refeere2 == null){
//                    //Exit right away since otherwise we go out of sync
//                    match.referee2Name = "";
//                    parser.next();
//                    return;
//                }
//                break;
        }
        parser.next();
        switch (name) {
            case "ChampionshipMatchID":
                match.championshipMatchID = Integer.parseInt(parser.getText());
                break;
            case "FederationMatchNumber":
                match.federationMatchNumber = Integer.parseInt(parser.getText());
                if (match.federationMatchNumber == 113591) {
                    String s = "";
                }
                break;
            case "ChampionshipName":
                match.championshipName = parser.getText();
                break;
            case "SeasonDescription":
                match.seasonDescription = parser.getText();
                break;
            case "ChampionshipLegNumber":
                match.championshipLegNumber = Integer.parseInt(parser.getText());
                break;
            case "ChampionshipLegName":
                match.championshipLegName = parser.getText();
                break;
            case "LegType":
                match.legType = parser.getText();
                break;
            case "MatchDateTime":
                match.matchDateTime = getDateFromText(parser.getText());
                break;
            case "HomeTeam":
                String homeTeamText = parser.getText();
//                String homeTeamText = "Team Køge";
                match.teamHome = TeamRepository.instance.getTeam(homeTeamText);
                if (match.teamHome == null) {
                    match.teamHome = new TeamModel(homeTeamText);
                    match.teamHome.shortName = homeTeamText;
                    match.teamHome.initials = homeTeamText;
                }
                break;
            case "GuestTeam":
                String guestTeamText = parser.getText();
//                String guestTeamText = "Team Køge";
                match.teamGuest = TeamRepository.instance.getTeam(guestTeamText);
                if (match.teamGuest == null) {
                    match.teamGuest = new TeamModel(guestTeamText);
                    match.teamHome.shortName = guestTeamText;
                    match.teamHome.initials = guestTeamText;
                }
                break;
            case "WonSetHome":
                match.setsWonByHome = Integer.parseInt(parser.getText());
                break;
            case "WonSetGuest":
                match.setsWonByGuest = Integer.parseInt(parser.getText());
                break;
            case "GoldenSetPlayed":
                match.goldenSetPlayed = Integer.parseInt(parser.getText()) == 1;
                break;
            case "Set1Home":
                rawSetInfo.set1Home = Integer.parseInt(parser.getText());
                break;
            case "Set1Guest":
                rawSetInfo.set1Guest = Integer.parseInt(parser.getText());
                break;
            case "Set2Home":
                rawSetInfo.set2Home = Integer.parseInt(parser.getText());
                break;
            case "Set2Guest":
                rawSetInfo.set2Guest = Integer.parseInt(parser.getText());
                break;
            case "Set3Home":
                rawSetInfo.set3Home = Integer.parseInt(parser.getText());
                break;
            case "Set3Guest":
                rawSetInfo.set3Guest = Integer.parseInt(parser.getText());
                break;
            case "Set4Home":
                rawSetInfo.set4Home = Integer.parseInt(parser.getText());
                break;
            case "Set4Guest":
                rawSetInfo.set4Guest = Integer.parseInt(parser.getText());
                break;
            case "Set5Home":
                rawSetInfo.set5Home = Integer.parseInt(parser.getText());
                break;
            case "Set5Guest":
                rawSetInfo.set5Guest = Integer.parseInt(parser.getText());
                break;
            case "GoldenSetHome":
                rawSetInfo.goldenSetHome = Integer.parseInt(parser.getText());
                break;
            case "GoldenSetGuest":
                rawSetInfo.goldenSetGuest = Integer.parseInt(parser.getText());
                break;
            case "TimeSet1":
                rawSetInfo.set1Time = Integer.parseInt(parser.getText());
                break;
            case "TimeSet2":
                rawSetInfo.set2Time = Integer.parseInt(parser.getText());
                break;
            case "TimeSet3":
                rawSetInfo.set3Time = Integer.parseInt(parser.getText());
                break;
            case "TimeSet4":
                rawSetInfo.set4Time = Integer.parseInt(parser.getText());
                break;
            case "TimeSet5":
                rawSetInfo.set5Time = Integer.parseInt(parser.getText());
                break;
            case "TimeGoldenSet":
                rawSetInfo.goldenSetTime = Integer.parseInt(parser.getText());
                break;
            case "Referee1Name":
                String referee1 = parser.getText();
                if (referee1 == null) {
                    //Exit right away since otherwise we go out of sync
                    match.referee1Name = "";
                    return;
                } else {
                    match.referee1Name = parseRefereeName(referee1);
                }
                break;
            case "Referee2Name":
                String referee2 = parser.getText();
                if (referee2 == null) {
                    //Exit right away since otherwise we go out of sync
                    match.referee2Name = "";
                    return;
                } else {
                    match.referee2Name = parseRefereeName(referee2);
                }
                break;
            case "Stadium":
                match.stadium = parser.getText();
                break;
            case "StadiumCity":
                match.stadiumCity = parser.getText();
                break;
            case "Spectators":
                match.spectators = Integer.parseInt(parser.getText());
                break;
            case "Receipts":
                match.receipts = Double.parseDouble(parser.getText());
                break;
            default:
                if (parser.getText() == null) {
                    //Exit right away since otherwise we go out of sync
                    return;
                }
        }
        parser.next();
    }

    private String parseRefereeName(String input) {
        if (input == null || input.equalsIgnoreCase("")) {
            return "";
        }
        List<String> split = Arrays.asList(input.split(" "));
        if (split.size() == 0) {
            return input;
        }
        String result = "";
        for (String entry : split.subList(1, split.size())) {
            result += entry + " ";
        }
        result += split.get(0);
        return result;
    }

    private List<PlayerStatisticsModel> parsePlayerStatistics(XmlPullParser parser) {
        List<PlayerStatisticsModel> result = new ArrayList<>();
        try {
            while (parser.next() == XmlPullParser.START_TAG) {
                PlayerStatisticsModel parseResult = parseSinglePlayerStatistic(parser);
                if (parseResult != null) {
                    result.add(parseResult);
                }
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private PlayerStatisticsModel parseSinglePlayerStatistic(XmlPullParser parser) {
        if (parser.getName().equalsIgnoreCase("WS_PlayerMatch")) {
            try {
                PlayerStatisticsModel result = new PlayerStatisticsModel();
                while (parser.next() != XmlPullParser.END_TAG) {
                    readPlayerStatisticTag(result, parser);
                }
                result.postProcess();
                return result;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void readPlayerStatisticTag(PlayerStatisticsModel statistic, XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        if (name == null) {
            return;
        }
        parser.next();
        switch (name) {
            case "Number":
                statistic.shirtNumber = Integer.parseInt(parser.getText());
                break;
            case "Surname":
                statistic.surname = correctSurnameCase(parser.getText());
                break;
            case "Name":
                statistic.name = parser.getText();
                break;
            case "PlayedSet":
                statistic.playedSet = Integer.parseInt(parser.getText());
                break;
            case "ServeTot":
                statistic.serveTotal = Integer.parseInt(parser.getText());
                break;
            case "ServeErr":
                statistic.serveError = Integer.parseInt(parser.getText());
                break;
            case "ServeWin":
                statistic.serveWins = Integer.parseInt(parser.getText());
                break;
            case "RecTot":
                statistic.receptionsTotal = Integer.parseInt(parser.getText());
                break;
            case "RecErr":
                statistic.receptionsErrors = Integer.parseInt(parser.getText());
                break;
            case "RecWinPerc":
                statistic.receptionsWinPercentage = Double.parseDouble(parser.getText());
                break;
            case "SpikeTot":
                statistic.spikeTotal = Integer.parseInt(parser.getText());
                break;
            case "SpikeWinPerc":
                statistic.spikeWinPercentage = Double.parseDouble(parser.getText());
                break;
            case "BlockWin":
                statistic.blockWins = Integer.parseInt(parser.getText());
                break;
        }
        parser.next();
    }

    private String correctSurnameCase(String text) {
        if (Util.isNullOrEmpty(text)) {
            return "";
        }
        return text.substring(0, 1) + text.substring(1).toLowerCase();
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Date getDateFromText(String dateText) {
        try {
            Date arrivalTime = DATE_FORMAT.parse(dateText);
//            Calendar result = Calendar.getInstance();
//            result.setTime(arrivalTime);
//            Log.d("TestVolley", dateText);
            if (arrivalTime.getTime() < 1420070400000L) {
                Log.d("TestVolley", dateText);
            }
            return arrivalTime;
        } catch (ParseException e) {
            return null;
        }
    }

    private class RawSetInfo {
        public int set1Home;
        public int set1Guest;
        public int set2Home;
        public int set2Guest;
        public int set3Home;
        public int set3Guest;
        public int set4Home;
        public int set4Guest;
        public int set5Home;
        public int set5Guest;
        public int goldenSetHome;
        public int goldenSetGuest;
        public int set1Time;
        public int set2Time;
        public int set3Time;
        public int set4Time;
        public int set5Time;
        public int goldenSetTime;
    }
}
