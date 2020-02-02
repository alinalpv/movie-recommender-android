package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

public class SummaryActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        setTitle("Your recommendations");

        recyclerView = findViewById(R.id.recyclerView);

        // Drawable verticalDivider = getDrawable(R.drawable.vertical_divider);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        // verticalDecoration.setDrawable(verticalDivider);

        recyclerView.addItemDecoration(verticalDecoration);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<Long> recommendedItemList = Recommender.recommendedItems.stream().map(recommendedItem -> recommendedItem.getItemID()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
        String[] recommended = new String[recommendedItemList.size()];
        String[] imdbRatings = new String[recommendedItemList.size()];
        for (int i = 0; i < recommendedItemList.size(); i++) {
            recommended[i] = MainActivity.getTitleFor(recommendedItemList.get(i));
            Movie movie = PlotDownloader.movies.get(recommendedItemList.get(i));

            imdbRatings[i] = movie.getImdbRating() + "/10";
        }
        mAdapter = new MyAdapter(recommended, imdbRatings);
        recyclerView.setAdapter(mAdapter);

    }

    public void exit(View view) {
        finishAffinity();
        System.exit(0);
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] mDataset;
        private String[] rDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;
            public TextView ratingTextView;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.myTextView);
                ratingTextView = v.findViewById(R.id.imdbRating2);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset, String[] ratingDataset) {
            mDataset = myDataset;
            rDataset = ratingDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_row, parent, false);
            //
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.setText(mDataset[position]);
            holder.ratingTextView.setText(rDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
