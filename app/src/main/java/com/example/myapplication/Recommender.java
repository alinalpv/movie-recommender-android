package com.example.myapplication;

import android.util.Log;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recommender {
    private static FileDataModel model;

    // private static DataReader imdbIdForMovieId;
    private static UserSimilarity similarity;
    private static UserNeighborhood neighborhood;
    private static UserBasedRecommender userBasedRecommender;
    private static Recommender recommender;
    public static List<RecommendedItem> recommendedItems = new ArrayList<>();

    private Recommender() {
    }

    public static void init(File ratingsFile) {
        if (recommender == null) {
            recommender = new Recommender();
            try {

                model = new FileDataModel(ratingsFile, ",");
                //imdbIdForMovieId = new DataReader("datasets/links.csv", ",");
                similarity = new PearsonCorrelationSimilarity(model);
                neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                userBasedRecommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void refresh() {
        model.refresh(new ArrayList<>());
    }

    public static List<RecommendedItem> getRecommendations(Integer noItems, Long userId) {
        try {
            recommendedItems.addAll(userBasedRecommender.recommend(userId, noItems));
            return  recommendedItems;
        } catch (Exception e) {
            Log.e("Error","failed to get recommendations for user" + e.toString());
            return new ArrayList<>();
        }

    }

}
