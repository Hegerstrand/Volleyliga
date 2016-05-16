package com.sportsapp.volleyliga.repositories.xmlParsers;

import android.util.Xml;

import com.sportsapp.volleyliga.models.LeagueStandingModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LeagueStandingXmlPullParser {
    private List<LeagueStandingModel> standings = new ArrayList<>();

    public List<LeagueStandingModel> parse(String input) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            input = input.replaceAll("\n", "");
            input = input.replaceAll("\t", "");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(input));
            parser.nextTag();
            readFeed(parser);
            return standings;
        } catch (XmlPullParserException | IOException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.getName() == null || !parser.getName().equalsIgnoreCase("team")) {
            parser.next();
        }
        do {
            LeagueStandingModel standing = new LeagueStandingModel();
            standing.position = Integer.parseInt(parser.getAttributeValue(null, "pos"));
            while (parser.next() == XmlPullParser.START_TAG) {
                readFromTag(standing, parser);
            }
            standings.add(standing);
        } while (parser.next() == XmlPullParser.START_TAG);
    }

    private void readFromTag(LeagueStandingModel standing, XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        int currentTag = parser.next();
        switch (name) {
            case "Team_Name":
                standing.teamName = parser.getText();
                break;
            case "Points":
                standing.points = Integer.parseInt(parser.getText());
                break;
            case "Matches_Won":
                standing.matchesWon = Integer.parseInt(parser.getText());
                break;
            case "Matches_Lost":
                standing.matchesLost = Integer.parseInt(parser.getText());
                break;
            case "Points_Won":
                standing.pointsWon = Integer.parseInt(parser.getText());
                break;
            case "Points_Lost":
                standing.pointsLost = Integer.parseInt(parser.getText());
                break;
            case "Disqualifications":
                standing.disqualifications = Integer.parseInt(parser.getText());
                break;
            case "Penalties":
                standing.penalties = Integer.parseInt(parser.getText());
                break;
            case "Matches_30":
                standing.matches30 = Integer.parseInt(parser.getText());
                break;
            case "Matches_31":
                standing.matches31 = Integer.parseInt(parser.getText());
                break;
            case "Matches_32":
                standing.matches32 = Integer.parseInt(parser.getText());
                break;
            case "Matches_23":
                standing.matches23 = Integer.parseInt(parser.getText());
                break;
            case "Matches_13":
                standing.matches13 = Integer.parseInt(parser.getText());
                break;
            case "Matches_03":
                standing.matches03 = Integer.parseInt(parser.getText());
                break;
            case "Set_Ratio":
                standing.setRatio = Integer.parseInt(parser.getText());
                break;
            case "Points_Ratio":
                standing.pointsRatio = Integer.parseInt(parser.getText());
                break;
        }
        if (currentTag == XmlPullParser.TEXT) {
            currentTag = parser.next();
        }
    }
}
