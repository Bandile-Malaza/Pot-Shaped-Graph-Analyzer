/**
 * @author BA MALAZA
 * @version MiniProject
 */
package Algorithms;

import Graph.Edge;
import Graph.Vertex;
import java.util.*;

/**
 * Implements Dijkstra's and A* algorithms for shortest pathfinding in a graph.
 * Handles obstacles and tracks search metrics (path cost, time, explored nodes).
 */
public class Pathfinder {

    /**
     * Result of a pathfinding operation, including:
     * - path: Shortest path (empty if none exists)
     * - explored: Nodes visited during search
     * - totalCost: Sum of edge weights in the path
     * - timeMillis: Execution time in milliseconds
     */
    public static class PathResult {
        public final List<Vertex> path;
        public final List<Vertex> explored;
        public final double totalCost;
        public final long timeMillis;

        public PathResult(List<Vertex> path, List<Vertex> explored, double totalCost, long timeMillis) {
            this.path = path;
            this.explored = explored;
            this.totalCost = totalCost;
            this.timeMillis = timeMillis;
        }
    }

    /**
     * Dijkstra's algorithm for shortest path.
     * @param start Starting vertex
     * @param end Target vertex
     * @return PathResult with path and search metrics
     */
    public static PathResult dijkstra(Vertex start, Vertex end) {
        long startTime = System.currentTimeMillis();
        
        // Initialize data structures
        Map<Vertex, Double> distances = new HashMap<>();  // Stores shortest known distances
        Map<Vertex, Vertex> predecessors = new HashMap<>(); // Tracks path
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        List<Vertex> explored = new ArrayList<>();

        // Set initial distances (infinity except for start node)
        for (Vertex v : getAllVertices(start)) {
            distances.put(v, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.add(start);

        // Main algorithm loop
        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            explored.add(current);

            if (current.equals(end)) break; // Early exit if target reached

            for (Edge edge : current.getEdges()) {
                Vertex neighbor = edge.getDestination();
                if (neighbor.isObstacle()) continue; // Skip obstacles

                double newDist = distances.get(current) + edge.getWeight();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct path and return result
        List<Vertex> path = reconstructPath(predecessors, end);
        return new PathResult(
            path,
            explored,
            path.isEmpty() ? Double.POSITIVE_INFINITY : distances.get(end),
            System.currentTimeMillis() - startTime
        );
    }

    /**
     * A* algorithm with Euclidean heuristic.
     * Optimizes Dijkstra by prioritizing nodes closer to the target.
     */
    public static PathResult aStar(Vertex start, Vertex end) {
        long startTime = System.currentTimeMillis();
        
        Map<Vertex, Double> gScore = new HashMap<>(); // Actual distances from start
        Map<Vertex, Double> fScore = new HashMap<>(); // Estimated total cost (gScore + heuristic)
        Map<Vertex, Vertex> predecessors = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));
        List<Vertex> explored = new ArrayList<>();

        // Initialize scores
        for (Vertex v : getAllVertices(start)) {
            gScore.put(v, Double.POSITIVE_INFINITY);
            fScore.put(v, Double.POSITIVE_INFINITY);
        }
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, end));
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            explored.add(current);

            if (current.equals(end)) break;

            for (Edge edge : current.getEdges()) {
                Vertex neighbor = edge.getDestination();
                if (neighbor.isObstacle()) continue;

                double tentativeGScore = gScore.get(current) + edge.getWeight();
                if (tentativeGScore < gScore.get(neighbor)) {
                    predecessors.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));
                    if (!queue.contains(neighbor)) queue.add(neighbor);
                }
            }
        }

        List<Vertex> path = reconstructPath(predecessors, end);
        return new PathResult(
            path,
            explored,
            path.isEmpty() ? Double.POSITIVE_INFINITY : gScore.get(end),
            System.currentTimeMillis() - startTime
        );
    }

    // --- Helper Methods ---

    /**
     * Reconstructs path from predecessors map.
     */
    private static List<Vertex> reconstructPath(Map<Vertex, Vertex> predecessors, Vertex end) {
        List<Vertex> path = new LinkedList<>();
        if (!predecessors.containsKey(end)) return path;

        Vertex current = end;
        while (current != null) {
            path.add(0, current); // Insert at beginning to reverse order
            current = predecessors.get(current);
        }
        return path;
    }

    /**
     * Euclidean distance heuristic for A*.
     */
    private static double heuristic(Vertex a, Vertex b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Gets all vertices reachable from 'start' (BFS traversal).
     */
    private static Set<Vertex> getAllVertices(Vertex start) {
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            if (visited.add(current)) {
                for (Edge edge : current.getEdges()) {
                    queue.add(edge.getDestination());
                }
            }
        }
        return visited;
    }
}