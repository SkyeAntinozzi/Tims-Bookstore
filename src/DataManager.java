/**
 * Author: Skye Antinozzi
 * Created for the Introduction to Java and OOP at Anoka-Ramsey Community College.
 * Just a simple book store Swing application.
 */

import java.io.*;
import java.util.*;
import javax.swing.*;

public class DataManager {

    private File dataFile;

    // Creates a datamanger that will operate on the given database file
    public DataManager(File database){
        dataFile = database;
    }//end DataManager

    // Loads the data in from a formatted book database file
    public DefaultListModel<Book> getData() throws IOException{

        // Open the file
        Scanner inFile = new Scanner(dataFile);

        // Setup a model to store the file data
        DefaultListModel<Book> model = new DefaultListModel<>();

        // Store tokens here
        String book, price, quantity;

        // While there is still data to be read
        while(inFile.hasNextLine()) {

            // Grab the input line
            StringTokenizer input = new StringTokenizer(inFile.nextLine(), ",");

            // Get the book token
            book = input.nextToken().trim();

            // Get the price token
            price = input.nextToken().trim();

            // Get the quantity token
            quantity = input.nextToken().trim();

            // Create a new book with the tokens
            Book newBook = new Book(book, price, Integer.parseInt(quantity));

            // Add the book to the model
            model.addElement(newBook);
        }//end get data loop

        // Return the database model
        return model;
    }//end getData

    public void updateFile(DefaultListModel<Book> data){

        // Get the file name with extension
        String file = dataFile.getName();

        // Get the first occurrence of '.' to separate .txt extension
        int index = file.indexOf('.');

        // Extract the file name with no extension
        String fileName = file.substring(0, index);

        // Append the name addition and extension
        fileName += "Out.txt";

        // Output to this file
        File outputFile = new File(fileName);

        // Hold tokens here
        String title, price, quantity;

        // Try-with-resources to close up writer
        try(PrintWriter writer = new PrintWriter(outputFile)) {

            // Iterate over the model
            for(int i = 0; i < data.size(); i++){

                // Pull a book from the model
                Book b = data.elementAt(i);

                // Get the three tokens from the book
                title = b.getName();
                price = b.getPrice();
                quantity = Integer.toString(b.getQuantity());

                // Output those tokens to the file
                writer.println(title + ", " +  price + ", " + quantity);

            }//end write data loop

        } catch(IOException ex){
            ex.printStackTrace();
        }

    }//end updateFile

}//end class DataManager
