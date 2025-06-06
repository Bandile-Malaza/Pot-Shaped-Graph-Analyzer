/**
 * @author BA MALAZA
 * @version MiniProject
 */
package Algorithms;

import Graph.Graph;
import Graph.Vertex;
import java.util.*;

/**
 * Compares two image graphs using multiple similarity metrics to determine
 * how closely they match. Uses a combination of feature similarity, structural
 * similarity, and k-Nearest Neighbor graph matching.
 * 
 * <p>Key comparison methods:
 * <ol>
 *   <li>Feature similarity - Compares visual characteristics of image regions</li>
 *   <li>Structural similarity - Compares graph connectivity patterns</li>
 *   <li>k-NN similarity - Finds most similar regions between images</li>
 * </ol>
 */
public class SimilarityDetector {

    /**
     * Container for similarity comparison results.
     * Contains individual metric scores and a combined weighted score.
     */
    public static class SimilarityResult {
        /** Combined similarity score (0-1 where 1 is identical) */
        public final double similarityScore;
        
        /** Score based on visual feature similarity (0-1) */
        public final double featureSimilarity;
        
        /** Score based on graph structure similarity (0-1) */
        public final double structuralSimilarity;
        
        /** Time taken for comparison in milliseconds */
        public final long timeMillis;

        /**
         * Constructor Creates a new similarity result container.
         * @param similarityScore Combined weighted score
         * @param featureSimilarity Visual feature match score
         * @param structuralSimilarity Graph structure match score
         * @param timeMillis Computation time
         */
        public SimilarityResult(double similarityScore, double featureSimilarity, double structuralSimilarity, long timeMillis) {
            this.similarityScore = similarityScore;
            this.featureSimilarity = featureSimilarity;
            this.structuralSimilarity = structuralSimilarity;
            this.timeMillis = timeMillis;
        }
    }

    /**
     * Calculates overall similarity between two graphs using multiple metrics.
     * @param graph1 First graph to compare
     * @param graph2 Second graph to compare
     * @param k Number of neighbors to consider in k-NN comparison
     * @return SimilarityResult containing all comparison metrics
     */
    public static SimilarityResult calculateSimilarity(Graph graph1, Graph graph2, int k) {
        long startTime = System.currentTimeMillis();
        
        // Compare different aspects of the graphs
        double featureSim = compareFeatures(graph1, graph2);
        double structuralSim = compareStructures(graph1, graph2);
        double knnSim = compareKNN(graph1, graph2, k);
        
        // Combine scores with weighted average
        double combinedScore = 0.4 * featureSim + 0.3 * structuralSim + 0.3 * knnSim;
        
        return new SimilarityResult(
            combinedScore,
            featureSim,
            structuralSim,
            System.currentTimeMillis() - startTime
        );
    }

    /**
     * Compares visual features (colors/textures) between graphs.
     * Uses Euclidean distance between feature vectors.
     * @param g1 First graph
     * @param g2 Second graph
     * @return Normalized similarity score (0-1)
     */
    private static double compareFeatures(Graph g1, Graph g2) {
        List<double[]> features1 = extractFeatures(g1);
        List<double[]> features2 = extractFeatures(g2);
        
        if (features1.isEmpty() || features2.isEmpty()) return 0.0;
        
        // Find minimal distances between all feature pairs
        double totalDistance = 0.0;
        for (double[] f1 : features1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (double[] f2 : features2) {
                minDist = Math.min(minDist, euclideanDistance(f1, f2));
            }
            totalDistance += minDist;
        }
        
        // Convert distance to similarity (higher = more similar)
        return 1.0 / (1.0 + (totalDistance / features1.size()));
    }

    /**
     * Compares graph structures by analyzing degree distributions.
     * Uses histogram intersection of node degrees.
     * @param g1 First graph
     * @param g2 Second graph
     * @return Structural similarity score (0-1)
     */
    private static double compareStructures(Graph g1, Graph g2) {
        Map<Integer, Integer> degDist1 = getDegreeDistribution(g1);
        Map<Integer, Integer> degDist2 = getDegreeDistribution(g2);
        
        // Calculate how much the degree distributions overlap
        Set<Integer> allDegrees = new HashSet<>();
        allDegrees.addAll(degDist1.keySet());
        allDegrees.addAll(degDist2.keySet());
        
        double intersection = 0.0;
        for (int degree : allDegrees) {
            intersection += Math.min(
                degDist1.getOrDefault(degree, 0),
                degDist2.getOrDefault(degree, 0)
            );
        }
        
        double union = g1.getVertices().size() + g2.getVertices().size();
        return union > 0 ? intersection / union : 0.0;
    }

    /**
     * Compares graphs using k-Nearest Neighbor matching.
     * For each node in graph1, finds k most similar nodes in graph2.
     * @param g1 First graph
     * @param g2 Second graph
     * @param k Number of neighbors to consider
     * @return k-NN similarity score (0-1)
     */
    private static double compareKNN(Graph g1, Graph g2, int k) {
        List<Vertex> nodes1 = g1.getVertices();
        List<Vertex> nodes2 = g2.getVertices();
        if (nodes1.isEmpty() || nodes2.isEmpty()) return 0.0;

        double totalSimilarity = 0.0;
        for (Vertex v1 : nodes1) {
            // Find k most similar nodes in graph2
            PriorityQueue<Vertex> neighbors = new PriorityQueue<>(
                Comparator.comparingDouble(v2 -> v1.featureDistance(v2))
            );
            neighbors.addAll(nodes2);
            
            // Calculate average similarity to top k matches
            double avgSim = 0.0;
            for (int i = 0; i < k && !neighbors.isEmpty(); i++) {
                avgSim += 1.0 / (1.0 + v1.featureDistance(neighbors.poll()));
            }
            totalSimilarity += avgSim / k;
        }
        return totalSimilarity / nodes1.size();
    }

    // ======================
    // Helper Methods
    // ======================
    
    /**
     * Extracts feature vectors from all graph vertices.
     * @param graph The graph to process
     * @return List of feature arrays
     */
    private static List<double[]> extractFeatures(Graph graph) {
        List<double[]> features = new ArrayList<>();
        for (Vertex v : graph.getVertices()) {
            if (v.getFeatures() != null) features.add(v.getFeatures());
        }
        return features;
    }

    /**
     * Calculates Euclidean distance between two feature vectors.
     * @param a First feature vector
     * @param b Second feature vector
     * @return Distance between features
     */
    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Calculates degree distribution (count of nodes with each degree).
     * @param graph The graph to analyze
     * @return Map of degree to count of nodes
     */
    private static Map<Integer, Integer> getDegreeDistribution(Graph graph) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Vertex v : graph.getVertices()) {
            int degree = v.getEdges().size();
            counts.put(degree, counts.getOrDefault(degree, 0) + 1);
        }
        return counts;
    }
}