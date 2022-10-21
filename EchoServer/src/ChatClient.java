
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF clientUI;
    String selectedUser = "";
    TicTacToe ticTacToe = new TicTacToe();

    //Constructors ****************************************************
    /**
     * Constructs an instance of the chat client.
     *
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */
    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        // openConnection();
    }

    //Instance methods ************************************************
    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    @Override
    public void handleMessageFromServer(Object msg) {
        if (msg instanceof Envelope) {
            Envelope env = (Envelope) msg;
            handleCommandFromServer(env);
        } else {
            clientUI.display(msg.toString());
        }

    }

    public void handleCommandFromServer(Envelope env) {
        if (env.getId().equals("who")) {
            ArrayList<String> userList = (ArrayList<String>) env.getContents();
            String room = env.getArg();
            clientUI.displayUserList(userList, room);
        }

        if (env.getId().equals("userstatus")) {
            ArrayList<String> userList = (ArrayList<String>) env.getContents();
            String room = env.getArg();
            clientUI.display("All Users Online And Their Groups");
            for (String s : userList) {
                clientUI.display(s);
            }
        }
    }

    public void handleMessageFromAdmin(String message) {

        try {
            openConnection();
            Envelope env = new Envelope("login", message, "<ADMIN>");
            this.sendToServer(env);
            //  sendToServer(message);

        } catch (IOException e) {
            clientUI.display("Could not send message to users from ADMIN.......");
            quit();
        }
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {

        if (message.charAt(0) == '#') {

            handleClientCommand(message);

        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display("Could not send message to server.  Terminating client.......");
                quit();
            }
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public void connectionClosed() {

        System.out.println("Connection closed");

    }

    protected void connectionException(Exception exception) {

        System.out.println("Server has shut down");

    }

    public void handleClientCommand(String message) {

        if (message.equals("#quit")) {
            clientUI.display("Shutting Down Client");
            quit();

        }

        if (message.equals("#logoff")) {
            clientUI.display("Disconnecting from server");
            try {
                closeConnection();
            } catch (IOException e) {
            };

        }

        if (message.indexOf("#setHost") >= 0) {

            if (isConnected()) {
                clientUI.display("Cannot change host while connected");
            } else {
                setHost(message.substring(8, message.length()).trim());
            }

        }

        if (message.indexOf("#setPort") >= 0) {

            if (isConnected()) {
                clientUI.display("Cannot change port while connected");
            } else {
                setPort(Integer.parseInt(message.substring(8, message.length()).trim()));
            }

        }

        if (message.indexOf("#login") == 0) {

            if (isConnected()) {
                clientUI.display("already connected");
            } else {

                try {
                    String userName = message.substring(6, message.length()).trim();
                    openConnection();
                    clientUI.display("Logged in as " + userName);
                    Envelope env = new Envelope("login", "", userName);
                    this.sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("failed to connect to server.");
                }
            }
        }

        if (message.indexOf("#join") == 0) {
            try {
                String roomName = message.substring(5, message.length()).trim();
                Envelope env = new Envelope("join", "", roomName);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Join Room.");
            }
        }

        if (message.indexOf("#pm") == 0) {
            try {
                //ex: #pm Bob Hello how are you?
                String targetAndMessage = message.substring(3, message.length()).trim();

                //ex: Bob Hello how are you?
                String target = targetAndMessage.substring(0, message.indexOf(" ")).trim();
                String pm = targetAndMessage.substring(message.indexOf(" "), targetAndMessage.length()).trim();
                Envelope env = new Envelope("pm", target, pm);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Send Private Message.");
            }
        }

        if (message.indexOf("#yell") == 0) {
            try {

                String yellMessage = message.substring(5, message.length()).trim();

                Envelope env = new Envelope("yell", "", yellMessage);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Send Yell Message.");
            }
        }

        if (message.indexOf("#who") == 0) {
            try {
                Envelope env = new Envelope("who", "", "");
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to get User List");
            }
        }

        if (message.indexOf("#ison") == 0) {
            try {
                String isonUser = message.substring(5, message.length()).trim();
                Envelope env = new Envelope("ison", "", isonUser);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Find User Information");
            }
        }

        if (message.indexOf("#userstatus") == 0) {
            try {
                Envelope env = new Envelope("userstatus", "", "");
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Retreve a list of all users");
            }
        }

        //----------------------------------- TIC TAC TOE COMMANDS --------------------------------------------------
        // added command for tic tac toe to store username seperately
        if (message.indexOf("#selectedUser") == 0) {
            String targetAndMessage = message.substring(13, message.length()).trim();
            selectedUser = targetAndMessage;
        }
        // command for inviting the tic tac toe game
        if (message.contentEquals("#ttt")) {
            try {
                Envelope env = new Envelope("ttt", selectedUser, ticTacToe);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Send Invitation to Player 2");
            }
        }
        // command for rejecting the tic tac toe game
        if (message.indexOf("#tttDecline") == 0) {
            try {
                Envelope env = new Envelope("tttDecline", "", ticTacToe);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Decline Invitation");
            }
        }
        // command for accepting the tic tac toe game
        if (message.indexOf("#tttAccept") == 0) {
            try {
                Envelope env = new Envelope("tttAccept", "", ticTacToe);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("failed to Accept Invitation");
            }
        }

    }

}
//End of ChatClient class
