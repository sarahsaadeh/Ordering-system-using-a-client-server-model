
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Client {

    int SERVICE_PORT = 1200;
    InetAddress address = null;
    String choice;
    JFrame f;
    String[] LoginInformation = new String[2];
    Socket socket;
    String LoginInfo;
    BufferedReader reader;
    PrintWriter pWriter;
    BufferedReader server_reader;

    public Client() {
        try {
            address = InetAddress.getByName("127.0.0.1");
            socket = new Socket(address, SERVICE_PORT);
            pWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            f = new JFrame();
            JOptionPane.showMessageDialog(f, "Welcome to our Ordering System");
            choice = JOptionPane.showInputDialog("1- Log in / 2-Sign up (Enter 1 or 2)");
            reader = new BufferedReader(new StringReader(choice));
            pWriter.print(choice);
            
            for (;;) {
                if (Integer.valueOf(choice)==1) {
                    //LOG IN
                    String choice2 = JOptionPane.showInputDialog("1- Admin / 2-Customer (Enter 1 or 2)");
                    if (Integer.valueOf(choice2)==1) {
                        //Admin
                        LoginInfo = Login();
                        LoginInformation = LoginInfo.split(" ");
                        // send log in information to server
                        reader = new BufferedReader(new StringReader(choice2 + " " + LoginInformation[0] + " " + LoginInformation[1]));
                        pWriter.print(choice2 + " " + LoginInformation[0] + " " + LoginInformation[1]);
                        //return variable from the server

                        String numberSERVER = server_reader.readLine();
                        CheckLogIN_A(numberSERVER);
                    } else {
                        LoginInfo = Login();
                        LoginInformation = LoginInfo.split(" ");
                        reader = new BufferedReader(new StringReader(choice2 + " " + LoginInformation[0] + " " + LoginInformation[1]));
                        pWriter.print(choice2 + " " + LoginInformation[0] + " " + LoginInformation[1]);
                        // waiting for log in succeed response from server
                        //return variable from the server 
                        String numberSERVER = server_reader.readLine();
                        CheckLogIN_C(numberSERVER);
                    }

                    break;
                } else if (Integer.valueOf(choice)==2) {
                    //SIGN UP
                    String choice2 = JOptionPane.showInputDialog("1- Admin / 2-Customer (Enter 1 or 2)");
                    reader = new BufferedReader(new StringReader(choice2));
                    pWriter.print(choice2);
                    if (choice2.equals(1)) {
                        //Admin
                        //return code from the server 
                        String codeSERVER = server_reader.readLine();
                        CheckSignUP_A(codeSERVER);
                    } else { // if choice2 is 2 "customer"
                        CheckSignUP_C();
                    }
                    break;
                } else {
                    System.out.println("Error: please choose one of the options");
                    choice = JOptionPane.showInputDialog("1- Log in / 2-Sign up (Enter 1 or 2)");
                }
            }
        } catch (IOException ioe) {
            System.err.println("IO/ERROR " + ioe);
        }
    }

    public String Login() {
        JFrame f;
        f = new JFrame();

        String u = null;
        String p = null;
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Username", SwingConstants.RIGHT));
        label.add(new JLabel("Password", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField username = new JTextField();
        controls.add(username);
        JPasswordField password = new JPasswordField();
        controls.add(password);
        panel.add(controls, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(f, panel, "login", JOptionPane.QUESTION_MESSAGE);
        u = username.getText();
        p = new String(password.getPassword());
        return u + " " + p;
    }

    public void CheckLogIN_A(String num) {
        try {
            server_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                if (Integer.valueOf(num)==1) {
                    JOptionPane.showMessageDialog(f, "You're successfully logged in as an Admin!");
                    String operation = JOptionPane.showInputDialog("Menu: 1-Add Item  2-Remove Item  3-Modify Item");
                    reader = new BufferedReader(new StringReader(operation));
                    pWriter.print(operation);
                } else if (Integer.valueOf(num)==0) {
                    JOptionPane.showMessageDialog(f, "The enterd password is wrong!!!, try again.");
                    LoginInfo = Login();
                    LoginInformation = LoginInfo.split(" ");
                    reader = new BufferedReader(new StringReader("1" + " " + LoginInformation[0] + " " + LoginInformation[1]));
                    pWriter.print("1" + " " + LoginInformation[0] + " " + LoginInformation[1]);
                    //return variable from the server 
                    String num2 = server_reader.readLine();
                    CheckLogIN_A(num2);
                } else {
                    JOptionPane.showMessageDialog(f, "Username not found!!!, try again.");
                    LoginInfo = Login();
                    LoginInformation = LoginInfo.split(" ");
                    reader = new BufferedReader(new StringReader("1" + " " + LoginInformation[0] + " " + LoginInformation[1]));
                    pWriter.print("1" + " " + LoginInformation[0] + " " + LoginInformation[1]);
                    //return variable from the server 
                    String num2 = server_reader.readLine();
                    CheckLogIN_A(num2);
                }
                break;
            }
        } catch (IOException ioe) {
            System.err.println("IO/ERROR " + ioe);
        }
    }

    public void CheckLogIN_C(String num) {
        try {
            while (true) {
                if (Integer.valueOf(num)==1) {
                    JOptionPane.showMessageDialog(f, "You're successfully logged in as a Customer!");
                    String operation = JOptionPane.showInputDialog("Menu: 1-Make new order  2-List previous orders  3-Exit");
                    reader = new BufferedReader(new StringReader(operation));
                    pWriter.print(operation);

                    if (Integer.valueOf(operation)==1) {
                        //make new order
                        //waiting the server display items
                        String resp = server_reader.readLine();
                        if (Integer.valueOf(resp)==1) {
                            String code = JOptionPane.showInputDialog("Enter the code of the item:");
                            reader = new BufferedReader(new StringReader(code));
                            pWriter.print(code);

                        }

                        // waiting response from the server that making order is done 
                        String resp1 = server_reader.readLine();
                        if (Integer.valueOf(resp1)==1) {
                            //Ask the user if he want to add more orders 
                            String moreOrders = JOptionPane.showInputDialog("Do you want to add more orders? 1-Yes  2-No");
                            reader = new BufferedReader(new StringReader(moreOrders));
                            pWriter.print(moreOrders);

                            //show menu again
                            String operation1 = JOptionPane.showInputDialog("Menu: 1-Make new order  2-List previous orders  3-Exit");
                            reader = new BufferedReader(new StringReader(operation1));
                            pWriter.print(operation1);

                        }

                    } else if (Integer.valueOf(operation)==2) {
                        //list previous orders
                        //waiting flag from server that listing is done
                        String flag = server_reader.readLine();
                        //ask if the user want to Reorder
                        String choice = JOptionPane.showInputDialog(" 1-Reorder 2-Show order");
                        if (Integer.valueOf(choice)==1) {
                            String Reorder = JOptionPane.showInputDialog("Enter the code of the order you want to Reorder");
                            //sending the code to server
                            reader = new BufferedReader(new StringReader(Reorder));
                            pWriter.print(Reorder);
                            //recieve flag from server to make input dialogue
                            server_reader.readLine();
                            String ReorderChoice = JOptionPane.showInputDialog("Do you want to modify the order : 1-Add items 2-remove items 3-finish the order");
                            //send the choice to server
                            reader = new BufferedReader(new StringReader(ReorderChoice));
                            pWriter.print(ReorderChoice);
                        } else if (Integer.valueOf(choice)==2) {
                            //Show order
                        } else {
                            //show menu again
                            String operation1 = JOptionPane.showInputDialog("Menu: 1-Make new order  2-List previous orders  3-Exit");
                            reader = new BufferedReader(new StringReader(operation1));
                            pWriter.print(operation1);
                        }

                    } else {

                    }
                } else if (Integer.valueOf(num)==0) {
                    JOptionPane.showMessageDialog(f, "The enterd password is wrong!!!, try again.");
                    LoginInfo = Login();
                    LoginInformation = LoginInfo.split(" ");
                    reader = new BufferedReader(new StringReader("2" + " " + LoginInformation[0] + " " + LoginInformation[1]));
                    pWriter.print("2" + " " + LoginInformation[0] + " " + LoginInformation[1]);
                    //return variable from the server 
                    String num2 = server_reader.readLine();
                    CheckLogIN_C(num2);
                } else {
                    JOptionPane.showMessageDialog(f, "Username not found!!!, try again.");
                    LoginInfo = Login();
                    LoginInformation = LoginInfo.split(" ");
                    reader = new BufferedReader(new StringReader("2" + " " + LoginInformation[0] + " " + LoginInformation[1]));
                    pWriter.print("2" + " " + LoginInformation[0] + " " + LoginInformation[1]);
                    //return variable from the server 
                    String num2 = server_reader.readLine();
                    CheckLogIN_C(num2);
                }
                break;
            }
        } catch (IOException ioe) {
            System.err.println("IO/ERROR " + ioe);
        }

    }

    public void CheckSignUP_A(String num) {
        try {
            while (true) {
                JOptionPane.showMessageDialog(f, "Your Admin Sign Up Code is: " + num);
                String code = JOptionPane.showInputDialog("Enter Your Code:");
                reader = new BufferedReader(new StringReader(code));
                pWriter.print(code);
                //waiting response from the server
                String response = server_reader.readLine();
                if (Integer.valueOf(response)==1) {
                    //code match
                    String UserName = JOptionPane.showInputDialog("Enter your username please:");
                    reader = new BufferedReader(new StringReader(UserName));
                    pWriter.print(UserName);
                    //waiting response from the server that username is valid
                    String flag = server_reader.readLine();
                    if (Integer.valueOf(flag)==1) {
                        // valid username
                        String PassWord = JOptionPane.showInputDialog("Enter your password please:");
                        reader = new BufferedReader(new StringReader(PassWord));
                        pWriter.print(PassWord);
                    } else {// server flag is zero
                        JOptionPane.showMessageDialog(f, "The username is already taken!!!, try again. ");
                        continue;
                    }
                } else { //server response is 0
                    JOptionPane.showMessageDialog(f, "The eneterd code is wrong!!!, try again. ");
                    continue;
                }
                break;
            }
        } catch (IOException ioe) {
            System.err.println("IO/ERROR " + ioe);
        }
    }

    public void CheckSignUP_C() {
        try {
            while (true) {
                String UserName = JOptionPane.showInputDialog("Enter your username please:");
                reader = new BufferedReader(new StringReader(UserName));
                pWriter.print(UserName);
                //waiting response from the server that user name is valid
                String flag = server_reader.readLine();
                if (Integer.valueOf(flag)==1) { // username is valid
                    String PassWord = JOptionPane.showInputDialog("Enter your password please:");
                    reader = new BufferedReader(new StringReader(PassWord));
                    pWriter.print(PassWord);
                } else { //server flag is 0
                    JOptionPane.showMessageDialog(f, "The username is already taken!!!, try again. ");
                    continue;
                }
                break;
            }
        } catch (IOException ioe) {
            System.err.println("IO/ERROR " + ioe);
        }
    }
}
