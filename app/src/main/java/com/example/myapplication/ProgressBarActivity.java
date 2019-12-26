package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProgressBarActivity extends Activity {
    private static WriteToFileTask writeToFileTask;
    private static DownloadImageTask posterDownloader;
    private static AsyncTask<LongStringPair, Void, String> plotDownloadTask;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        Log.i("info", "started activity");

        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView textView = findViewById(R.id.textView2);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setZ(100.0f);
        Intent intent = new Intent(this, RecommendationActivity.class);
        Intent ratingActivityIntent = new Intent(this, RatingActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                File directory = getApplicationContext().getDir("ratingsDir", Context.MODE_PRIVATE);

                String fileName = getIntent().getStringExtra("fileName") == null ? "ratings.1.csv" : getIntent().getStringExtra("fileName");
                File ratingsFile = new File(directory, fileName);
                Log.i("info", "using file name " + fileName );

                try {
                    if(!Files.exists(Paths.get(ratingsFile.getPath()))) {
                        Files.createFile(Paths.get(ratingsFile.getPath()));
                    }
                } catch (Exception e) {
                    Log.e("error", "failed to create file");
                }
                writeToFileTask = new WriteToFileTask(fileName,
                        getApplicationContext());

                writeToFileTask.execute(Ratings.getRatings());

                while (!(CopyRatingsTask.isCompleted() && writeToFileTask.isCompleted())) {
                }
                Log.i("info", ""+Integer.parseInt(String.valueOf(ratingsFile.length())));
                try {
                    Files.lines(Paths.get(ratingsFile.getPath())).forEach(line -> Log.i("info", "line " + line));
                } catch (Exception e) {
                    Log.e("error", "failed to read from file");
                }
                Recommender.refresh();

                Log.i("info", Recommender.getUserRatings(611L).length()+ " ratings by user");

                List<RecommendedItem> recommendations = new ArrayList<>();
                int noOfRecommendations = 6;

                while(recommendations.size() ==0 && noOfRecommendations >0) {
                    noOfRecommendations--;
                    recommendations = Recommender.getRecommendations(noOfRecommendations, 611L);
                }

                if(recommendations.isEmpty()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Could not get any recommendations. Preparing several more..");
                        }
                    });

                    ratingActivityIntent.putExtra("index", Ratings.getRatings().length);
                    ratingActivityIntent.putExtra("fileName", "ratings." + MainActivity.fileIndex + ".csv");
                    MainActivity.fileIndex++;
                    ratingActivityIntent.putExtra("maxMovies", Ratings.getRatings().length + 5);

                    DownloadImageTask newPosterDownloader = new DownloadImageTask(getApplicationContext());

                    LongStringPair[] movieArr = new LongStringPair[5];
                    List<LongStringPair> list = ImdbState.getRandomMovies(5);
                    list.toArray(movieArr);

                    newPosterDownloader.execute(movieArr);
                    PlotDownloader.downloadMovies(movieArr);

                    while(!DownloadImageTask.isTaskCompleted() || !PlotDownloader.isCompleted()) {
                    }
                    Log.i("info", "starting rating activiity");
                    startActivity(ratingActivityIntent);
                } else {
                    posterDownloader = new DownloadImageTask(getApplicationContext());
                    LongStringPair[] movieArr = new LongStringPair[noOfRecommendations];

                    for (int i = 0; i < recommendations.size() && i < noOfRecommendations; i++) {
                        Long itemId = recommendations.get(i).getItemID();
                        movieArr[i] = new LongStringPair(itemId, ImdbState.getImdbIdFor(itemId));
                    }

                    posterDownloader.execute(movieArr);
                    plotDownloadTask = new AsyncTask<LongStringPair, Void, String>() {
                        @Override
                        protected String doInBackground(LongStringPair... longStringPairs) {
                            PlotDownloader.downloadMovies(longStringPairs);
                            return "";
                        }
                    };
                    plotDownloadTask.execute(movieArr);
                    while (!(DownloadImageTask.isTaskCompleted() && PlotDownloader.isCompleted())) {
                        Log.i("info", "DownloadImageTask and or PlotDownloader not completed");
                    }

                    Log.i("info", "starting activity");

                    startActivity(intent);
                }
            }
        }).start();
    }
}
