package com.example.myapplication;

import java.util.Set;
import java.util.TreeSet;

public class Ratings {
    private static Set<String> ratingsUpdateSet= new TreeSet<>();
    private static Ratings ratings = new Ratings();
    private Ratings(){}

    public static void addRating(String rating) {
        ratingsUpdateSet.add(rating);
    }

    public static String[] getRatings() {
        String[] ratingsArray = new String[ratingsUpdateSet.size()];
        return ratingsUpdateSet.toArray(ratingsArray);
    }

}

