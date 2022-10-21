
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author macbookpro2017
 */
public class GUIConsole extends JFrame implements ChatIF {

    final public static int DEFAULT_PORT = 5555;
    ChatClient client;
    
    private JButton closeB = new JButton("Logoff");
    private JButton loginB = new JButton("Login");
    private JButton sendB = new JButton("Send");
    private JButton quitB = new JButton("Quit");
    private JButton ticTacToeB = new JButton("Tic Tac Toe");
    private JButton openB = new JButton("Refresh List");
    
    
    public JComboBox whoCB = new JComboBox();

    private JTextField portTxF = new JTextField("5555");
    private JTextField hostTxF = new JTextField("127.0.0.1");
    private JTextField messageTxF = new JTextField("");
    private JTextField userTxF = new JTextField("");

    private JLabel portLB = new JLabel("Port: ", JLabel.RIGHT);
    private JLabel hostLB = new JLabel("Host: ", JLabel.RIGHT);
    private JLabel messageLB = new JLabel("Message: ", JLabel.RIGHT);
    private JLabel userListLB = new JLabel("User List: ", JLabel.RIGHT);

    private JTextArea messageList = new JTextArea();

    public GUIConsole(String host, int port, String userId) {
        super("Simple Chat GIU");
        setSize(300, 400);

        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        add("Center", messageList);
        add("South", bottom);

        bottom.setLayout(new GridLayout(8, 2, 5, 5));
        bottom.add(hostLB);
        bottom.add(hostTxF);
        bottom.add(portLB);
        bottom.add(portTxF);
        bottom.add(messageLB);
        bottom.add(messageTxF);
        bottom.add(messageTxF);
        bottom.add(userListLB);
        bottom.add(whoCB);
        
        bottom.add(openB);
        bottom.add(sendB);
        
        bottom.add(ticTacToeB);
        bottom.add(closeB);
        bottom.add(loginB);
        bottom.add(userTxF);
        bottom.add(quitB);

        portTxF.setText(port + "");
        hostTxF.setText(host + "");
        userTxF.setText(userId + "");

        setVisible(true);
        sendB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send(messageTxF.getText());
            }
        });

        loginB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login(hostTxF.getText(), portTxF.getText(), userTxF.getText());
            }
        });

        closeB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        quitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        openB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              send("#who"); //refresh userlist
            }
        });
        
         ticTacToeB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {            
                    send("#selectedUser"+whoCB.getSelectedItem()); //get selected users
                    send("#ttt"); //sent invitation
            }
        });

        try {
            client = new ChatClient(host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection!!!!"
                    + " Terminating client.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String host = "";
        int port = 0;  //The port number
        String userId = "";

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            host = "localhost";
            port = DEFAULT_PORT;
        }
        try {
            userId = args[2];
        } catch (Exception e) {
            userId = "Guest";
        }
        GUIConsole console = new GUIConsole(host, port, userId);
    }

    @Override
    public void display(String message) {
        messageList.insert(message + " \n", 0);
    }

    public void send(String message) {
        client.handleMessageFromClientUI(message);
    }

    private void login(String host, String port, String userId) {
        client.handleMessageFromClientUI("#setHost " + host);
        client.handleMessageFromClientUI("#setPort " + port);
        client.handleMessageFromClientUI("#login " + userId);
        client.handleMessageFromClientUI("#who");
    }

    public void close() {
        client.handleMessageFromClientUI("#logoff");
    }
    
    public void displayUserList(ArrayList<String> userList, String room){
        whoCB.removeAllItems();
        for(String user : userList){
            whoCB.addItem(user);
        }
    }
    
    

}
