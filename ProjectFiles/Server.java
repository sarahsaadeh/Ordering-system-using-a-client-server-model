
import java.util.*;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.lang.*;
import javax.swing.JFrame;

class serveClient extends Thread {

    ArrayList<String>[][] AdminInfo = new ArrayList[100][2];
    ArrayList[][] CustomerInfo = new ArrayList[100][2];
    ArrayList[][] CustomerOrders;
    ArrayList<Item> items;
    ArrayList<Order> Order;
    ServerSocket server;
    Socket mySocket;
    BufferedReader reader;
    PrintWriter pWriter;
    BufferedReader client_reader;
    int code;
    String Username;
    String password;
    FileWriter Fwriter;
    JFrame f = new JFrame();

    public serveClient(Socket s) {
        mySocket = s;
    }

    @Override
    public void run() {
        //deserialize the array list 'item.out'
        items = new ArrayList<>();
        try {
            FileInputStream fin = new FileInputStream("items.out");
            // Connect an object input stream to the list
            ObjectInputStream oin = new ObjectInputStream(fin);
            Object obj = oin.readObject();
            items = (ArrayList) obj;

        } catch (ClassCastException | ClassNotFoundException cce) {
            // Can't read it, create a blank one
            items = new ArrayList();
        } // Can't read it, create a blank one
        //fin.close();
        catch (FileNotFoundException fnfe) {
            // Create a blank vector
            items = new ArrayList();
        } catch (IOException ioe) {
            System.out.println("");
        }
        while (true) {

            try {

                pWriter = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));
                client_reader = new BufferedReader(new StringReader(mySocket.getInputStream().toString()));
                System.out.println("Connection accepted...");
                //read if 1 login if 2 sign up
                String Log_Sign = client_reader.readLine();
                if (Integer.valueOf(Log_Sign) == 1) {
                    //login read from client 
                    String LogIn_info = client_reader.readLine();
                    String[] LogIn_info1 = LogIn_info.split(" ");
                    if (LogIn_info1[0].equals(1)) {
                        //Admin
                        for (int i = 0; i < 100; i++) {
                            //Check UserName
                            if (LogIn_info1[1].equals(AdminInfo[i][0])) {
                                //if username found check password
                                if (LogIn_info1[2].equals(AdminInfo[i][1])) {
                                    //password match 'return 1 means log in successful'
                                    reader = new BufferedReader(new StringReader("1"));
                                    pWriter.print("1");
                                    //waiting for operation num
                                    String operation = client_reader.readLine();
                                    AdminService(operation, LogIn_info1[1]);

                                } else {
                                    //password doesn't match "return 0"
                                    reader = new BufferedReader(new StringReader("0"));
                                    pWriter.print("0");

                                }

                            } else {
                                //UserName Not Found "return -1"
                                reader = new BufferedReader(new StringReader("-1"));
                                pWriter.println("-1");

                            }
                        }
                    } else {
                        //Customer login
                        for (int i = 0; i < 100; i++) {
                            //Check UserName
                            if (LogIn_info1[1].equals(CustomerInfo[i][0])) {
                                //if username found check password 
                                if (LogIn_info1[2].equals(CustomerInfo[i][1])) {
                                    //password match 'return 1 means log in successful'
                                    reader = new BufferedReader(new StringReader("1"));
                                    pWriter.print("1");
                                    //waiting for operation num
                                    String operation = client_reader.readLine();
                                    CustomerService(operation, LogIn_info1[1]);

                                } else {
                                    //password doesn't match "return 0"
                                    reader = new BufferedReader(new StringReader("0"));
                                    pWriter.println("0");
                                }

                            } else {
                                //UserName Not Found "return -1"
                                reader = new BufferedReader(new StringReader("-1"));
                                pWriter.println("-1");
                            }
                        }
                    }
                } else { //2   SignUp
                    //see if admin or customer
                    String AorC = client_reader.readLine();
                    if (Integer.valueOf(AorC) == 1) {
                        // Admin sign up
                        Random rand = new Random();
                        code = rand.nextInt(1000);

                        reader = new BufferedReader(new StringReader(String.valueOf(code)));
                        //check for entered code from client
                        String returned_code = client_reader.readLine();
                        if (String.valueOf(code).equals(returned_code)) {
                            //code match send 1
                            reader = new BufferedReader(new StringReader("1"));
                            pWriter.print("1");
                            //receive username from client
                            Username = client_reader.readLine();
                            //check that recieved username is valid in the Array AdminInfo
                            for (int i = 0; i < 100; i++) {
                                //Check UserName
                                if (Username.equals(AdminInfo[i][0])) {
                                    //username is taken!! send flag zero
                                    reader = new BufferedReader(new StringReader("0"));
                                    pWriter.print("0");

                                } else {
                                    //save the username in the array
                                    AdminInfo[i][0].add(Username);
                                    //username is valid, send flag true
                                    reader = new BufferedReader(new StringReader("1"));
                                    pWriter.print("1");
                                    //receive password from client
                                    password = client_reader.readLine();
                                    //save password in the array
                                    AdminInfo[i][1].add(password);

                                }
                                SaveInfo();

                            }

                        }

                    } else {
                        //customer
                        //receive username from client
                        Username = client_reader.readLine();
                       CustomerInfo[0][0].add("Yara");
                       CustomerInfo[0][1].add("123");
                        //check that recieved username is valid in the Array CustomerInfo
                        for (int i = 0; i < 100; i++) {
                            //Check UserName
                            if (Username.equals(CustomerInfo[i][0])) {
                                //username is taken!! send flag zero
                                reader = new BufferedReader(new StringReader("0"));
                                pWriter.print("0");

                            } else {
                                //save the username in the array
                                CustomerInfo[i][0].add(Username);
                                //username is valid, send flag true
                                reader = new BufferedReader(new StringReader("1"));
                                pWriter.print("1");
                                //receive password from client
                                password = client_reader.readLine();
                                //save password in the array
                                CustomerInfo[i][1].add(password);

                            }
                            SaveInfo();

                        }

                    }

                }
            } catch (IOException ioe) {
                System.out.println("");
            }
        }
    }

    public void CustomerService(String operation, String UserName) {
        try {
            Order = null;
            if (Integer.valueOf(operation) == 1) {
                //make new order
                //display the items list
                JOptionPane.showMessageDialog(f, items);
                //Sending flag that server displayed items
                reader = new BufferedReader(new StringReader("1"));
                pWriter.print("1");
                for (int i = 0; i < 100; i++) {
                    //Check UserName
                    if (UserName.equals(CustomerOrders[i][0])) {
                        //User exist in the array
                        int code = client_reader.read();
                        Order order = new Order(code);
                        //adding the client order into his ArrayList order
                        Order.add(order);
                        CustomerOrders[i][0].add(Order);
                        //Sending flag to client that order is done.
                        reader = new BufferedReader(new StringReader("1"));
                        pWriter.print("1");
                        //check if customer want to add more orders 
                        String moreOrders = client_reader.readLine();
                        if (Integer.valueOf(moreOrders) == 1) {
                            int code1 = client_reader.read();
                            Order order1 = new Order(code1);
                            //adding the client order into his ArrayList order
                            Order.add(order1);
                            CustomerOrders[i][0].add(Order);
                            //Sending flag to client that order is done.
                            reader = new BufferedReader(new StringReader("1"));
                            pWriter.print("1");
                        }

                    } else if (Integer.valueOf(operation) == 2) {
                        //listing previous order
                        for (int j = 0; j < 100; j++) {
                            //Check UserName
                            if (UserName.equals(CustomerOrders[j][0])) {
                                //User exist in the array
                                //Display pervious order for the user 
                                Order o = (Order) CustomerOrders[j][1].get(j);
                                JOptionPane.showMessageDialog(f, o.toString());
                                //Sending flag to client that displaying previous orders is done.
                                reader = new BufferedReader(new StringReader("2"));
                                pWriter.print("2");
                                //recieve 1-Reorder 2-Showorder
                                String Prev_options = client_reader.readLine();
                                if (Integer.valueOf(Prev_options) == 1) {
                                    //1-Reorder
                                    //recieve from user the Code of order he want to Reorder again
                                    String ReorderCode = client_reader.readLine();
                                    for (int k = 0; k < Order.size(); k++) {

                                        if (Integer.valueOf(ReorderCode) == Order.get(k).getCode()) {//if code match >> Reorder
                                            Order newOrder = new Order(Order.get(k).getCode());
                                            //ask the customer whether to add items or remove items or finish
                                            //send flag to client to show input dialogue
                                            reader = new BufferedReader(new StringReader("make input dialogue"));
                                            pWriter.print("make input dialogue");
                                            //recive the Reorder modify choice
                                            String ReorderChoice = client_reader.readLine();
                                            if (Integer.valueOf(ReorderChoice) == 1) {
                                                //Add items to the order
                                                //display the items list
                                                JOptionPane.showMessageDialog(f, items);
                                                String new_item = JOptionPane.showInputDialog("Enter the ID of item you want to add");
                                                int new_itemID = Integer.valueOf(new_item);
                                                //loop through items
                                                for (int a = 0; a < items.size(); a++) {
                                                    if (items.get(a).id == new_itemID) {
                                                        Item t = (Item) items.get(a);
                                                        //add the item t the order
                                                        newOrder.addItem(t);
                                                    }
                                                    //add the order into the arraylist Order 
                                                    Order.add(newOrder);
                                                }
                                            } else if (Integer.valueOf(ReorderChoice) == 2) {
                                                //remove item from the order
                                                //display the order items
                                                String removeItem = JOptionPane.showInputDialog("Enter the ID of item you want to remove " + newOrder.getItems());
                                                int removeItemID = Integer.valueOf(removeItem);
                                                for (int b = 0; b < newOrder.getItems().size(); b++) {
                                                    //loop through the order items
                                                    if (newOrder.getItems().get(b).id == removeItemID) //remove the item from order
                                                    {
                                                        newOrder.getItems().remove(b);
                                                    }
                                                }
                                            } else {
                                                //finish
                                                //display the order
                                                String OrderDisplay = newOrder.toString();
                                                JOptionPane.showMessageDialog(f, OrderDisplay);
                                            }

                                        }
                                    }
                                } else {
                                    //2- Show Order
                                    //show the orders for the user
                                    JOptionPane.showMessageDialog(f, Order);
                                    //the code of Order he want to show
                                    String O_Code = JOptionPane.showInputDialog("enter the code for the order you want to show ");
                                    for (int c = 0; c < Order.size(); c++) {
                                        //loop through orders
                                        if (Order.get(c).code == Integer.valueOf(O_Code)) {
                                            //display the order
                                            JOptionPane.showMessageDialog(f, Order.get(c).getItems() + " " + Order.get(c).orderValue());
                                            while (true) {
                                                //ask the user to add review
                                                String review = JOptionPane.showInputDialog("Do you want to add review to one of the items? 1-yes 2-no");
                                                if (Integer.valueOf(review) == 1) {
                                                    // ask him what item to review
                                                    String reviewItemID = JOptionPane.showInputDialog("Enter the ID of the Item you want to review");
                                                    ArrayList<Item> reviewItems = Order.get(c).getItems();
                                                    for (int d = 0; d < reviewItems.size(); d++) {
                                                        //find the item to review
                                                        if (reviewItems.get(d).id == Integer.valueOf(reviewItemID)) {
                                                            //item is found enter your review
                                                            synchronized (reviewItems.get(d)) {
                                                                reviewItems.get(d).addReview();
                                                            }
                                                        }
                                                    }
                                                    String REVIEW = JOptionPane.showInputDialog("Do you want to add more reviews?? 1-yes 2-no");
                                                    if (Integer.valueOf(REVIEW) == 1) {
                                                        //add more reviews
                                                        continue;

                                                    } else {
                                                        //no more reviews
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                        }
                    } else {
                        //exit
                        JOptionPane.showMessageDialog(f, "Logging Out");
                        mySocket.close();
                        System.exit(0);
                    }
                }
            }

        } catch (IOException ioe) {

        }
    }

    public void AdminService(String operation, String UserName) {

        try {
            FileOutputStream fout = new FileOutputStream("items.out");
            // Construct an object output stream
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            if (Integer.valueOf(operation) == 1) {
                //Add Item
                //recieve item from admin by client
                ObjectInputStream obj = new ObjectInputStream(mySocket.getInputStream());
                Object item = obj.readObject();

                synchronized (item) {
                    //adding the recieved item into the Items list 
                    Item item2 = (Item) item;
                    items.add(item2);

                }

                oout.writeObject(item);
                fout.close();

            } else if (Integer.valueOf(operation) == 2) {
                //remove item
                //recieve ID of item the admin want to remove
                String ID = client_reader.readLine();
                //loop into array of items to find the item

                for (Object i : items) {
                    if (i.toString().contains(ID)) {
                        Item item1 = (Item) i;
                        synchronized (item1) {
                            items.remove(item1);
                        }
                        oout.writeObject(item1);
                        fout.close();

                    }
                }

            } else {
                //modify item
                //modify the price
                //recieve ID of item the admin want to modify
                String ID = client_reader.readLine();
                //recieve the new price from admin
                double price = client_reader.read();
                //loop into array of items to find the item
                for (Object i : items) {
                    if (i.toString().contains(ID)) {
                        Item item1 = (Item) i;
                        synchronized (item1) {
                            item1.setPriceWithoutTax(price);
                        }
                        oout.writeObject(item1);
                        fout.close();

                    }
                }

            }
        } catch (IOException ioe) {
            System.err.print("ERROR: " + ioe);
        } catch (ClassNotFoundException cnf) {
            System.err.print("ERROR: " + cnf);
        }
    }

    public void SaveInfo() {
        //Save Admins information into file
        try {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 100; i++)//for each row
            {
                for (int j = 0; j < 2; j++)//for each column
                {
                    builder.append(AdminInfo[i][j] + "");
                    builder.append("    ");
                }
                builder.append("\n");//append new line at the end of the row
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter("Admins.txt"));
            writer.write(builder.toString());//save the string representation 
            writer.close();
        } catch (IOException ioe) {
            System.err.println("I/O error - " + ioe);

        }
        //Save Customers information into file
        try {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 100; i++)//for each row
            {
                for (int j = 0; j < 2; j++)//for each column
                {
                    builder.append(CustomerInfo[i][j] + "");
                    builder.append("    ");
                }
                builder.append("\n");//append new line at the end of the row
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter("Customers.txt"));
            writer.write(builder.toString());//save the string representation 
            writer.close();
        } catch (IOException ioe) {
            System.err.println("I/O error - " + ioe);

        }

    }

}

public class Server {

    public static final int SERVICE_PORT = 1200;

    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(SERVICE_PORT);

            System.out.println("TCP service started");
            // Loop indefinitely, accepting clients
            for (;;) {
                // Get the next TCP client
                Socket nextClient = server.accept();
                // Display connection details
                System.out.println("Received request from " + nextClient.getInetAddress() + ":" + nextClient.getPort());
                //Create a new thread and pass the created socket to it
                serveClient nextThread = new serveClient(nextClient);
                //launch the thread
                nextThread.start();
            }
        } catch (BindException be) {
            System.err.println("Service already running on port " + SERVICE_PORT);
        } catch (IOException ioe) {
            System.err.println("I/O error - " + ioe);
        }
    }
}
