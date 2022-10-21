
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Constructors ****************************************************
    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {

        super(port);

//    try {
//            
//      this.listen(); //Start listening for connections
//    } 
//    catch (Exception ex) 
//    {
//      System.out.println("ERROR - Could not listen for clients!");
//    }
    }

    //Instance methods ************************************************
    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof Envelope) {
            Envelope env = (Envelope) msg;
            handleCommandFromClient(env, client);
        } else {
            System.out.println("Message received: " + msg + " from " + client);
            String userId = client.getInfo("userid").toString();
            this.sendToAllClientsInRoom(msg, client);
        }

    }

    public void handleCommandFromClient(Envelope env, ConnectionToClient client) {
        if ("login".equals(env.getId())) {
            String userId = env.getContents().toString();
            client.setInfo("userid", userId);
            client.setInfo("room", "lobby");
        }
        if (env.getId().equals("join")) {
            String roomName = env.getContents().toString();
            client.setInfo("room", roomName);
        }
        if (env.getId().equals("pm")) {
            String target = env.getArg();
            String message = env.getContents().toString();
            sendToAClient(message, target, client);
        }
        if (env.getId().equals("yell")) {
            String yell = env.getContents().toString();
            String userId = client.getInfo("userid").toString();
            this.sendToAllClients(userId + " yells: " + yell.toUpperCase());
        }
        if (env.getId().equals("who")) {
            this.sendRoomListToClient(client);
        }
        if (env.getId().equals("ison")) {
            String userId = client.getInfo("userid").toString();
            String userAskingForId = env.getContents().toString();
            this.sendAClientAndRoom(userAskingForId, client);
        }
        if (env.getId().equals("userstatus")) {
            this.retreveListOfAllUsersAndRoom(client);
        }
        //----------------------------------------------------------------------
        // Handles all command for tic tac toe
        if (env.getId().equals("ttt")) {
            String player2 = env.getArg(); //retrive player 2 from envelope
            String player1 = client.getInfo("userid").toString(); //get player 1 from ConnectionToClient
            TicTacToe t = (TicTacToe) env.getContents(); //get tic tac toe object from envelope
            t.setPlayer1(player1); //set player 1 name
            t.setPlayer2(player2);//set player 2 name
            t.setGameState(1); //set game state
            this.processTicTacToe(client, t); //call function handling game
        }

        if (env.getId().equals("tttDecline")) {
            TicTacToe t = (TicTacToe) env.getContents();
            t.setGameState(2); //update game state to call the approprate method for state 2
            this.processTicTacToe(client, t);
        }

        if (env.getId().equals("tttAccept")) {
            TicTacToe t = (TicTacToe) env.getContents();
            t.setGameState(3); //update game state to call the approprate method for state 3
            this.processTicTacToe(client, t);
        }
        //------------------------------------------------------------------

        //HANDLES ADMIN COMMANDS
        if (client.getInfo("userid").toString().equals("<ADMIN>")) {
            env.setContents(client);
            String admin = env.getArg();
            String userId = client.getInfo("userid").toString();

            //commands for Admin
            if (env.getArg().equals("#stop")) {
                stopListening();
            } else if (env.getArg().equals("#start")) {
                try {
                    listen();
                } catch (IOException ex) {
                    System.out.println("ERROR - Could not listen for clients!");
                }
            } else if (env.getArg().equals("#quit")) {
                System.out.println("Server Has Been Shut Down!");
                System.exit(0);
            } else if (env.getArg().contains("#setPort")) {
                try {
                    int newPort = Integer.parseInt(env.getArg().substring(8, env.getArg().length()).trim());
                    EchoServer sv = new EchoServer(newPort);
                    sv.listen();
                    // System.out.print("Port has been updated to "+ getPort());
                } catch (IOException ex) {
                    System.out.println("Opps! Ran into error setting new port - Ensure server is running");
                }
            } else {
                this.sendToAllClients("<ADMIN>: " + admin.toUpperCase());
            }

        }
    }

    //----------------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>----------------------------
    public void sendAClientAndRoom(String e, ConnectionToClient client) {
        ArrayList<String> userList = new ArrayList<String>();
        String room = "";
        Thread[] clientThreadList = getClientConnections();
        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient target = (ConnectionToClient) clientThreadList[i];
            if (target.getInfo("userid").equals(e)) {
                room = target.getInfo("room").toString();
            }
        }
        try {
            if (room.isEmpty()) {
                client.sendToClient(e + " is currently not logged in ");
            } else {
                client.sendToClient(e + " is currently in room " + room);
            }
        } catch (Exception ex) {
            System.out.println("Failed to send UserList to client");
        }
    }

    public void retreveListOfAllUsersAndRoom(ConnectionToClient client) {
        Envelope e = new Envelope();
        e.setId("userstatus");
        ArrayList<String> userList = new ArrayList<String>();
        String room = client.getInfo("room").toString();
        e.setArg(room);
        Thread[] clientThreadList = getClientConnections();

        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient target = (ConnectionToClient) clientThreadList[i];
            if (target.getInfo("userid").toString().equals("<ADMIN>")) {

            } else {
                userList.add(target.getInfo("userid").toString() + " - " + target.getInfo("room").toString());
            }
        }

        e.setContents(userList);
        try {
            client.sendToClient(e);
        } catch (Exception ex) {
            System.out.println("Failed to send UserList to client");
        }
    }

    public void sendRoomListToClient(ConnectionToClient client) {
        Envelope e = new Envelope();
        e.setId("who");
        ArrayList<String> userList = new ArrayList<String>();
        String room = client.getInfo("room").toString();
        e.setArg(room);
        Thread[] clientThreadList = getClientConnections();

        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient target = (ConnectionToClient) clientThreadList[i];
            if (target.getInfo("room").equals(room)) {
                userList.add(target.getInfo("userid").toString());
            }
        }

        e.setContents(userList);
        try {
            client.sendToClient(e);
        } catch (Exception ex) {
            System.out.println("Failed to send UserList to client");
        }
    }

    public void sendToAClient(Object msg, String pmTarget, ConnectionToClient client) {
        Thread[] clientThreadList = getClientConnections();
        try {
            for (int i = 0; i < clientThreadList.length; i++) {
                ConnectionToClient target = (ConnectionToClient) clientThreadList[i];
                if (target.getInfo("userid").equals(pmTarget)) {
                    try {
                        //  target.sendToClient(client.getInfo("userid") + ": " + msg);
                        target.sendToClient(msg);
                    } catch (Exception ex) {
                        System.out.println("Failed to send to Client");
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * stops listening for connections.
     */
    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    //Class methods ***************************************************
    /**
     * This method is responsible for the creation of the server instance (there
     * is no UI in this phase).
     *
     * @param args[0] The port number to listen on. Defaults to 5555 if no
     * argument is entered.
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("<Client Connected:" + client + ">");
    }

    @Override
    synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println("Client has Shutdown");

    }

    public void sendToAllClientsInRoom(Object msg, ConnectionToClient client) {
        Thread[] clientThreadList = getClientConnections();
        String room = client.getInfo("room").toString();
        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient target = (ConnectionToClient) clientThreadList[i];
            if (target.getInfo("room").equals(room)) {
                try {
                    target.sendToClient(client.getInfo("userid") + ": " + msg);
                } catch (Exception ex) {
                    System.out.println("Failed to send to Client");
                }
            }
        }
    }

//-------------------------------------------TIC TAC TOE METHODS -------------------------------------------------------------------------------------  
    //save updated tic tac toe object for both users 
    public void updateTicTacToeUsers(TicTacToe ticTacToe) {
        Thread[] clientThreadList = getClientConnections();
        for (Thread clientThreadList1 : clientThreadList) {
            ConnectionToClient target = (ConnectionToClient) clientThreadList1;
            if (target.getInfo("userid").equals(ticTacToe.getPlayer1())) {
                target.setInfo("ttt", (TicTacToe) ticTacToe); //set Player 1
            }
            if (target.getInfo("userid").equals(ticTacToe.getPlayer2())) {
                target.setInfo("ttt", (TicTacToe) ticTacToe); //set Player 2
            }
        }
    }

    //check who won the game, check movement for game over, handle messages to both players and activate gamestate 4 
    public void moveChecker(TicTacToe ticTacToe, TicTacToeConsole ttt, TicTacToeConsole ttt2, ConnectionToClient client) { 

        if (ticTacToe.getActivePlayer() == 1) { //check if the active player is 1 meaning that is player 1
            ttt.getPlayerName().setText(ticTacToe.getPlayer1()); //sent the text field to player 1 name
            ttt2.getPlayerName().setText("Loading..."); //blank out the player 2 name when sending to player 1
            ticTacToe.checkWin(); //check if the player 1 move is a winner move on the board
            ticTacToe.gameOver(); //check if the player 1 move is the 9th move
        } else { //if the active player is not 1 meaning that is player 2
            ttt.getPlayerName().setText("Loading..."); //blank out the player 1 name when sending to player 2
            ttt2.getPlayerName().setText(ticTacToe.getPlayer2()); //sent the text field to player 1 name
            ticTacToe.checkWin();//check if the player 2 move is a winner move on the board
            ticTacToe.gameOver(); //check if the player 2 move is the 9th move
        }

        if (ticTacToe.getGameState() == 4) { //if the check checkWin() method has found a winner that changes the game state to 4
            if (ticTacToe.getActivePlayer() == 1) { //check the active player is 1 (player 1)
                ttt2.closeGame(); //close the tic tac toe board
                ttt.getTextfield().setText("");
                ttt.getPlayerName().setText("LOOSER!");
                sendToAClient(ticTacToe.getPlayer1() + " You Lost To " + ticTacToe.getPlayer2(), ticTacToe.getPlayer1(), client);//inform player 1 that they lost to player 2               
            } else {//the active player is 2 (player 2)
                ttt.closeGame();//close the tic tac toe board
                ttt2.getPlayerName().setText("LOOSER!");
                ttt2.getTextfield().setText("");
                sendToAClient(ticTacToe.getPlayer2() + " You Lost To " + ticTacToe.getPlayer1(), ticTacToe.getPlayer2(), client);//inform player 1 that they lost to player 1                 
            }
            ticTacToe.setGameState(1); //reset the game state back to 1
        }

    }

    // process method that handles the tic tac toe
    public void processTicTacToe(ConnectionToClient client, TicTacToe ticTacToe) {
        if (ticTacToe.getGameState() == 1) { //handles game invitation state
            updateTicTacToeUsers(ticTacToe); //calls the update method to store the tic tac toe information for both players
            Envelope env = new Envelope("", "", ticTacToe); //save it in the envelope
            String msg = "You are invited to play Tic Tac Toe with: \n" + ticTacToe.getPlayer1() + "\n #tttAccept to Accept, \n #tttDecline to decline";
            sendToAClient(msg, ticTacToe.getPlayer2(), client); //send the invitation to player 2
        }

        if (ticTacToe.getGameState() == 2) { //handles game decline state
            String msg = "Your game was declined ";
            ticTacToe = (TicTacToe) client.getInfo("ttt"); //retrive information saved from state 1
            sendToAClient(msg, ticTacToe.getPlayer1(), client); //send the decline message back to player 1
        }

        if (ticTacToe.getGameState() == 3) { //handles game playing state
            ticTacToe = (TicTacToe) client.getInfo("ttt"); //bring out information 
            ticTacToe.setGameState(3);
            // updateTicTacToeUsers(ticTacToe);
            TicTacToeConsole ttt = new TicTacToeConsole(null);  //bring up tic tac toe board for player 1
            TicTacToeConsole ttt2 = new TicTacToeConsole(null); //bring up tic tac toe board for player 2
            ttt.getPlayerName().setText(ticTacToe.getPlayer1()); //set text field to players 1 name to start the game after player 2 accepts
            //-----------------------------------------------------------------------------------------------------------------
            ticTacToe.setActivePlayer(1); //set the active player to 1 
            //buttons for the tic tac toe calling the getters to attach an action 
            //** retrives information, calls the updateboard method with a move of 1 - 9 (depending on botton pressed), updates the GUI board with the tic tac toe board,
            //this method checks if there is a winner move and who's the active player, and changes the game state to 4 (won) to inform the player who lost and who won
            ttt.getB1().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(1);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });
            ttt.getB2().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(2);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });
            ttt.getB3().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(3);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt.getB4().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(4);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt.getB5().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(5);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt.getB6().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(6);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt.getB7().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(7);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }

                }
            });

            ttt.getB8().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");

                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(8);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }

                }
            });

            ttt.getB9().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 1) {
                        ticTacToe.updateBoard(9);
                        ttt.updateBoard(ticTacToe.getBoard());
                        ttt2.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

//-------------------------=====================================-------------ACTION FOR SECOND TIC TAC TOE INSTANCE -------------------=======================================--------------------
            ttt2.getB1().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt"); //retrive saved tic tac toe object

                    if (ticTacToe.getActivePlayer() == 2) { //this IF ensure player 1 cannot double move when it's not their turn to play
                        ticTacToe.updateBoard(1); //call the tic tac toe class method with a move number corresponding to the click 
                        ttt2.updateBoard(ticTacToe.getBoard()); //update board 2
                        ttt.updateBoard(ticTacToe.getBoard()); //update board 1 with the new input from the board 2
                        moveChecker(ticTacToe, ttt, ttt2, client); //calls the move checker method
                    }
                }
            });
            ttt2.getB2().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(2);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });
            ttt2.getB3().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(3);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB4().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(4);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB5().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(5);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB6().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(6);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB7().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(7);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB8().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(8);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });

            ttt2.getB9().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TicTacToe ticTacToe = (TicTacToe) client.getInfo("ttt");
                    if (ticTacToe.getActivePlayer() == 2) {
                        ticTacToe.updateBoard(9);
                        ttt2.updateBoard(ticTacToe.getBoard());
                        ttt.updateBoard(ticTacToe.getBoard());
                        moveChecker(ticTacToe, ttt, ttt2, client);
                    }
                }
            });
        }

    }

    public static void main(String[] args) {
        int port = 0; //Port to listen on

        try {
            port = Integer.parseInt(args[0]); //Set port to 5555
        } catch (ArrayIndexOutOfBoundsException e) {
            port = DEFAULT_PORT;
        }

        try {
            EchoServer sv = new EchoServer(port);
            sv.listen();
            // sv.listen(); //Start listening for connections
        } catch (IOException ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }
}
