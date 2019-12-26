package com.example.myapplication;

import android.util.Pair;

public class LongStringPair extends Pair<Long, String> {
    public LongStringPair(Long id, String imdbId){
        super(id, imdbId);
    }
    public LongStringPair(Pair<Long, String> pair){
        super(pair.first, pair.second);
    }
}
