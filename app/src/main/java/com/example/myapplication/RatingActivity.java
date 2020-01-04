package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.File;

public class RatingActivity extends Activity {
    int index = 0;
    int rating;
    private Pair<Long, String> pair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        ImageView imageview = findViewById(R.id.imageView);

        // Add image path from drawable folder.
        index = getIntent().getIntExtra("index", 0);

        pair = ImdbState.currentMovies.get(index);
        setTitle(MainActivity.getTitleFor(pair.first));

        File directory = getApplicationContext().getDir("imageDir", Context.MODE_PRIVATE);

        File myPath = new File(directory, Long.toString(pair.first));
        Bitmap myBitmap = BitmapFactory.decodeFile(myPath.getAbsolutePath());

        imageview.setImageBitmap(myBitmap);

        RatingBar ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float myRating, boolean fromUser) {
                rating = Math.round(myRating);
            }
        });
    }

    public void next(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, RatingActivity.class);
        Intent intentRecommendationActivity = new Intent(this, ProgressBarActivity.class);
        String fileName = getIntent().getStringExtra("fileName");
        Integer maxMovies = getIntent().getIntExtra("maxMovies", 10);

        index++;

        if (rating == 0) {
            rating = 1;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                intent.putExtra("index", index);
                Ratings.addRating("611," + pair.first + "," + rating);
                Log.i("info", "added rating " + "611," + pair.first + "," + rating);
                Log.i("info", "index  = " + index + " rated " + Ratings.getRatings().length);

                if (index == maxMovies) {
                    if(fileName != null) {
                        intentRecommendationActivity.putExtra("fileName", fileName);
                    }
                    startActivity(intentRecommendationActivity);
                } else {
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("maxMovies", maxMovies);

                    startActivity(intent);
                }
            }
        }).start();


    }

    public void onSelectRating(View view) {

    }
}
