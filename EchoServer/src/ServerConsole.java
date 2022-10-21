/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner; 
/**
 *
 * @author macbookpro2017
 */

public class ServerConsole implements ChatIF {
     
    Scanner input = new Scanner(System.in); //scanner 
     int port = 0; //default port
     
    //Class variables *************************************************
  public ServerConsole(){};
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5556;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;

  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  
  
  public static void main(String[] args) 
  {
      
  int port = 0; //Port to listen on

   try{
    port = Integer.parseInt(args[0]); //Set port to 5555
   }
   catch(ArrayIndexOutOfBoundsException e){
       port = DEFAULT_PORT;
   }
   	

    
    try 
    {   
      ServerConsole a= new ServerConsole();
      System.out.println("Welcome to the server ADMIN: ");
      System.out.println("USE COMMAND AS ADMIN: ");
      System.out.println("#start - to start server");
      System.out.println("press anything - to shut-down server");
      String main = a.input.nextLine();
      a.echoCommand(main); 

    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
 
  }

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ServerConsole(String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!!!!" + " Terminating client.");
      System.exit(1);
    }
  }

    public void echoCommand(Object o) throws IOException{  
      if (o.toString().equals("#start")){
          if (port == 0){
              EchoServer sv = new EchoServer(DEFAULT_PORT);
              sv.listen();
              String host = "";
              ServerConsole chat= new ServerConsole(host, DEFAULT_PORT);
              chat.accept();  //Wait for console data
          }else{
              EchoServer sv = new EchoServer(port);
              sv.listen();
              String host = "";
              ServerConsole chat= new ServerConsole(host, port);
              chat.accept();  //Wait for console data
          }
             
      }
      else{
          System.out.println("Server shutting down - Good Bye");
      }
  }
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) 
      {
        message = fromConsole.readLine();
        client.handleMessageFromAdmin(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {  
           System.out.println("> " + message);  
  }

  
  //Class methods ***************************************************

    @Override
    public void displayUserList(ArrayList<String> userList, String room) {
        
    }
  
}
