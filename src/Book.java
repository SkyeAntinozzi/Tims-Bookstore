/**
 * Author: Skye Antinozzi
 * Created for the Introduction to Java and OOP at Anoka-Ramsey Community College.
 * Just a simple book store Swing application.
 */
public class Book{

    // Name of the book
    private String name;

    // Price of the book
    private String price;

    // Quantity of this book on-hand
    private int quantity;

    public Book(String name, String price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }//end Book

    public String getName(){
        return name;
    }//end getName

    public int getQuantity(){
        return quantity;
    }//end getQuantity

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }//end setQuantity

    public String getPrice(){
        return price;
    }//end getPrice

    public String toString(){
        return name  + " $" + price;
    }//end toString

}//end class Book
