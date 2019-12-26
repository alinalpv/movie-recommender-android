package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProgressBarActivity extends AppCompatActivity{
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        Log.i("info", "started activity");

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setZ(100.0f);
        Intent intent = new Intent(this, RecommendationActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                WriteToFileTask writeToFileTask = new WriteToFileTask("ratings.update.csv",
                        getApplicationContext());

                writeToFileTask.execute(Ratings.getRatings());
                while (!CopyRatingsTask.isCompleted() || !writeToFileTask.isCompleted()) {
                    Log.w("Warn", "Ratings not downloaded, trying again");
                }

                Recommender.refresh();
                List<RecommendedItem> recommendations = new ArrayList<>();

                while(recommendations.size() ==0) {
                    recommendations = Recommender.getRecommendations(5, 611L);
                    Log.w("Warn", "Could not get recommendations, trying again");
                }

                DownloadImageTask posterDownloader = new DownloadImageTask(getApplicationContext());
                LongStringPair[] movieArr = new LongStringPair[5];

                for (int i = 0; i < recommendations.size(); i++) {
                    Long itemId = recommendations.get(i).getItemID();
                    movieArr[i] = new LongStringPair(itemId, ImdbState.getImdbIdFor(itemId));
                }

                posterDownloader.execute(movieArr);
                AsyncTask<LongStringPair, Void, String> plotDownloadTask = new AsyncTask<LongStringPair, Void, String>() {
                    @Override
                    protected String doInBackground(LongStringPair... longStringPairs) {
                        PlotDownloader.downloadMovies(longStringPairs);
                        return "";
                    }
                };

                plotDownloadTask.execute(movieArr);
                while (!DownloadImageTask.isTaskCompleted() || !PlotDownloader.isCompleted()) {
                    Log.i("info", "DownloadImageTask and or PlotDownloader not completed");
                }

                Log.i("info", "starting activity");

                startActivity(intent);
            }
        }).start();
    }
}
