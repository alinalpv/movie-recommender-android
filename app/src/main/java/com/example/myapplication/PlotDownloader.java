package com.example.myapplication;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PlotDownloader {
    public static Map<Long, Movie> movies = new HashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String url = "http://omdbapi.com/?apikey=ff64ebbe&plot=short&i=tt";
    private static PlotDownloader plotDownloader;
    private static boolean completed = false;
    private PlotDownloader(){}

    public static void downloadMovies(LongStringPair[] imdbIds){
        if(plotDownloader == null) { plotDownloader = new PlotDownloader();}

        for(LongStringPair imdbId: imdbIds) {
            try {
                InputStream in = new java.net.URL(url + imdbId.second).openStream();
                String jsonMovie = inputStreamToString(in);
                Log.i("info", jsonMovie);
                Movie movie = objectMapper.readValue(jsonMovie, Movie.class);
                movies.put(imdbId.first, movie);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
        completed = true;
    }

    private static String inputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            is.close();
        }
        catch (IOException e)
        { }
        return stringBuilder.toString();
    }
    public static boolean isCompleted(){ return completed;}
}
