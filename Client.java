import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * SimpleRestClient
 * A minimalist Java application demonstrating a REST client using standard Java (HttpURLConnection).
 * This application retrieves a post (in JSON format) from the JSONPlaceholder API.
 */
public class client {

    private static final String API_URL_BASE = "https://jsonplaceholder.typicode.com/posts/";

    public static void main(String[] args) {
        // --- 1. Simple Console UI/UX Component ---
        System.out.println("--- Client Demo ---");
        System.out.println("This client fetches a single 'Post' from a public test API (JSONPlaceholder).");
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the Post ID (1-100) you want to fetch: ");

        if (scanner.hasNextInt()) {
            int postId = scanner.nextInt();
            
            // Validate input for a slightly better UX
            if (postId < 1 || postId > 100) {
                System.err.println("\nError: Please enter an ID between 1 and 100.");
            } else {
                System.out.println("\nFetching data for Post ID: " + postId + "...");
                String jsonResponse = fetchPost(postId);
                
                // Display the result
                if (jsonResponse != null) {
                    System.out.println("\n--- SUCCESS: Data Received ---");
                    // In a real application, you would use a library (like Jackson or Gson)
                    // to turn this JSON string into a Java object.
                    System.out.println(jsonResponse); 
                    System.out.println("---------------------------------");
                }
            }
        } else {
            System.err.println("\nError: Invalid input. Please enter a number.");
        }

        scanner.close();
        System.out.println("\nClient finished execution.");
    }

    /**
     * Fetches a Post by ID from the external REST API.
     * @param id The ID of the post to fetch.
     * @return The JSON response body as a String, or null if an error occurred.
     */
    private static String fetchPost(int id) {
        try {
            // Construct the full URL for the specific post
            URL url = new URL(API_URL_BASE + id);
            
            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set the request method to GET
            connection.setRequestMethod("GET");
            
            // Set request properties (optional, but good practice)
            connection.setRequestProperty("Accept", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();
            
            // Check if the request was successful (HTTP 200 OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                
                // Read the response from the connection's input stream
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                
                // Close the reader and disconnect
                in.close();
                connection.disconnect();

                return content.toString();
                
            } else {
                // Handle non-200 responses (e.g., 404 Not Found)
                System.err.println("API Request failed. Response Code: " + responseCode);
                
                // Read and print the error stream for debugging if available
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream()))) {
                    String errorLine;
                    StringBuilder errorContent = new StringBuilder();
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorContent.append(errorLine);
                    }
                    if (errorContent.length() > 0) {
                        System.err.println("Error details: " + errorContent.toString());
                    }
                }
                connection.disconnect();
                return null;
            }
        } catch (Exception e) {
            // Catch connection errors, malformed URLs, etc.
            System.err.println("An unexpected error occurred during API call:");
            e.printStackTrace();
            return null;
        }
    }
}


