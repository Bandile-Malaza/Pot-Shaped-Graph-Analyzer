/**
 * @author BA MALAZA
 * @version MiniProject
 */
package Graph;

import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Converts images into graphs where vertices represent regions
 * and edges capture spatial-feature relationships.
 */
public class ImageGraph extends Graph {
    private final BufferedImage image;
    private final int regionSize;

    public ImageGraph(BufferedImage image, int regionSize) {
        super(true, false); // Weighted, undirected
        this.image = image;
        this.regionSize = regionSize;
        buildGraph();
    }

    private void buildGraph() {
        // Create vertices for each region
        for (int y = 0; y < image.getHeight(); y += regionSize) {
            for (int x = 0; x < image.getWidth(); x += regionSize) {
                Vertex v = addVertex(x + "," + y, x, y);
                v.setFeatures(calculateRegionFeatures(x, y));
                v.setObstacle(isObstacleRegion(x, y));
            }
        }

        // Connect adjacent regions with graph-based weights
        getVertices().forEach(v -> {
            int x = v.getX(), y = v.getY();
            connectIfValid(v, x + regionSize, y);
            connectIfValid(v, x - regionSize, y); 
            connectIfValid(v, x, y + regionSize);
            connectIfValid(v, x, y - regionSize);
        });
    }

    private void connectIfValid(Vertex source, int x, int y) {
        Vertex dest = getVertex(x + "," + y);
        if (dest != null && !dest.isObstacle()) {
            double weight = source.distanceTo(dest) * 
                          (1 + source.featureDistance(dest)); // Graph-enhanced weight
            addEdge(source.getId(), dest.getId(), weight);
        }
    }

    private double[] calculateRegionFeatures(int x, int y) {
        double[] features = new double[3]; // RGB averages
        int count = 0;
        
        for (int dy = 0; dy < regionSize && y + dy < image.getHeight(); dy++) {
            for (int dx = 0; dx < regionSize && x + dx < image.getWidth(); dx++) {
                Color c = new Color(image.getRGB(x + dx, y + dy));
                features[0] += c.getRed();
                features[1] += c.getGreen();
                features[2] += c.getBlue();
                count++;
            }
        }
        
        if (count > 0) {
            features[0] /= count;
            features[1] /= count;
            features[2] /= count;
        }
        return features;
    }

    private boolean isObstacleRegion(int x, int y) {
        int darkPixels = 0;
        int total = 0;
        
        for (int dy = 0; dy < regionSize && y + dy < image.getHeight(); dy++) {
            for (int dx = 0; dx < regionSize && x + dx < image.getWidth(); dx++) {
                Color c = new Color(image.getRGB(x + dx, y + dy));
                if ((c.getRed() + c.getGreen() + c.getBlue()) / 3 < 50) darkPixels++;
                total++;
            }
        }
        return (double)darkPixels / total > 0.7;
    }

    public BufferedImage getImage() { return image; }
}