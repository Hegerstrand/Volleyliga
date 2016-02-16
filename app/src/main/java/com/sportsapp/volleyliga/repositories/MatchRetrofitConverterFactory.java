package com.sportsapp.volleyliga.repositories;

import com.google.common.io.CharStreams;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.volley.match.MatchXmlPullParser;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Converter;

public class MatchRetrofitConverterFactory implements Converter.Factory {
    @Override
    public Converter<MatchModel> get(Type type) {
        return new MatchRetrofitConverter();
    }

    private class MatchRetrofitConverter implements Converter<MatchModel> {


        @Override
        public MatchModel fromBody(ResponseBody body) throws IOException {
            String response = CharStreams.toString(body.charStream());
            MatchXmlPullParser parser = new MatchXmlPullParser();
            return parser.parse(response);
        }

        @Override
        public RequestBody toBody(MatchModel value) {
            return null;
        }
    }
}
