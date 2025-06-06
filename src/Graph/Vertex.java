package Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the graph, corresponding to an image region.
 * Stores position, visual features, and connections to other regions.
 */
public class Vertex {
    private final String id;          // Format: "x,y" coordinates
    private final int x, y;           // Pixel position in original image
    private final List<Edge> edges;    // Outgoing connections
    private double[] features;        // Visual characteristics (color, texture)
    private boolean isObstacle;       // For pathfinding (true = impassable)

    public Vertex(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    /**
     * Connects this vertex to another with given traversal cost.
     */
    public void addEdge(Vertex destination, double weight) {
        edges.add(new Edge(this, destination, weight));
    }

    /**
     * Removes all connections to a specific vertex.
     */
    public void removeEdge(Vertex destination) {
        edges.removeIf(e -> e.getDestination().equals(destination));
    }

    // --- Key Calculations ---
    
    /**
     * Physical distance to another vertex (Euclidean).
     */
    public double distanceTo(Vertex other) {
        return Math.hypot(x - other.x, y - other.y);
    }

    /**
     * Visual similarity to another vertex (0 = identical).
     */
    public double featureDistance(Vertex other) {
        if (features == null || other.features == null) return Double.POSITIVE_INFINITY;
        
        double sum = 0;
        for (int i = 0; i < features.length; i++) {
            sum += Math.pow(features[i] - other.features[i], 2);
        }
        return Math.sqrt(sum);
    }

    // --- Getters/Setters ---
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public List<Edge> getEdges() { return edges; }
    public double[] getFeatures() { return features; }
    public void setFeatures(double[] features) { this.features = features; }
    public boolean isObstacle() { return isObstacle; }
    public void setObstacle(boolean isObstacle) { this.isObstacle = isObstacle; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vertex && this.id.equals(((Vertex) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}