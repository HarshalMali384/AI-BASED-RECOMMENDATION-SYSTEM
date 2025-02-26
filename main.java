import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

public class UserBasedRecommender {

    public static void main(String[] args) {
        try {
            // Load data from CSV file
            DataModel dataModel = new FileDataModel(new File("dataset.csv"));

            // Define user similarity algorithm
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

            // Define user neighborhood
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);

            // Build the recommender
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            // Generate recommendations for a specific user
            int userId = 1;
            int numberOfRecommendations = 2;
            List recommendations = recommender.recommend(userId, numberOfRecommendations);

            // Output recommendations
            System.out.println("Recommendations for User ID " + userId + ":");
            for (var recommendation : recommendations) {
                System.out.println("Item ID: " + recommendation.getItemID() + ", Value: " + recommendation.getValue());
            }

            // Evaluate the recommender
            RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
            double evaluationScore = evaluator.evaluate(new RecommenderBuilder() {
                @Override
                public Recommender buildRecommender(DataModel model) throws TasteException {
                    return new GenericUserBasedRecommender(model, neighborhood, similarity);
                }
            }, null, dataModel, 0.7, 1.0);

            System.out.println("Evaluation Score: " + evaluationScore);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
