/**
 * @author BA MALAZA
 * @version MiniProject
 */
package UI;

import Graph.Edge;
import Graph.ImageGraph;
import Graph.Vertex;
import Algorithms.Pathfinder;
import Algorithms.SimilarityDetector;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application class for the Graph-Based Image Analysis Tool.
 * Provides a complete UI for graph-based image processing including:
 * - Image loading and graph construction
 * - Pathfinding visualization (Dijkstra/A*)
 * - Image similarity comparison
 */
public class Main extends Application {
    // Image display components
    private ImageView originalImageView = new ImageView();
    private ImageView graphImageView = new ImageView();
    private ImageView resultView = new ImageView();
    
    // Control buttons
    private Button addImageBtn = createIconButton("➕", "Add Image");
    private Button findPathBtn = createStyledButton("Find Path");
    private Button compareBtn = createStyledButton("Compare Images");
    private Button deleteBtn = createIconButton("🗑️", "Delete Image");
    
    // Algorithm controls
    private ComboBox<String> algorithmCombo = new ComboBox<>();
    private Slider regionSizeSlider = new Slider(5, 50, 10);
    
    // Output console
    private TextArea pathfindingOutput = new TextArea();
    
    // Current graph data
    private ImageGraph graph;

    @Override
    public void start(Stage primaryStage) {
        // Main layout containers
        BorderPane root = new BorderPane();
        VBox contentArea = new VBox(20);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20));

        // Navigation sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Image display area
        HBox imageCanvasBox = new HBox(20);
        imageCanvasBox.setAlignment(Pos.CENTER);
        imageCanvasBox.getChildren().addAll(
            createImageCanvas("Original Image", originalImageView),
            createImageCanvas("Graph Visualization", graphImageView)
        );

        // Algorithm controls
        HBox controls = createControlPanel();
        controls.setAlignment(Pos.CENTER);

        // Result display
        VBox results = createResultDisplay();
        
        // Assemble content area
        contentArea.getChildren().addAll(imageCanvasBox, controls, results);
        root.setCenter(contentArea);

        // Configure scene
        Scene scene = new Scene(root, 1200, 900);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Graph Image Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        configureUI();
    }

    /**
     * Creates a panel to display an image with a title.
     * @param title The panel's title
     * @param imageView The ImageView to display
     * @return The created image panel
     */
    private VBox createImageCanvas(String title, ImageView imageView) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("image-canvas");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("canvas-title");
        
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");
        
        box.getChildren().addAll(titleLabel, imageView);
        return box;
    }

    /**
     * Creates the sidebar with navigation buttons.
     * @return The configured sidebar
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(150);

        Label appTitle = new Label("Graph Analyzer");
        appTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        addImageBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        
        sidebar.getChildren().addAll(appTitle, addImageBtn, deleteBtn);
        return sidebar;
    }

    /**
     * Creates the control panel with algorithm options.
     * @return The configured control panel
     */
    private HBox createControlPanel() {
        HBox panel = new HBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("control-panel");

        // Algorithm selection
        VBox algorithmBox = new VBox(5, new Label("Algorithm:"), algorithmCombo);
        algorithmCombo.getItems().addAll("Dijkstra's Algorithm", "A* Algorithm");
        algorithmCombo.setValue("A* Algorithm");

        // Region size control
        VBox regionBox = new VBox(5, new Label("Region Size:"), regionSizeSlider);
        regionSizeSlider.setShowTickLabels(true);
        regionSizeSlider.setShowTickMarks(true);
        regionSizeSlider.setMajorTickUnit(10);
        regionSizeSlider.setMinorTickCount(5);

        panel.getChildren().addAll(algorithmBox, regionBox, findPathBtn, compareBtn);
        return panel;
    }

    /**
     * Creates the result display area at the bottom.
     * @return The configured result display
     */
    private VBox createResultDisplay() {
        VBox resultBox = new VBox(20);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(20));
        resultBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10;");

        resultView.setFitWidth(600);
        resultView.setFitHeight(300);
        resultView.setPreserveRatio(true);
        resultView.getStyleClass().add("result-view");

        pathfindingOutput.setPrefHeight(150);
        pathfindingOutput.getStyleClass().add("output-area");
        
        resultBox.getChildren().addAll(resultView, pathfindingOutput);
        return resultBox;
    }

    /**
     * Creates a styled button with standard appearance.
     * @param text The button text
     * @return The created button
     */ 
    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("primary-button");
        return btn;
    }

    /**
     * Creates an icon button with a tooltip.
     * @param icon The icon symbol
     * @param tooltip The tooltip text
     * @return The created button
     */
    private Button createIconButton(String icon, String tooltip) {
        Button btn = new Button(icon);
        btn.setTooltip(new Tooltip(tooltip));
        btn.getStyleClass().add("icon-button");
        return btn;
    }

    /**
     * Adds text to the output console.
     * @param text The text to append
     */
    private void appendOutput(String text) {
        pathfindingOutput.appendText(text + "\n\n");
    }

    /**
     * Shows a warning alert to the user.
     * @param message The warning message
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets up all button click handlers.
     */
    private void configureUI() {
        addImageBtn.setOnAction(e -> loadImage());
        deleteBtn.setOnAction(e -> deleteImage());
        findPathBtn.setOnAction(e -> findPath());
        compareBtn.setOnAction(e -> compareImages());
        
        pathfindingOutput.setEditable(false);
    }

    /**
     * Loads an image file and creates its graph representation.
     */
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                BufferedImage bufferedImage = ImageIO.read(file);
                
                originalImageView.setImage(image);
                graph = new ImageGraph(bufferedImage, (int)regionSizeSlider.getValue());
                
                WritableImage graphImage = visualizeGraph(graph);
                graphImageView.setImage(graphImage);
                
                appendOutput("Loaded image and created graph with " + 
                           graph.getVertices().size() + " vertices");
            } catch (IOException ex) {
                showAlert("Error loading image: " + ex.getMessage());
            }
        }
    }

    /**
     * Clears all loaded images and results.
     */
    private void deleteImage() {
        originalImageView.setImage(null);
        graphImageView.setImage(null);
        graph = null;
        resultView.setImage(null);
        pathfindingOutput.clear();
    }

    /**
     * Draws the graph structure over the original image.
     * @param graph The graph to visualize
     * @return Image showing nodes and edges
     */
    private WritableImage visualizeGraph(ImageGraph graph) {
        BufferedImage original = graph.getImage();
        BufferedImage combined = new BufferedImage(
            original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D g2d = combined.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        
        // Draw graph structure
        g2d.setColor(java.awt.Color.RED);
        for (Vertex vertex : graph.getVertices()) {
            int x = vertex.getX();
            int y = vertex.getY();
            
            // Draw edges
            g2d.setColor(new java.awt.Color(0, 255, 0, 100));
            for (Edge edge : vertex.getEdges()) {
                Vertex dest = edge.getDestination();
                g2d.drawLine(x, y, dest.getX(), dest.getY());
            }
            
            // Draw vertex
            g2d.setColor(vertex.isObstacle() ? java.awt.Color.BLACK : java.awt.Color.RED);
            g2d.fillOval(x - 2, y - 2, 5, 5);
        }
        
        g2d.dispose();
        return SwingFXUtils.toFXImage(combined, null);
    }
    /**
     * Finds and displays the shortest path in the graph.
     */
    private void findPath() {
        if (graph == null) {
            showAlert("Please load an image first");
            return;
        }
        
        List<Vertex> vertices = graph.getVertices();
        if (vertices.size() < 2) {
            showAlert("Graph doesn't have enough vertices");
            return;
        }
        
        Vertex start = vertices.get(0);
        Vertex end = vertices.get(vertices.size() - 1);
        
        Pathfinder.PathResult result = algorithmCombo.getValue().equals("Dijkstra's Algorithm") 
            ? Pathfinder.dijkstra(start, end)
            : Pathfinder.aStar(start, end);
        
        if (result.path.isEmpty()) {
            appendOutput("No path found between the selected points");
        } else {
            appendOutput(String.format(
                "%s found path with %d steps (cost: %.2f) in %d ms\n" +
                "Explored %d vertices",
                algorithmCombo.getValue(),
                result.path.size(),
                result.totalCost,
                result.timeMillis,
                result.explored.size()
            ));
            
            resultView.setImage(visualizePath(graph, result));
        }
    }
    
    /**
     * Draws the pathfinding results on the image.
     * @param graph The source graph
     * @param result The pathfinding results
     * @return Image showing the path
     */
    private WritableImage visualizePath(ImageGraph graph, Pathfinder.PathResult result) {
        BufferedImage original = graph.getImage();
        BufferedImage combined = new BufferedImage(
            original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D g2d = combined.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        
        // Draw explored areas
        g2d.setColor(new java.awt.Color(255, 255, 0, 100));
        for (Vertex v : result.explored) {
            g2d.fillOval(v.getX() - 3, v.getY() - 3, 7, 7);
        }
        
        // Draw path
        if (!result.path.isEmpty()) {
            g2d.setColor(java.awt.Color.RED);
            g2d.setStroke(new java.awt.BasicStroke(2));
            
            Vertex prev = result.path.get(0);
            for (int i = 1; i < result.path.size(); i++) {
                Vertex current = result.path.get(i);
                g2d.drawLine(prev.getX(), prev.getY(), current.getX(), current.getY());
                prev = current;
            }
            
            // Mark start and end
            g2d.setColor(java.awt.Color.GREEN);
            g2d.fillOval(result.path.get(0).getX() - 5, result.path.get(0).getY() - 5, 11, 11);
            
            g2d.setColor(java.awt.Color.BLUE);
            g2d.fillOval(prev.getX() - 5, prev.getY() - 5, 11, 11);
        }
        
        g2d.dispose();
        return SwingFXUtils.toFXImage(combined, null);
    }
    
    /**
     * Compares two images using graph similarity metrics.
     * Prompts user to select a second image for comparison.
     */
    private void compareImages() {
        // Check if first image is loaded
        if (graph == null) {
            showAlert("Please load the first image before comparing");
            return;
        }

        // Prompt user to load second image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Second Image for Comparison");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File secondFile = fileChooser.showOpenDialog(null);
        if (secondFile == null) {
            return; // User cancelled
        }

        try {
            // Load second image and create its graph
            BufferedImage secondImage = ImageIO.read(secondFile);
            ImageGraph secondGraph = new ImageGraph(
                secondImage, 
                (int)regionSizeSlider.getValue()
            );

            appendOutput("Loaded second image: " + secondFile.getName());
            appendOutput("Second graph has " + secondGraph.getVertices().size() + " vertices");

            // Perform comparison
            int k = 5; // Number of neighbors for k-NN similarity
            SimilarityDetector.SimilarityResult result = 
                SimilarityDetector.calculateSimilarity(graph, secondGraph, k);
            
            appendOutput(String.format(
                "Comparison Results:\n" +
                "Combined Similarity: %.2f\n" +
                "Feature Similarity: %.2f\n" +
                "Structural Similarity: %.2f\n" +
                "Time Taken: %d ms",
                result.similarityScore,
                result.featureSimilarity,
                result.structuralSimilarity,
                result.timeMillis
            ));
            
            // Visualize comparison
            resultView.setImage(visualizeSimilarity(graph, secondGraph));
            
        } catch (IOException ex) {
            showAlert("Error loading second image: " + ex.getMessage());
        }
    }
    
    /**
     * Draws similarity connections between two images.
     * @param graph1 First image's graph
     * @param graph2 Second image's graph
     * @return Image showing matched regions
     */
    private WritableImage visualizeSimilarity(ImageGraph graph1, ImageGraph graph2) {
        BufferedImage img1 = graph1.getImage();
        BufferedImage img2 = graph2.getImage();
        
        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = Math.max(img1.getHeight(), img2.getHeight());
        
        BufferedImage combined = new BufferedImage(
            width * 2, height, BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D g2d = combined.createGraphics();
        g2d.drawImage(img1, 0, 0, null);
        g2d.drawImage(img2, width, 0, null);
        
        // Draw similarity connections
        g2d.setColor(java.awt.Color.RED);
        g2d.setStroke(new java.awt.BasicStroke(2));
        
        List<Vertex> vertices1 = graph1.getVertices();
        List<Vertex> vertices2 = graph2.getVertices();
        
        if (!vertices1.isEmpty() && !vertices2.isEmpty()) {
            int samples = Math.min(10, Math.min(vertices1.size(), vertices2.size()));
            
            for (int i = 0; i < samples; i++) {
                Vertex v1 = vertices1.get(i * vertices1.size() / samples);
                
                Vertex mostSimilar = vertices2.get(0);
                double minDistance = Double.POSITIVE_INFINITY;
                
                for (Vertex v2 : vertices2) {
                    double dist = v1.featureDistance(v2);
                    if (dist < minDistance) {
                        minDistance = dist;
                        mostSimilar = v2;
                    }
                }
                
                g2d.drawLine(
                    v1.getX(), v1.getY(),
                    width + mostSimilar.getX(), mostSimilar.getY()
                );
            }
        }
        
        g2d.dispose();
        return SwingFXUtils.toFXImage(combined, null);
    }

    /**
     * Launches the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}