package com.sportsapp.volleyliga.repositories;

import com.google.common.io.CharStreams;
import com.sportsapp.volleyliga.repositories.xmlParsers.MatchCollectionXmlPullParser;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Converter;

public class MatchCollectionRetrofitConverterFactory implements Converter.Factory {
    @Override
    public Converter<MatchCollection> get(Type type) {
        if (!(type instanceof Class)) {
            return null;
        }
        Class<?> cls = (Class<?>) type;
        if (cls.equals(MatchCollection.class)) {
            return new MatchCollectionRetrofitConverter();
        }
        return null;
    }

    private class MatchCollectionRetrofitConverter implements Converter<MatchCollection> {


        @Override
        public MatchCollection fromBody(ResponseBody body) throws IOException {
            String response = CharStreams.toString(body.charStream());
            MatchCollectionXmlPullParser parser = new MatchCollectionXmlPullParser();
            try {
                MatchCollection result = parser.parse(response);
                return result;
            } catch (XmlPullParserException e) {
                throw new IllegalArgumentException("Test");
            }
        }

        @Override
        public RequestBody toBody(MatchCollection value) {
            return null;
        }
    }
}
