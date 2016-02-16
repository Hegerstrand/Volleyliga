package com.sportsapp.volleyliga.repositories;

import com.google.gson.Gson;
import com.sportsapp.volleyliga.models.MatchModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MatchCache {
    private File cacheDirectory;

    public MatchCache(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public void save(MatchModel data) {
        String wrapperJSONSerialized = new Gson().toJson(data);
        try {
            File file = new File(cacheDirectory, "Match_" + data.federationMatchNumber);

            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(wrapperJSONSerialized);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(String key) {
        File file = new File(cacheDirectory, key);
        file.delete();
    }

    public void deleteAll() {
        for (File file : cacheDirectory.listFiles()) {
            file.delete();
        }
    }

    public MatchModel retrieve(int matchNumber) {
        try {
            File file = new File(cacheDirectory, "Match_" + matchNumber);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));

            MatchModel data = new Gson().fromJson(bufferedReader, MatchModel.class);

            bufferedReader.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
