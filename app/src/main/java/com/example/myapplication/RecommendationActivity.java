package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.io.File;

public class RecommendationActivity extends AppCompatActivity {
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info", "created");
        setContentView(R.layout.activity_recommendation);
        ImageView imageview = findViewById(R.id.imageView2);

        index = getIntent().getIntExtra("index", 0);

        RecommendedItem item = Recommender.recommendedItems.get(index);
        setTitle(MainActivity.getTitleFor(item.getItemID()));
        File directory = getApplicationContext().getDir("imageDir", Context.MODE_PRIVATE);
        TextView textView = findViewById(R.id.description);
        textView.setMovementMethod(new ScrollingMovementMethod());

        Movie movie = PlotDownloader.movies.get(item.getItemID());
        if(movie != null) {
            textView.setText(movie.getPlot());
            Log.i("info", movie.toString());
        } else {
            Log.e("error", "movie not donwloaded");
        }

        File myPath = new File(directory, Long.toString(item.getItemID()));
        Bitmap myBitmap = BitmapFactory.decodeFile(myPath.getAbsolutePath());

        imageview.setImageBitmap(myBitmap);
    }

    public void next(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, RecommendationActivity.class);
        index++;
        if (index < 5) {
            intent.putExtra(  "index", index);
            startActivity(intent);
        }
    }

}
