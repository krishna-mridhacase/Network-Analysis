// B.java
import java.io.*;
import java.util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.scoring.ClusteringCoefficient;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class GlobalEffi {
    public List<Double> readFileAndReturnData(String filePath) {
        String line;
        boolean startReadingMatrix = false;

        ArrayList<HashMap<String, Object>> allGraphData = new ArrayList<>();
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        int counter = 0;
        allGraphData.add(new HashMap<>());

        String[] labels = new String[10];
            long startTime = 0;

		String lnum;
        int lineNumber = 0;
        double ge = 0.0;;
        double otherValue1 = 0.0; 
        double otherValue2 = 0.0;

        List<Double> result = new ArrayList<>();

        List<String> clabels = new ArrayList<>();
        List<List<Double>> corrMatrix = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("X")) {
                    startReadingMatrix = true;
                    labels = line.split(",");
					
                    // We start at 1 since the first is the X
                    for (int i = 1; i < labels.length; i++) {
                        // graph.addVertex(labels[i]);
                        clabels.add(labels[i].trim()); 
	
                    }
					
                    for (String label : clabels) {
                        graph.addVertex(label);
                    }
                    

					
                } else if (startReadingMatrix) {
                    if(line.isEmpty()) {


                        //Creating Z-score matrix 
                        List<List<Integer>> resultZScoreMatrix = zscoreMatrix(corrMatrix);
                        

                        //Creating Final Matrix from the z-score matrix
                        List<List<String>> finalMatrix = new ArrayList<>();

                        // Add column labels as the first row. We add an empty string at the beginning for the top-left cell.
                        List<String> headerRow = new ArrayList<>();
                        headerRow.add(""); // Placeholder for the top-left cell
                        headerRow.addAll(clabels);
                        finalMatrix.add(headerRow);
                        
                        // Add the rows with the matrix
                        for (int i = 0; i < resultZScoreMatrix.size(); i++) {
                            List<String> row = new ArrayList<>();
                            row.add(clabels.get(i));  // add the row label
                            for (int j = 0; j < resultZScoreMatrix.get(i).size(); j++) {
                                row.add(String.valueOf(resultZScoreMatrix.get(i).get(j)));
                            }
                            finalMatrix.add(row);

                        }

                            //Adding the edge with the vertex. self loop is not allowed

                        for (int i = 1; i < finalMatrix.size(); i++) { 
                                for (int j = 1; j < finalMatrix.get(i).size(); j++) {
                                    if (i != j && "1".equals(finalMatrix.get(i).get(j))) { // Check i != j to skip diagonal
                                        String rowLabel = finalMatrix.get(i).get(0); 
                                        String colLabel = finalMatrix.get(0).get(j); 
                                        
                                        if (graph.containsVertex(rowLabel) && graph.containsVertex(colLabel)) {
                                            graph.addEdge(rowLabel, colLabel);
                                        }
                                    }
                                }
                        }
                    
                        // Calculate the Characteristic Path Length
                        ge = getGlobalEfficiency(graph);
                        result.add(ge);
                        
                                    
                        counter++;
                        allGraphData.add(new HashMap<String, Object>());
                        startReadingMatrix = false;
                        graph = new SimpleDirectedGraph<>(DefaultEdge.class);
                        corrMatrix.clear();  // Clear the matrix for the next graph (if any)
                        clabels.clear();
                        continue;
                    }

                      // Parse the line into matrix row and add it to adjacencyMatrix
                        String[] matrixRowStrings = line.split(",");

                        List<Double> matrixRow = new ArrayList<>();
                        for (int idx = 1; idx < matrixRowStrings.length; idx++) {
                            matrixRow.add(Double.parseDouble(matrixRowStrings[idx].trim()));  // trim to remove any extra spaces
                        }
                        corrMatrix.add(matrixRow);  
                }  
                
                
            }

        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + filePath);
            e.printStackTrace();
        }

  

    // Return the list
    return result;
    }

    private static List<List<Integer>> zscoreMatrix(List<List<Double>> matrix) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();
    
        // Calculate the mean and standard deviation of the entire dataset
        double sum = 0;
        int totalCount = 0;
        for (List<Double> row : matrix) {
            for (Double val : row) {
                sum += val;
                totalCount++;
            }
        }
        double mean = sum / totalCount;
    
        double squaredSum = 0;
        for (List<Double> row : matrix) {
            for (Double val : row) {
                squaredSum += Math.pow(val - mean, 2);
            }
        }
        double std = Math.sqrt(squaredSum / totalCount);
    
        List<List<Integer>> thresholdedMatrix = new ArrayList<>();
    
        // Compute the Z-scores for each data point and threshold it
        for (int i = 0; i < rows; i++) {
            List<Integer> zRow = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                double x = matrix.get(i).get(j);
                double zScore = (x - mean) / std;
                if (zScore >= 1) {
                    zRow.add(1);
                } else {
                    zRow.add(0);
                }
            }
            thresholdedMatrix.add(zRow);
        }
    
        return thresholdedMatrix;
    }
    

    private static double getGlobalEfficiency(Graph<String, DefaultEdge> graph) {
		FloydWarshallShortestPaths shortestPathAlgo = new FloydWarshallShortestPaths(graph);
		
		double inverseDistanceSumPerVertex = 0.0;
		for(String vertex : graph.vertexSet()) {
			SingleSourcePaths<String, DefaultEdge> shortestPathList = shortestPathAlgo.getPaths(vertex);
			for (String shortestPathVertex : graph.vertexSet()) {
				if(!(shortestPathVertex.equals(vertex))){
					GraphPath<String, DefaultEdge> pathPerVertex = shortestPathList.getPath(shortestPathVertex);
					if(pathPerVertex != null) {
						inverseDistanceSumPerVertex += (Double) (1.0/pathPerVertex.getLength());
					}					
				}
			}
		}
		if(graph.vertexSet().size() < 2){
			return 0.0;
		} else {
			return (1.0/(graph.vertexSet().size()*(graph.vertexSet().size()-1))) * inverseDistanceSumPerVertex;
		}

	}
}
