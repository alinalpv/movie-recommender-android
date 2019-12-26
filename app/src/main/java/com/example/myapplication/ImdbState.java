package com.example.myapplication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ImdbState {
    private static DataReader reader;  // map(id to imdbId)
    private static Set<Long> pickedIds = new HashSet<>();
    public static List<LongStringPair> currentMovies = new ArrayList<>();

    public ImdbState(InputStream linksStream) {
        reader = new DataReader(linksStream, ",");
    }

    public static Optional<DataReader> getImdbIds() {
        return Optional.ofNullable(reader);
    }

    public static LongStringPair getNext(){
        if(getImdbIds().isPresent()){
            LongStringPair id = reader.next();
            while(pickedIds.contains(id.first)) {
                id = reader.next();
            }
            pickedIds.add(id.first);
            return id;
        }
        return new LongStringPair(1L, "0114709");
    }

    public static List<LongStringPair> getRandomMovies(int nrOfMovies) {
        List<LongStringPair> randomMovies = new ArrayList<>();
        for(int i=0; i<nrOfMovies; i++) {
            randomMovies.add(getNext());
        }
        currentMovies.addAll(randomMovies);
        return randomMovies;
    }
    public static String getImdbIdFor(Long movieId) {
        return reader.get(movieId);
    }
}
