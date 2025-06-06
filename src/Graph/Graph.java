/**
 * @author BA MALAZA
 * @version MiniProject
 */
package Graph;

import java.util.*;

/**
 * Base graph structure supporting both pathfinding and similarity tasks.
 * Can be configured as weighted/unweighted and directed/undirected.
 */
public class Graph {
    private final Map<String, Vertex> vertices = new HashMap<>();
    private final boolean isWeighted;
    private final boolean isDirected;

    public Graph(boolean isWeighted, boolean isDirected) {
        this.isWeighted = isWeighted;
        this.isDirected = isDirected;
    }

    /**
     * Adds a vertex representing an image region.
     * @return Existing vertex if ID already exists
     */
    public Vertex addVertex(String id, int x, int y) {
        return vertices.computeIfAbsent(id, k -> new Vertex(id, x, y));
    }

    /**
     * Connects two vertices with automatic weight handling.
     * @throws IllegalArgumentException if vertices don't exist
     */
    public void addEdge(String id1, String id2, double weight) {
        Vertex v1 = vertices.get(id1);
        Vertex v2 = vertices.get(id2);
        if (v1 == null || v2 == null) throw new IllegalArgumentException("Vertex not found");

        v1.addEdge(v2, isWeighted ? weight : 1.0);
        if (!isDirected) v2.addEdge(v1, isWeighted ? weight : 1.0);
    }

    // --- Graph Operations ---
    public void removeEdge(String id1, String id2) {
        Vertex v1 = vertices.get(id1);
        Vertex v2 = vertices.get(id2);
        if (v1 != null) v1.removeEdge(v2);
        if (!isDirected && v2 != null) v2.removeEdge(v1);
    }

    public void removeVertex(String id) {
        Vertex removed = vertices.remove(id);
        if (removed != null) {
            vertices.values().forEach(v -> v.removeEdge(removed));
        }
    }

    // --- Accessors ---
    public Vertex getVertex(String id) { return vertices.get(id); }
    public List<Vertex> getVertices() { return new ArrayList<>(vertices.values()); }
}