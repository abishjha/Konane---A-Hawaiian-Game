package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 3 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     March 23, 2018                                  *
 ************************************************************
*/

import java.util.Arrays;
import java.util.LinkedList;

public class MiniMaxNode {
    private char[][] board; // updated board state after move "move"
    private String move; // string containing the move used to reach this state
    private LinkedList<String> availableMoves; // a list of available moves from current board state for next player
    private int myScore; // the current players score
    private int opponentScore; // the next players score


    //
    // parameters :- inBoard --> the board state for the current node before the move is made,
    //               move --> the move to be made to reach the current node from the parent node,
    //               player --> the current player,
    //               myScore --> the score for the current player,
    //               opponentScore --> the score for the opponent player
    // returns    :- (null)
    //
    // parameter that comes in is the initial board state and we modify it to reflect the move
    MiniMaxNode(char[][] inBoard, String move, char player, int myScore, int opponentScore){
        // saving a copy of the board so it can be edited without the edits reflecting on the original board
        this.board = new char[Board.BOARD_DIM][Board.BOARD_DIM];
        for(int i = 0; i < Board.BOARD_DIM; i++)
            this.board[i] = Arrays.copyOf(inBoard[i], inBoard[i].length);

        this.move = move;
        makeMove();

        if(this.move.length() >= 6)
            myScore += (move.length() / 3) - 1;

        this.myScore = myScore;
        this.opponentScore = opponentScore;

        // search player is the same as the player with the node as it is his moves we are searching for
        // the move execution is just updating the board state
        Search newSearch = new Search("DepthFS", this.board, player, 0);
        this.availableMoves = newSearch.getOutput();
        breakMoveToParts();
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    // make the move that was taken to reach the current state i.e. update the board state as per the move
    private void makeMove(){
        if(this.move.length() < 6) return;

        int srcRow, srcCol, destRow, destCol;
        for (int i = 0; i < (move.length() - 3); i += 3) {
            srcRow = Character.getNumericValue(move.charAt(i));
            srcCol = Character.getNumericValue(move.charAt(i + 1));
            destRow = Character.getNumericValue(move.charAt(i + 3));
            destCol = Character.getNumericValue(move.charAt(i + 4));

            // move tile from source slot to destination slot
            board[destRow][destCol] = board[srcRow][srcCol];

            // put a blank space in the tile that was taken over(i.e. jumped over) by the player
            if(srcCol == destCol){
                int biggerRow = java.lang.Math.max(srcRow, destRow);
                board[biggerRow-1][srcCol] = Board.SPACE;
            }
            else{
                int biggerCol = java.lang.Math.max(srcCol, destCol);
                board[srcRow][biggerCol-1] = Board.SPACE;
            }

            // put a blank space in the source slot as the tile is moved to destination slot
            board[srcRow][srcCol] = Board.SPACE;
        }
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    // breaks the calculated moves into parts as each can be a move on its own
    private void breakMoveToParts(){
        int size = this.availableMoves.size();
        for(int i = 0; i < size; i++){
            String temp = this.availableMoves.get(i);
            if(temp.length() <= 6) continue;

            for(int j = 6; j < temp.length(); j += 3) {
                String move = temp.substring(0, j);
                if(!this.availableMoves.contains(move))
                    this.availableMoves.addLast(move);
            }
        }

        // order moves from lowest scoring to highest scoring.
        // checked and compared ordered result times with non ordered ones and there is only a slight time difference
        //    of around 4-5% so I am going to stick with ordering the result to have a predictable pattern to the output
        boolean flag = true;
        String temp;
        while(flag){
            flag = false;
            for(int i = 0; i < availableMoves.size()-1; i++){
                if(availableMoves.get(i).length() > availableMoves.get(i+1).length()){
                    temp = availableMoves.get(i);
                    availableMoves.set(i, availableMoves.get(i+1));
                    availableMoves.set(i+1, temp);
                    flag = true;
                }
            }
        }
    }


    //
    // parameters :- (null)
    // returns    :- a two dimensional character array contains the board state for the current node
    //
    public char[][] getBoard(){
        return this.board;
    }


    //
    // parameters :- (null)
    // returns    :- a list of string containing moves possible from the current board for the next player
    //
    public LinkedList<String> getAvailableMoves(){
        return availableMoves;
    }


    //
    // parameters :- (null)
    // returns    :- true if the node has no children moves, false otherwise
    //
    public boolean isLeafNode(){
        if(availableMoves.size() == 0)
            return true;
        return false;
    }


    //
    // parameters :- (null)
    // returns    :- the current player's score
    //
    public int getMyScore(){
        return this.myScore;
    }


    //
    // parameters :- (null)
    // returns    :- the opponent player's score
    //
    public int getOpponentScore(){
        return this.opponentScore;
    }
}
