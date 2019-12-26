package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {
    int index = 0;
    static List<String> images = new ArrayList<>();
    int rating;
    Pair<Long, String> pair;

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
        // Log.i("info", "width " + myBitmap.getWidth() + "height " + myBitmap.getHeight());

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
        index++;

        if (index == 10) {
            Intent intentRecommendationActivity = new Intent(this, ProgressBarActivity.class);
            startActivity(intentRecommendationActivity);
        } else {
            if (rating == 0) {
                rating = 1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*WriteToFileTask writeToFileTask = new WriteToFileTask("ratings.update.csv",
                            getApplicationContext());
                    writeToFileTask.execute("611," + pair.first + "," + rating);*/
                    intent.putExtra("index", index);
                   /* while(!writeToFileTask.isCompleted()){
                    }*/
                    Ratings.addRating("611," + pair.first + "," + rating);
                    startActivity(intent);
                }
            }).start();

        }
    }

    public void onSelectRating(View view) {

    }
}
