package com.pocketpalsson.volleyball.utilities.volley.match;

import android.util.Xml;

import com.pocketpalsson.volleyball.models.TeamModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class TeamXmlPullParser {
    private TeamModel team;

    public TeamModel parse(String input) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            input = input.replaceAll("\n", "");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(input));
            parser.nextTag();
            readFeed(parser);
            return team;
        } catch (XmlPullParserException | IOException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    private TeamModel readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        team = new TeamModel();
        while (!parser.getName().equalsIgnoreCase("team")) {
            parser.next();
        }
        while (parser.next() == XmlPullParser.START_TAG) {
            readFromTag(parser);
        }
        return team;
    }

    private void readFromTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        parser.next();
        switch (name) {
            case "teamID":
                team.Id = Integer.parseInt(parser.getText());
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
