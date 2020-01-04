package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends Activity {
    private static DownloadImageTask posterDownloader;
    private static CopyRatingsTask ratingsCopyTask;
    private static DataReader movies;
    public static int fileIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream linksStream = getResources().openRawResource(R.raw.links);
                ImdbState imdbState = new ImdbState(linksStream); // id to imdb

                posterDownloader = new DownloadImageTask(getApplicationContext());

                LongStringPair[] movieArr = new LongStringPair[10];
                List<LongStringPair> list = ImdbState.getRandomMovies(10);
                list.toArray(movieArr);

                posterDownloader.execute(movieArr);

                File directory = getApplicationContext().getDir("ratingsDir", Context.MODE_PRIVATE);

                File ratingsFile = new File(directory, "ratings.csv");
                File ratingsUpdateFile = new File(directory, "ratings." + fileIndex + ".csv");
                fileIndex++;
                try {
                    List<Path> toDelete = Files.list(Paths.get(directory.getPath())).filter(file -> file.toString().contains("rating")).collect(Collectors.toList());
                    for(Path path: toDelete) {
                        Files.deleteIfExists(path);
                        Log.i("info", "deleted file "+ path);
                    }
                } catch (Exception e) {
                    Log.e("error", "fuck " + e);
                }

                ratingsCopyTask = new CopyRatingsTask(ratingsFile, ratingsUpdateFile, getApplicationContext());
                ratingsCopyTask.execute();
                movies = new DataReader(getResources().openRawResource(R.raw.movies), ",");
            }
        }).start();
    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, RatingActivity.class);
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!DownloadImageTask.isTaskCompleted() || !movies.isCompleted()) {
                }
                startActivity(intent);
            }
        }).start();

    }

    public static String getTitleFor(Long id) {
        return movies.get(id);
    }


}
