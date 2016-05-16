package com.sportsapp.volleyliga.repositories.xmlParsers;

import android.util.Xml;

import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.repositories.MatchCollection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MatchCollectionXmlPullParser {

    private List<MatchModel> matches = new ArrayList<>();

    public MatchCollection parse(String input) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        input = input.replaceAll("\n", "");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(input));
        parser.nextTag();
        readFeed(parser);
        MatchCollection result = new MatchCollection(matches);
        return result;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        MatchXmlPullParser matchParser = new MatchXmlPullParser();
        while (parser.getName() != null && !parser.getName().equalsIgnoreCase("MatchStatsResponse")) {
            parser.next();
        }
        while (parser.next() == XmlPullParser.START_TAG) {
            MatchModel match = matchParser.readFeed(parser);
            if (match != null) {
                matches.add(match);
            }
            while (parser.getName() == null || !parser.getName().equalsIgnoreCase("match")) {
                parser.next();
            }
        }
    }
}
