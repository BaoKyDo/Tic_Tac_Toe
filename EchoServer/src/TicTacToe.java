
import java.io.Serializable;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author macbookpro2017
 */
public class TicTacToe implements Serializable {

    private String player1;
    private String player2;
    private int activePlayer;
    private int gameState = 1;
    private char[][] board = new char[3][3];
    public int movement = 9;
    
    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    //check winner if character matches in the board and the active player matches
    public void checkWin() {

        if (hasContestantWon(board, 'X') == true && activePlayer == 1) {
         

        }
        if (hasContestantWon(board, 'O') == true && activePlayer == 2) {
           
    
        }

    }

    public boolean hasContestantWon(char[][] board, char symbol) {
        if        ( (board[0][0] == symbol && board[0][1] == symbol && board[0][2] == symbol)
                || (board[1][0] == symbol && board[1][1] == symbol && board[1][2] == symbol)
                || (board[2][0] == symbol && board[2][1] == symbol && board[2][2] == symbol)
                || (board[0][0] == symbol && board[1][0] == symbol && board[2][0] == symbol)
                || (board[0][1] == symbol && board[1][1] == symbol && board[2][1] == symbol)
                || (board[0][2] == symbol && board[1][2] == symbol && board[2][2] == symbol)
                || (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
                || (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)){
            setGameState(4); //chages the state immediately this is true
            return true;
        }
        else{
            return false;
        }
    }

    public void updateBoard(int move) {
        char character;

        if (movement > 0) { //as long as the movement is grater than 0, players can continue playing and updating the board
            switch (move) { //moves 1 - 9 to update board
                case 1:
                    if (board[0][0] == 'X' || board[0][0] == 'O') { //check if a board has already been update, it prevents it from been override by clicking on it again

                    } else { 
                        if (activePlayer == 1) { // helps to toogle the active player after any click
                            character = 'X';
                            setActivePlayer(2); 
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[0][0] = character; //updates the board to the character
                        setBoard(board); //set the board to the board
                        movement--; //reduce the move by 1
                    }
                    break;
                case 2:
                    if (board[0][1] == 'X' || board[0][1] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[0][1] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 3:
                    if (board[0][2] == 'X' || board[0][2] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[0][2] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 4:
                    if (board[1][0] == 'X' || board[1][0] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[1][0] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 5:

                    if (board[1][1] == 'X' || board[1][1] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[1][1] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 6:
                    if (board[1][2] == 'X' || board[1][2] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[1][2] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 7:
                    if (board[2][0] == 'X' || board[2][0] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[2][0] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 8:
                    if (board[2][1] == 'X' || board[2][1] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[2][1] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;
                case 9:
                    if (board[2][2] == 'X' || board[2][2] == 'O') {

                    } else {
                        if (activePlayer == 1) {
                            character = 'X';
                            setActivePlayer(2);
                        } else {
                            character = 'O';
                            setActivePlayer(1);
                        }

                        board[2][2] = character;
                        setBoard(board);
                        movement--;
                    }
                    break;

                default:
                    break;
            }
        }
    }
    
    //game over method when tic tac toe board is full
     public void gameOver(){
            if(movement == 0){
                   JOptionPane.showMessageDialog(null, "Both Players are out of moves,\n Game ended in a Draw!", "Game Over",JOptionPane.PLAIN_MESSAGE);
                   System.exit(0); //exits game
                   
            }
        }

}
