/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author gavin
 */
public class TicTacToeConsole extends JFrame {

    private JButton b1 = new JButton("-");
    private JButton b2 = new JButton("-");
    private JButton b3 = new JButton("-");
    private JButton b4 = new JButton("-");
    private JButton b5 = new JButton("-");
    private JButton b6 = new JButton("-");
    private JButton b7 = new JButton("-");
    private JButton b8 = new JButton("-");
    private JButton b9 = new JButton("-");

    private JLabel textfield = new JLabel();
    private JLabel playerName = new JLabel();
    ChatIF clientUI;
    TicTacToe game = new TicTacToe();

    public TicTacToeConsole(ChatIF clientUI) {
        super("Tic Tac Toe");
        textfield.setText("Your Turn: ");
        this.setLayout(new GridLayout(4, 4, 3, 3));
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
        add(b7);
        add(b8);
        add(b9);
        add(textfield);
        add(playerName);
        setSize(300, 300);
        setVisible(true);

        this.clientUI = clientUI;
    }

    public JLabel getTextfield() {
        return textfield;
    }

    public JLabel getPlayerName() {
        return playerName;
    }

    
    public JButton getB1() {
        return b1;
    }

    public JButton getB2() {
        return b2;
    }

    public JButton getB3() {
        return b3;
    }

    public JButton getB4() {
        return b4;
    }

    public JButton getB5() {
        return b5;
    }

    public JButton getB6() {
        return b6;
    }

    public JButton getB7() {
        return b7;
    }

    public JButton getB8() {
        return b8;
    }

    public JButton getB9() {
        return b9;
    }

    public void updateBoard(char[][] board) {
        b1.setText(board[0][0] + "");
        b2.setText(board[0][1] + "");
        b3.setText(board[0][2] + "");
        b4.setText(board[1][0] + "");
        b5.setText(board[1][1] + "");
        b6.setText(board[1][2] + "");
        b7.setText(board[2][0] + "");
        b8.setText(board[2][1] + "");
        b9.setText(board[2][2] + "");

    }

    public boolean checkWin(char[][] board) {
        if ((board[0][0] == board[0][1] && board[0][0] == board[0][2])
                || (board[1][0] == board[1][1] && board[1][0] == board[1][2])
                || (board[2][0] == board[2][1] && board[2][0] == board[2][2])
                || (board[0][0] == board[1][0] && board[0][0] == board[2][0])
                || (board[0][1] == board[1][1] && board[0][1] == board[2][1])
                || (board[0][2] == board[1][2] && board[0][2] == board[2][2])
                || (board[0][0] == board[1][1] && board[0][0] == board[2][2])
                || (board[0][2] == board[1][1] && board[0][2] == board[2][0])) {
            return true;
        }
        //for example
        return false;
    }

    public void closeGame() { //close game
        this.dispose();
    }

}
