/**
 * @author BA MALAZA
 * @version MiniProject
 */
package Graph;

/**
 * Represents a directed connection between two vertices in a graph.
 * Used for both pathfinding and similarity detection in image graphs.
 */
public class Edge {
    private final Vertex source;
    private final Vertex destination;
    private final double weight;

    /**
     * Creates a weighted edge between two vertices.
     * @param source Starting vertex
     * @param destination Ending vertex
     * @param weight Connection cost (e.g., pixel color difference or physical distance)
     */
    public Edge(Vertex source, Vertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    // --- Getters ---
    public Vertex getSource() { return source; }
    public Vertex getDestination() { return destination; }
    public double getWeight() { return weight; }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.2f)", source.getId(), destination.getId(), weight);
    }
}