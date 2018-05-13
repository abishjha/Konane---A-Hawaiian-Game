package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 1 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     February 2, 2018                               *
 ************************************************************
*/

import java.util.Arrays;
import java.util.Random;

public class Board {
   public static int BOARD_DIM = 6; // board dimension
   public static final char WHITE = 'W'; // representation for white piece
   public static final char BLACK = 'B'; // representation for black piece
   public static final char SPACE = 'O'; // representation for white piece

   // store the main board for the game
   private char[][] board;


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // constructor for the board which fills the board and removes one white and one black piece at random
   public Board(int boardSize) {
      BOARD_DIM = boardSize;
      board = new char[BOARD_DIM][BOARD_DIM];
      fillBoardInit();
      removeTwo();
   }

   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // constructor for the board with a given board state
   public Board(char[][] in){
      board = in;
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // fill the board as per the game rules
   private void fillBoardInit() {
      // cells whose (row + column) sum is even are black and the odd ones are white
      for (int i = 0; i < BOARD_DIM; i++) {
         for (int j = 0; j < BOARD_DIM; j++) {
            if ((i + j) % 2 == 0)
               board[i][j] = BLACK;
            else
               board[i][j] = WHITE;
         }
      }
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // remove one black and one white tile at random from the filled board
   private void removeTwo() {
      Random rand = new Random();
      int row, col;
      char firstColor;

      row = rand.nextInt(BOARD_DIM);
      col = rand.nextInt(BOARD_DIM);
      firstColor = board[row][col]; // recording the first color that is removed so its counter can be removed the second time
      board[row][col] = SPACE; // storing blank in the memory i.e. deleting tile

      row = rand.nextInt(BOARD_DIM);
      col = rand.nextInt(BOARD_DIM);
      // this means we deleted a white tile the first time
      if (firstColor == 'W') {
         if ((row + col) % 2 == 0)
            board[row][col] = SPACE;
         else
            board[row][(col + 1) % BOARD_DIM] = SPACE;
      }
      // this means we deleted a black tile the first time
      else {
         if ((row + col) % 2 == 1)
            board[row][col] = SPACE;
         else
            board[row][(col + 1) % BOARD_DIM] = SPACE;
      }
   }


   //
   // parameters :- (null)
   // returns    :- a deep copy of the current board state of the game as a two-dimensional character array
   //
   public char[][] getBoardCopy() {
      char[][] copyBoard = new char[Board.BOARD_DIM][Board.BOARD_DIM];
      for(int i = 0; i < Board.BOARD_DIM; i++){
         copyBoard[i] = Arrays.copyOf(board[i], board[i].length);
      }
      return copyBoard;
   }


   //
   // parameters :- row --> row of the tile, col --> column of the tile
   // returns    :- the tile at [ row, column ] in the board as a character
   //
   public char getTileAt(int row, int col) {
      return board[row][col];
   }


   //
   // parameters :- row --> row of the tile, col --> column of the tile, color --> the color to set the tile to
   // returns    :- (null)
   //
   public void setTileAt(int row, int col, char color) {
      board[row][col] = color;
   }


   // test main for this class
   public static void main(String[] args) {
      Board game = new Board(6);
      // printing the board
      for (int i = 0; i < BOARD_DIM; i++) {
         for (int j = 0; j < BOARD_DIM; j++) {
            System.out.print(game.getBoardCopy()[i][j] + " ");
         }
         System.out.println("");
      }
   }
}