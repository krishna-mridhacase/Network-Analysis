// A.java
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> filePaths = new ArrayList<>();
      
        int choice = -1;

        while (choice != 1 && choice != 2) {
            System.out.println("Please choose an option:");
            System.out.println("1. Folder");
            System.out.println("2. Files");
            System.out.print("Enter your choice (1/2): ");
            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input.  Please enter '1' for Folder or '2' for Files.");
            }

            
        }

        if (choice == 1) {
            boolean validFolder = false;
            while (!validFolder) {
                System.out.print("Enter the path to the folder: ");
                String folderPath = scanner.nextLine();
                File folder = new File(folderPath);

                if (folder.exists() && folder.isDirectory()) {
                    validFolder = true;
                    File[] listOfFiles = folder.listFiles();
                    if (listOfFiles != null) {
                        for (File file : listOfFiles) {
                            if (file.isFile()) {
                                filePaths.add(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    System.out.println("Error: The path is not a valid directory. Please try again.");
                }
            }
        } else {
            int numberOfFiles = 0;
            while (numberOfFiles <= 0) {
                System.out.print("Enter the number of Events: ");
                try {
                    numberOfFiles = Integer.parseInt(scanner.nextLine());
                    if (numberOfFiles <= 0) {
                        System.out.println("Please enter a positive number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number entered. Please enter a positive number.");
                    numberOfFiles = 0; // Reset number of files to keep the loop going
                }
            }

            for (int i = 0; i < numberOfFiles; i++) {
        
                System.out.print("Enter the path for Event file : "); // Use the event number in the prompt
                String filePath = scanner.nextLine();
                File file = new File(filePath);
        
                if (file.exists() && !file.isDirectory()) {
                    filePaths.add(filePath); // Store the file path
                } else {
                    System.out.println("Error: The path does not point to a valid file. Please try again.");
                    i--; // Decrement i to repeat the loop for the same file number
                    
                }
            }
        }



        Set<Integer> methodChoices = new HashSet<>();
        System.out.println("Choose the class methods to process the files (enter numbers separated by space):");
        System.out.println("1. Characteristic Path Lenght");
        System.out.println("2. Clustering coefficient");
        System.out.println("3. Global Efficeiency");
        System.out.println("4. Degree Distribution");
        System.out.println("5. Number of Cliques");
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Enter your choices (e.g., '1 2 3'): ");
            String[] inputChoices = scanner.nextLine().split("\\s+");
            methodChoices.clear(); // Clear previous choices
            for (String n : inputChoices) {
                try {
                    int methodChoice = Integer.parseInt(n);
                    if (methodChoice >= 1 && methodChoice <= 5) {
                        methodChoices.add(methodChoice);
                        validInput = true; // Set to true if at least one valid input is provided
                    } else {
                        System.out.println("Invalid choice: " + n + ". Please enter a number between 1 and 5.");
                        validInput = false; // Reset to false if any input is invalid
                        break; // Break out of the for loop
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid input " + n + ". Please enter numbers (1-5).");
                    validInput = false; // Reset to false if a non-numeric input is encountered
                    break; // Break out of the for loop
                }
            }
        }
        

        for (String path : filePaths) {
            System.out.println("Processing Event file: " + path);
            for (int methodChoice : methodChoices) {
                switch (methodChoice) {
                    case 1:
                        CPL b = new CPL();
                        List<Double> result1 = b.readFileAndReturnData(path);

                        // Access the double values
                        double cpl = result1.get(0);
                        double value1 = result1.get(1);
                        double value2 = result1.get(2);
                        System.out.println("The Cpl Value is : " +cpl);
                        
                        break;
                    case 2:
                        ClueCoeff c = new ClueCoeff();
                       List<Double> result2 = c.readFileAndReturnData(path);

                        // Access the double values
                        double cc = result2.get(0);
                        System.out.println("The Clustering Coefficient is : " +cc);
                        break;
                    case 3:
                        GlobalEffi d = new GlobalEffi();
                        List<Double> result3 = d.readFileAndReturnData(path);

                        // Access the double values
                        double ge = result3.get(0);
                        System.out.println("The Global Effieciency is : " +ge);
                        break;
                    case 4:
                        DegreeDis e = new DegreeDis();
                        List<Double> nodeDegrees = e.readFileAndReturnData(path);

                        // Assuming the nodeDegrees list is evenly divided among the three graphs
                        int totalNodes = nodeDegrees.size();
                        int nodesPerGraph = totalNodes / 3; // Since we know there are three graphs

                                        List<List<Double>> graphsNodeDegrees = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            int start = i * nodesPerGraph;
                            int end = (i + 1) * nodesPerGraph;
                            List<Double> singleGraphDegrees = new ArrayList<>(nodeDegrees.subList(start, end));
                            graphsNodeDegrees.add(singleGraphDegrees);
                        }

                        // Now you can print or process each graph's node degrees separately --graphsNodeDegrees.size()
                        for (int i = 0; i < 1 ; i++) {
                            System.out.println("Degree Distribution: " + graphsNodeDegrees.get(i));
                        }
                       
                        break;
                    case 5:
                        Cliques f = new Cliques();
                        List<Double> result4 = f.readFileAndReturnData(path);

                        // Access the double values
                        double cli = result4.get(0);
                        System.out.println("# of Cliques : " +cli);
                        break;
                    default:
                        System.out.println("Invalid choice: " + methodChoice);
                        break;
                }
            }
        }


        scanner.close();
    }
}
