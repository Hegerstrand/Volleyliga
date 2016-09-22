package com.volleyapp.volleyliga.repositories;

import com.google.common.io.CharStreams;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Converter;

public class StringRetrofitConverterFactory implements Converter.Factory {
    @Override
    public Converter<String> get(Type type) {
        return new StringRetrofitConverter();
    }

    private class StringRetrofitConverter implements Converter<String> {


        @Override
        public String fromBody(ResponseBody body) throws IOException {
            return CharStreams.toString(body.charStream());
        }

        @Override
        public RequestBody toBody(String value) {
            return null;
        }
    }
}
