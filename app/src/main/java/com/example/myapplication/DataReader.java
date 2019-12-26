package com.example.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class DataReader {
    private Map<Long, String> map = new HashMap<>();
    private List<Long> movieIds;
    private boolean completed = false;

    public DataReader(InputStream linksStream, String separator) {
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(linksStream)).lines()) {

            stream.map(line -> line.split(separator)).forEach(arr -> map.put(Long.parseLong(arr[0]), arr[1]));
            movieIds = new ArrayList<>(map.keySet());

            linksStream.close();
        } catch (Exception e) {
            Log.e("error", "failed to close links stream " + e.getMessage());
        }
        completed = true;
    }

    public String get(Long id) {
        return map.get(id);
    }

    /**
     * *
     *
     * @return a pair of id and imdbId
     */
    public LongStringPair next() {
        Long id = movieIds.get(ThreadLocalRandom.current().nextInt(movieIds.size()));
        return new LongStringPair(id, map.get(id));
    }

    public boolean isCompleted() {
        return completed;
    }
}
