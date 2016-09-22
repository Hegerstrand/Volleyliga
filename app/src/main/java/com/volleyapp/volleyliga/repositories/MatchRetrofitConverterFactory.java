package com.volleyapp.volleyliga.repositories;

import com.google.common.io.CharStreams;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.repositories.xmlParsers.MatchXmlPullParser;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Converter;

public class MatchRetrofitConverterFactory implements Converter.Factory {
    @Override
    public Converter<MatchModel> get(Type type) {
        if (!(type instanceof Class)) {
            return null;
        }
        Class<?> cls = (Class<?>) type;
        if (cls.equals(MatchModel.class)) {
            return new MatchRetrofitConverter();
        }
        return null;
    }

    private class MatchRetrofitConverter implements Converter<MatchModel> {


        @Override
        public MatchModel fromBody(ResponseBody body) throws IOException {
            String response = CharStreams.toString(body.charStream());
            MatchXmlPullParser parser = new MatchXmlPullParser();
            try {
                return parser.parse(response);
            } catch (XmlPullParserException e) {
                throw new IllegalArgumentException("Test");
            }
        }

        @Override
        public RequestBody toBody(MatchModel value) {
            return null;
        }
    }
}
