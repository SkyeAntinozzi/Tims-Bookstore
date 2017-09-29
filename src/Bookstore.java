/**
 * Author: Skye Antinozzi
 * Created for the Introduction to Java and OOP at Anoka-Ramsey Community College.
 * Just a simple book store Swing application.
 */

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;


public class Bookstore {

    // Represents the book database text file
    private File databaseFile = new File("Books.txt");

    // The font for lists text
    private Font listFont = new Font("Monospaced", Font.ITALIC, 18);

    // The font for the components
    private Font componentFont = new Font("Monospaced", Font.PLAIN, 15);

    // The background color for the lists
    private Color listColor = new Color(75,75,75);

    // The list text color
    private Color listTextColor = new Color(255,110,40);

    // The top level container for all components
    private JFrame frame;

    // The menu bar at the top of the screen
    private JMenuBar menuBar;

    // The panel holding the shopping cart
    private CartPanel cartPanel;

    // The panel holding the store's stock
    private StorePanel storePanel;

    // Panel preferred dimension
    public static final Dimension PANEL_SIZE = new Dimension(600, 500);

    public static final Dimension CONTROL_PANEL_SIZE = new Dimension(500, 75);

    // Constructs a new Bookstore that is the top-level program
    public Bookstore(){

        // Set the Swing components to have a System look and feel
        // instead of the java Steel look and feel
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex){
            ex.printStackTrace();
        }

        // Create the new top-level container
        frame = new JFrame("Tim's Bookstore");

        // Create the new panels that house individual components
        cartPanel = new CartPanel();
        storePanel = new StorePanel();

        // Create the menu bar for the top of the frame
        menuBar = new JMenuBar();

        // Setup menu components and properties
        setupMenu();

        // Setup the frame properties
        setupFrame();

