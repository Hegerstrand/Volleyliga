package com.sportsapp.volleyliga.utilities.volley.match;

import android.util.Xml;

import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.models.TeamPlayer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TeamXmlPullParser {
    private List<TeamModel> teams;

    public List<TeamModel> parse(String input) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            input = input.replaceAll("\n", "");
            input = input.replaceAll("\t", "");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(input));
            parser.nextTag();
            readFeed(parser);
            return teams;
        } catch (XmlPullParserException | IOException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        teams = new ArrayList<>();
        while (parser.getName() == null || !parser.getName().equalsIgnoreCase("team")) {
            parser.next();
        }
        do {
            TeamModel team = new TeamModel();
            while (parser.next() == XmlPullParser.START_TAG) {
                readFromTag(team, parser);
            }
            teams.add(team);
        } while (parser.next() == XmlPullParser.START_TAG);
    }

    private void readFromTag(TeamModel team, XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        int currentTag = parser.next();
        switch (name) {
            case "teamID":
                team.id = Integer.parseInt(parser.getText());
                break;
            case "teamName":
                team.setName(parser.getText());
                break;
            case "shortTeamName":
                team.shortName = parser.getText();
                break;
            case "teamInitials":
                team.initials = parser.getText();
                break;
            case "teamHomePage":
                team.homePage = parser.getText();
                break;
            case "teamFb":
                team.facebookId = parser.getText();
                break;
            case "teamMail":
                team.email = parser.getText();
                break;
            case "teamPhone":
                team.phoneNumber = parser.getText();
                break;
            case "teamGym":
                team.stadium = parser.getText();
                break;
            case "teamGymAddress":
                team.stadiumAddress = parser.getText();
                break;
            case "teamMaps":
                team.mapsUrl = parser.getText();
                break;
            case "teamLat":
                team.lat = Double.parseDouble(parser.getText());
                break;
            case "teamLon":
                team.lon = Double.parseDouble(parser.getText());
                break;
            case "players":
                team.players = parsePlayers(parser);
        }
        if (currentTag == XmlPullParser.TEXT) {
            currentTag = parser.next();
        }
    }

    private List<TeamPlayer> parsePlayers(XmlPullParser parser) {
        List<TeamPlayer> result = new ArrayList<>();
        try {
            do {
                TeamPlayer parseResult = parseSinglePlayer(parser);
                if (parseResult != null) {
                    result.add(parseResult);
                }
            } while (parser.next() == XmlPullParser.START_TAG);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private TeamPlayer parseSinglePlayer(XmlPullParser parser) {
        if (parser.getName().equalsIgnoreCase("player")) {
            try {
                TeamPlayer result = new TeamPlayer();
                while (parser.next() != XmlPullParser.END_TAG) {
                    readPlayerTag(result, parser);
                }
                return result;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void readPlayerTag(TeamPlayer player, XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        if (name == null) {
            return;
        }
        parser.next();
        switch (name) {
            case "id":
                player.id = parser.getText();
                break;
            case "number":
                player.number = Integer.parseInt(parser.getText());
                break;
            case "name":
                player.name = parser.getText();
                break;
        }
        parser.next();
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
}
