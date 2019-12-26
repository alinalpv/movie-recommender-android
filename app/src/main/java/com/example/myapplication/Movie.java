package com.example.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

    private String plot;
    private String imdbID;
    private String imdbRating;

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }
    public String getPlot() {
        return plot;
    }

    @JsonProperty("Plot")
    public void setPlot(String plot) {
        this.plot = plot;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "plot='" + plot + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                '}';
    }
}