        // Load in the database from the database file
        loadDatabase();

    }//end Bookstore

    private void setupFrame(){
        // Set the layout for the frame's content pane
        frame.setLayout(new GridLayout(0,2));

        // Set frame's menu bar
        frame.setJMenuBar(menuBar);

        // Add the components to the frame's content pane
        frame.add(storePanel, BorderLayout.WEST);
        frame.add(cartPanel, BorderLayout.EAST);

        // Pack the frame around the panels
        frame.pack();

        // Spawn JFrame at the center of the screen
        frame.setLocationRelativeTo(null);

        // Make it so the upper X closes the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Frame is visible
        frame.setVisible(true);
    }//end setupFrame

    private void setupMenu(){

        // Store menu setup
            JMenu storeMenu = new JMenu("Store");
            JMenuItem loadDatabaseMenuItem = new JMenuItem("Open Database");
            JMenuItem addToCartMenuItem = new JMenuItem("Add to cart");

            // Add listener for store menu items
            loadDatabaseMenuItem.addActionListener(storePanel.listener);
            addToCartMenuItem.addActionListener(storePanel.listener);

            // Add menu item to store menu
            storeMenu.add(loadDatabaseMenuItem);
            storeMenu.add(addToCartMenuItem);

            // Add the store menu to menu bar
            menuBar.add(storeMenu);

        // Cart menu setup
            JMenu cartMenu = new JMenu("Cart");
            JMenuItem clearCartMenuItem = new JMenuItem("Clear Cart");
            JMenuItem removeItemMenuItem = new JMenuItem("Remove Item");
            JMenuItem checkOutMenuItem= new JMenuItem("Check Out");

            // Add listeners for cart menu items
            clearCartMenuItem.addActionListener(cartPanel.listener);
            removeItemMenuItem.addActionListener(cartPanel.listener);
            checkOutMenuItem.addActionListener(cartPanel.listener);

            // Add menu items to cart menu
            cartMenu.add(clearCartMenuItem);
            cartMenu.add(removeItemMenuItem);
            cartMenu.add(checkOutMenuItem);

            // Add the cart menu to menu bar
            menuBar.add(cartMenu);

        // The menu has now been setup
    }//end setupMenu

    private void loadDatabase(){

        DataManager dm = new DataManager(databaseFile);
        DefaultListModel<Book> data = null;

        try {
            data = dm.getData();

            for(int i = 0; i < data.size(); i++)
                storePanel.bookData.addElement(data.getElementAt(i));

        } catch (IOException ex) {
            System.out.println(ex);
        }

    }//end loadDatabase

    private void outputMessage(String msg, String title){
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }//end output message

    private class CartPanel extends JPanel{

        // Store books in the shopping cart here
        private JList<Book> shoppingCart;

        // Stores the book data in this default list model
        private DefaultListModel<Book> bookData;

        // Scroll pane for the shopping cart
        private JScrollPane scrollPane;

        private JLabel totalLabel;

        // Clear the shopping cart
        private JButton clearAllBtn;

        // Remove selected item from cart
        private JButton removeItemBtn;

        // Check out and pay for items
        private JButton checkOutBtn;

        // The panel containing the shopping cart list
        private JPanel cartListPanel;

        // The panel containing the cart control panel
        private JPanel cartControlPanel;

        // The total value of books in the cart
        private BigDecimal totalCost = new BigDecimal("0.0");

        // The listener for all events on this panel
        private CartPanelListener listener = new CartPanelListener();

        // Constructs a new CartPanel
        public CartPanel(){

            // Create the cart list panel
            cartListPanel = new JPanel();

            // Create the cart control panel
            cartControlPanel = new JPanel();

            // Set the panel properties
            setupCartPanel();

            // Setup the cart list panel properties
            setupCartListPanel();

            // Setup the cart control panel properties
            setupCartControlPanel();

            // Add the list panel to the parent panel
            add(cartListPanel, BorderLayout.CENTER);

            // Add the cart control panel to the parent panel
            add(cartControlPanel, BorderLayout.SOUTH);

        }//end CartPanel

        // Add the book to the cart
        public void addToCart(Book b){
            // Add the book to the model
            bookData.addElement(b);

            // Add the total to the price
            addTotal(b.getPrice());
        }//end addToCart

        // Checks out by buying all the books in the cart
        public void buyBooks(){

            BigDecimal salesTax, // The sales tax which is 7% of the subtotal
                       total,    // The total cost
                       subtotal; // The subtotal which is a copy of totalCost

            // Copy total cost into subtotal
            subtotal = new BigDecimal(totalCost.toString());

            // If the cart isn't empty
            if(!bookData.isEmpty()) {

                // The number of books in the cart
                int cartSize = bookData.getSize();

                // Clear the list from back to front
                for (int i = cartSize-1; i >= 0; i--) {

                    // Get the current book
                    Book b = bookData.elementAt(i);

                    // Remove the book from the cart
                    bookData.removeElement(b);

                    // Subtract the book from the cart total
                    subtractTotal(b.getPrice());

                }//end clear cart loop

                // Compute the sales tax
                salesTax = subtotal.multiply(new BigDecimal(0.07));

                // Compute tht total
                total = salesTax.add(subtotal);

                // Update the file to reflect the on-hand quantity
                new DataManager(databaseFile).updateFile(storePanel.bookData);

                // Output a message for the checkout details
                String s = "Subtotal: $" + subtotal.setScale(2, BigDecimal.ROUND_CEILING).doubleValue() +
                           "\nSales Tax: $" + salesTax.setScale(2, BigDecimal.ROUND_CEILING).doubleValue() +
                           "\nTotal: $" + total.setScale(2, BigDecimal.ROUND_CEILING).doubleValue() + "\nThanks for shopping Tim's bookstore!";

                outputMessage(s, "Checkout Receipt");

            }//end if not empty

        }//end buyBooks

        // Adds the provided string to the total cart value
        public void addTotal(String addMe){
            totalCost = totalCost.add(new BigDecimal(addMe));
            totalLabel.setText("$" + totalCost.setScale(2, BigDecimal.ROUND_CEILING).doubleValue());
        }//end addTotal

        // Subtracts the provided string from the total cart value
        public void subtractTotal(String subtractMe){
            totalCost = totalCost.subtract(new BigDecimal(subtractMe));
            totalLabel.setText("$" + totalCost.setScale(2, BigDecimal.ROUND_CEILING).doubleValue());
        }//end subtractTotal

        public void clearCart(){
            // If the cart isn't empty
            if(!bookData.isEmpty()) {
                // Store book references here
                Book b = null;

                // The number of books in the cart
                int cartSize = bookData.getSize();

                // Clear the list from back to front
                for (int i = cartSize-1; i >= 0; i--) {

                    // Get the current book
                    b = bookData.elementAt(i);

                    // Remove the book from the cart
                    bookData.removeElement(b);

                    // Return the book to the store's stock
                    storePanel.returnItem(b);

                    // Subtract the book's price
                    subtractTotal(b.getPrice());

                }//end clear cart loop
            }//end if !empty
        }//end clearCart

        // Helper - Setup the cart list panel
        private void setupCartListPanel(){

            // Setup border layout for the list panel
            cartListPanel.setLayout(new BorderLayout());

            // Create the list model that will store the book data
            bookData = new DefaultListModel<>();

            // Create the shopping cart as the JList
            shoppingCart = new JList<>(bookData);

            // Create the scroll pane
            scrollPane = new JScrollPane(shoppingCart);

            // Setup single selection mode
            shoppingCart.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

            // One item per line
            shoppingCart.setLayoutOrientation(JList.VERTICAL);

            // Set the listFont for the list
            shoppingCart.setFont(listFont);

            shoppingCart.setForeground(listTextColor);

            // Set the the background color for the list
            shoppingCart.setBackground(listColor);

            // Setup scroll bar policies
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            // Add the scroll pane to the panel
            cartListPanel.add(scrollPane, BorderLayout.CENTER);

        }//end setupCartListPanel

        // Helper - Setup the cart control panel
        private void setupCartControlPanel(){

            cartControlPanel.setLayout(new GridLayout(0,4));
            // Create the clear button
            clearAllBtn = new JButton("Clear Cart");

            // Create the remove item button
            removeItemBtn = new JButton("Remove Item");

            // Create the check out button
            checkOutBtn = new JButton("Check Out");

            // Total label
            totalLabel = new JLabel();

            totalLabel.setText("$" + totalCost.toString());

            // Set font for buttons
            clearAllBtn.setFont(componentFont);
            removeItemBtn.setFont(componentFont);
            checkOutBtn.setFont(componentFont);
            totalLabel.setFont(componentFont);

            totalLabel.setBorder(BorderFactory.createTitledBorder("Subtotal"));

            // Add listeners for all the buttons
            clearAllBtn.addActionListener(listener);
            removeItemBtn.addActionListener(listener);
            checkOutBtn.addActionListener(listener);

            // Add the clear all button
            cartControlPanel.add(clearAllBtn);

            // Add the remove item button
            cartControlPanel.add(removeItemBtn);

            // Add the checkout button
            cartControlPanel.add(checkOutBtn);

            cartControlPanel.add(totalLabel);

            // Setup border for the shopping cart controls
            cartControlPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart Controls"));

            cartControlPanel.setPreferredSize(CONTROL_PANEL_SIZE);
        }//end setupCartControlPanel

        // Helper - Set the panel properties
        private void setupCartPanel(){
            // Set border layout for the cart panel
            setLayout(new BorderLayout());

            // Set the panel preferred size
            this.setPreferredSize(PANEL_SIZE);

            // Set the panel border
            this.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        }//end setupStorePanel

        // Event listener for all cart panel events
        private class CartPanelListener implements ActionListener{

            // If a button was pressed
            @Override
            public void actionPerformed(ActionEvent e){
                if(e.getSource() == clearAllBtn || e.getActionCommand().toLowerCase().equals("clear cart"))
                    clearAllButtonHandler();
                else if(e.getSource() == removeItemBtn || e.getActionCommand().toLowerCase().equals("remove item"))
                    removeItemButtonHandler();
                else if(e.getSource() == checkOutBtn || e.getActionCommand().toLowerCase().equals("check out"))
                    checkOutButtonHandler();
            }//end actionPerformed

            // Helper - Clear all button
            public void clearAllButtonHandler(){
                clearCart();
            }//end clearAllButtonHandler

            // Helper - Remove item button
            private void removeItemButtonHandler(){
                // If a book is selected
                if(shoppingCart.getSelectedValue() != null) {
                    // Get the selected book
                    Book b = shoppingCart.getSelectedValue();

                    // Remove the book from the shopping cart
                    bookData.removeElement(b);

                    // Return the book to the store's stock
                    storePanel.returnItem(b);

                    // Subtract the total from the price
                    subtractTotal(b.getPrice());
                }//end if selected value is not null
            }//end removeItemButtonHandler

            // Helper - Check out button
            private void checkOutButtonHandler() {
                if (!bookData.isEmpty())
                    buyBooks();
            }//end checkoutButtonHandler

        }//end CartPanelListener

    }//end inner class CartPanel

    private class StorePanel extends JPanel{

        // The list of the books the store carries
        private JList<Book> storeList;

        // The list model that stores the data for the book stock list
        private DefaultListModel<Book> bookData;

        // The "Add to Cart" button
        private JButton addToCartBtn;

        // The drop down list for the quantity of books to buy
        private JComboBox<String> quantityBox;

        // The quantity label for the quantity combo box
        private JLabel quantityLabel;

        // The listener for all events on this panel
        private StorePanelListener listener;

        // The ScrollPane holding the store list panel
        private JScrollPane storeListScrollPane;

        // The Panel holding the Store's JList
        private JPanel storeListPanel;

        // The Panel holding the Store's controls
        private JPanel storeControlPanel;

        // The panel holding the quantity label and the quantity combobox
        private JPanel storeQuantityPanel;

        // Constructs a new StorePanel
        public StorePanel(){

            // Create the store list panel and use BorderLayout
            storeListPanel = new JPanel(new BorderLayout());

            // Create the store control panel and use default FlowLayout
            storeControlPanel = new JPanel();

            storeQuantityPanel = new JPanel();

            // Create the event listener for everything in the store panel
            listener = new StorePanelListener();

            // Set all the panel properties
            setupStorePanel();

            // Set all the list properties
            setupStoreListPanel();

            // Set all the button and combo box properties
            setupStoreControlPanel();

            // Add the store list panel to the center
            add(storeListScrollPane, BorderLayout.CENTER);

            // Add the store control panel to the bottom
            add(storeControlPanel, BorderLayout.SOUTH);

        }//end StorePanel

        // Returns an item to the store's stock
        public void returnItem(Book b){
            // Get a reference to the stock book
            Book stock = bookData.elementAt(bookData.indexOf(b));

            // "Return" the item by incrementing the quantity
            stock.setQuantity(b.getQuantity() + 1);

            // Update the quantity combo box
            updateQuantityComboBox();
        }//end returnItem

        // Helper - Set the panel properties
        private void setupStorePanel(){
            // Set the layout of the store panel to border layout
            setLayout(new BorderLayout());

            // Set the panel preferred size
            this.setPreferredSize(PANEL_SIZE);

            // Set the border for the panel
            this.setBorder(BorderFactory.createTitledBorder("Bookstore"));
        }//end setupStorePanel

        // Helper - Setup the store list panel
        private void setupStoreListPanel(){
            // Create the data model that will hold the book data
            bookData = new DefaultListModel<>();

            // Create the JList to hold the books
            storeList = new JList<>(bookData);

            // Add the store list panel to the scroll pane
            storeListScrollPane = new JScrollPane(storeListPanel);

            // Setup single selection mode
            storeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

            // One item per line
            storeList.setLayoutOrientation(JList.VERTICAL);

            // Setup the listFont for this panel
            storeList.setFont(listFont);

            // Set the the background color for the list
            storeList.setBackground(listColor);

            // Set the list text color
            storeList.setForeground(listTextColor);

            // Add action listener for list selection event
            storeList.addListSelectionListener(listener);

            // Add the store list to the store list panel
            storeListPanel.add(storeList);

            // Set scroll bar policies
            storeListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            storeListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        }//end setupStoreListPanel

        // Helper - Setup the store control panel
        private void setupStoreControlPanel(){

            // Set layout to a grid layout
            storeControlPanel.setLayout(new GridLayout(0,2));

            // Create the add to cart button
            addToCartBtn = new JButton("Add to Cart");

            // Create the label for the combo box
            quantityLabel = new JLabel("Quantity");

            // Create the drop down list for the book quantity
            quantityBox = new JComboBox<>();

            // Add action listener to the button
            addToCartBtn.addActionListener(listener);

            // Add action listener to the combo box
            quantityBox.addActionListener(listener);

            // Setup the quantity combo box by setting the selected
            // valued books on hand
            if(storeList.getSelectedValue() != null)
                updateQuantityComboBox();

            // Set the font for all the components
            addToCartBtn.setFont(componentFont);
            quantityLabel.setFont(componentFont);
            quantityBox.setFont(componentFont);

            // Add the components to the quantity panel
            storeQuantityPanel.add(quantityLabel);
            storeQuantityPanel.add(quantityBox);

            // Add the buttons to the store control panel
            storeControlPanel.add(addToCartBtn);
            storeControlPanel.add(storeQuantityPanel);

            // Create a border for the book store controls
            storeControlPanel.setBorder(BorderFactory.createTitledBorder("Bookstore Controls"));

            storeControlPanel.setPreferredSize(CONTROL_PANEL_SIZE);
        }//end setupStoreControlPanel

        // Helper - Updates the combo box to reflect the on-hand quantity
        // of the currently selected book
        private void updateQuantityComboBox(){

            // If the currently selected value in the stock list is not null
            if(storeList.getSelectedValue() != null){
                // Then update the quantity held by the combo box

                // Get the quantity of books for the selected value
                int quantity = storeList.getSelectedValue().getQuantity();

                // Setup a model to store the each valid number from [1,quantity]
                DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<>();

                // If the quantity is not zero
                if(quantity != 0)
                    // Add each element to the combo box
                    for(Integer i = 1; i <= quantity; i++)
                        dcm.addElement(i.toString());
                // Else just add zero to the list
                else
                    dcm.addElement(new Integer(0).toString());

                // Set the data model for the combo box
                quantityBox.setModel(dcm);
            }//end if
        }//end updateQuantityComboBox

        // Helper - Clears the book store stock list
        private void clearStock(){
            for(int i = bookData.size() - 1; i >= 0; i--)
                bookData.removeElement(bookData.elementAt(i));

        }//end clearStock

        // Inner class for StorePanel events
        private class StorePanelListener implements ActionListener, ListSelectionListener {

            // If a standard event was fired
            @Override
            public void actionPerformed(ActionEvent e) {
                // If the event is "Add to Cart"
                if(e.getSource() == addToCartBtn || e.getActionCommand().toLowerCase().equals("add to cart"))
                    addToCartButtonHandler();
                else if(e.getActionCommand().toLowerCase().equals("open database"))
                    openDatabaseButtonHandler();
            }//end actionPerformed

            // If a list item is selected
            @Override
            public void valueChanged(ListSelectionEvent e){
                // If this if statement is not used the press AND the release of
                // the mouse button will trigger the event twice
                if(e.getValueIsAdjusting())
                    listChangedHandler();
            }//end valueChanged

            // Helper - A list item was selected
            private void listChangedHandler(){
                // Update the on-hand book quantity for the combo box
                updateQuantityComboBox();
            }//end listChangedHandler

            // Helper - Add the selected item to the cart
            private void addToCartButtonHandler() {
                // If an item is selected
                if (storeList.getSelectedValue() != null) {

                    // Get the selected quantity for the current book
                    int selectedQuantity = Integer.parseInt((String) quantityBox.getSelectedItem());

                    Book b = storeList.getSelectedValue();

                    // If the quantity is not zero
                    if (selectedQuantity != 0) {
                        // Then add it to the cart

                        // Get a reference for the current book


                        // Add that number of books to the cart
                        for (int i = 0; i < selectedQuantity; i++)
                            cartPanel.addToCart(b);

                        // Update the quantity of the book
                        b.setQuantity(b.getQuantity() - selectedQuantity);

                        // Update the quantity shown in the combo box
                        updateQuantityComboBox();
                    }//end if selected quantity is not zero
                    else
                        outputMessage("\"" + b.getName() + "\" is not in stock.", "Out of Stock");

                }//end store list selected value is not null

            }//end addToCartButtonHandler

            // Helper - Opens a database
            private void openDatabaseButtonHandler(){
                // Create the file chooser
                JFileChooser chooser = new JFileChooser();

                // Start it at a convenient working directory
                File workingDirectory = new File(System.getProperty("user.dir"));

                // Set the working directory
                chooser.setCurrentDirectory(workingDirectory);

                // Save the user return value
                int returnVal = chooser.showOpenDialog(null);

                // If the user approved
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    // Then get the selected file
                    databaseFile = chooser.getSelectedFile();

                    cartPanel.clearCart();

                    // Clear the stock
                    clearStock();

                    // And load in the file
                    loadDatabase();
                }
            }//end openDatabaseButtonHandler

        }//end inner inner class StorePanelListener

    }//end inner class StorePanel

}//end class Bookstore
