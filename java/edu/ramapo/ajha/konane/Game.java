package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 1 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     February 2, 2018                               *
 ************************************************************
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

public class Game {
   private Board inProgGame;
   private Player player1;
   private Player player2;
   private boolean turn; // if true, player1's move. false -- player2.

   //
   // parameters :- name1 --> the name of player1, name2 --> for name of player2
   // returns    :- (null)
   //
   // the constructor for the class
   public Game(int boardSize, String blackPlayer){
      inProgGame = new Board(boardSize);
      player1 = new Player(Board.BLACK);
      player2 = new Player(Board.WHITE);
      turn = true;

      if(blackPlayer.equals(Player.COMPUTER)){
         player1.setPlayerType(Player.COMPUTER);
         player2.setPlayerType(Player.HUMAN);
      }
      else{
         player1.setPlayerType(Player.HUMAN);
         player2.setPlayerType(Player.COMPUTER);
      }

      /* // use this code to load a specific board for debugging purposes in the emulator
      char[][] arr = new char[][]{
              "BOOOOO".toCharArray(),
              "WOOOOO".toCharArray(),
              "OOBWOW".toCharArray(),
              "OOOBOO".toCharArray(),
              "OOOOBO".toCharArray(),
              "OOOOOO".toCharArray()
      };
      inProgGame = new Board(arr);
      player1 = new Player("Black", Board.BLACK);
      player2 = new Player("White", Board.WHITE);
      turn = true; */
   }


   //
   // parameters :- file --> filename with game state data to be loaded
   // returns    :- (null)
   //
   // the constructor for the class if the input is from a file
   public Game(String fileName){
      try {
         FileReader in = new FileReader(fileName);
         BufferedReader reader = new BufferedReader(in);

         int lines = 0;
         while(reader.readLine() != null) lines++;
         Board.BOARD_DIM = lines - 5; // cause there are five lines in the text file that are not board rows

         // reloading the file so we can
         in = new FileReader(fileName);
         reader = new BufferedReader(in);

         char[][] arr = new char[Board.BOARD_DIM][Board.BOARD_DIM];
         int row = 0;

         String oneLine;
         while((oneLine = reader.readLine()) != null) {
            String[] content = oneLine.split(" ");

            if (content[0].equals("Black:")) {
               player1 = new Player(Board.BLACK, Integer.parseInt(content[1]));
            } else if (content[0].equals("White:")) {
               player2 = new Player(Board.WHITE, Integer.parseInt(content[1]));
            } else if (content[0].equals("Next")) {
               turn = false;
               if (content[2].equals("Black"))
                  turn = true;
            } else if (content[0].equals("Board:")) {
               // do nothing for this line
            } else if (content[0].equals("Human:")){
               if(content[1].equals("White")){
                  player1.setPlayerType(Player.COMPUTER);
                  player2.setPlayerType(Player.HUMAN);
               }
               else{
                  player1.setPlayerType(Player.HUMAN);
                  player2.setPlayerType(Player.COMPUTER);
               }
            } else {
               if(content.length == Board.BOARD_DIM && row < Board.BOARD_DIM) {
                  for (int i = 0; i < Board.BOARD_DIM; i++)
                     arr[row][i] = content[i].charAt(0);
                  row++;
               }
            }
         }
         inProgGame = new Board(arr);
         reader.close();
      }
      catch(Exception ex){
         System.out.println(ex.toString());
      }
   }


   //
   // parameters :- srcRow --> source row for the move, srcCol --> source column for the move,
   //               destRow --> destination row for the move, destCol --> destination column for the move
   // returns    :- true --> move valid and successful, false --> move not valid or problem moving
   //
   public boolean move(int srcRow, int srcCol, int destRow, int destCol){
      boolean check = checkMove(srcRow, srcCol, destRow, destCol);
      if(check)
         makeMove(srcRow, srcCol, destRow, destCol);
      return check;
   }


   //
   // parameters :- srcRow --> source row for the move, srcCol --> source column for the move,
   //               destRow --> destination row for the move, destCol --> destination column for the move
   // returns    :- true --> move valid, false --> move not valid
   //
   private boolean checkMove(int srcRow, int srcCol, int destRow, int destCol){
      // check if the inputs are within bound
      if(srcRow < 0 || srcRow >= Board.BOARD_DIM || srcCol < 0 || srcCol >= Board.BOARD_DIM ||
            destRow < 0 || destRow >= Board.BOARD_DIM || destCol < 0 || destCol >= Board.BOARD_DIM)
         return false;

      // if the destination tile is not empty, the move is not valid
      if(inProgGame.getTileAt(destRow, destCol) != Board.SPACE) return false;

      // if the source tile is empty, the move is not valid
      if(inProgGame.getTileAt(srcRow, srcCol) == Board.SPACE) return false;

      // checking the gap between the source and the destination tile
      if(srcCol == destCol){
         // if the difference is not 2, the move is not valid
         int diff = java.lang.Math.abs(srcRow - destRow);
         if(diff != 2)
            return false;
      }
      else if(srcRow == destRow){
         // if the difference is not 2, the move is not valid
         int diff = java.lang.Math.abs(srcCol - destCol);
         if(diff != 2)
            return false;
      }
      // if neither the source row and the destination row, or the source column and the destination
      // column is same, it means the move is done diagonally which is not permitted
      else{
         return false;
      }

      if(turn && (inProgGame.getTileAt(srcRow, srcCol) == Board.BLACK)){
         // check if the character in between the source and the destination tile is of color Board.WHITE
         if(srcRow == destRow){
            int biggerCol = java.lang.Math.max(srcCol, destCol);
            if(inProgGame.getTileAt(srcRow, biggerCol-1) == Board.WHITE)
               return true;
         }
         else{
            int biggerRow = java.lang.Math.max(srcRow, destRow);
            if(inProgGame.getTileAt(biggerRow-1 , srcCol) == Board.WHITE)
               return true;
         }
      }
      else if(!turn && (inProgGame.getTileAt(srcRow, srcCol) == Board.WHITE)){
         // check if the character in between the source and the destination tile is of color Board.BLACK
         if(srcRow == destRow){
            int biggerCol = java.lang.Math.max(srcCol, destCol);
            if(inProgGame.getTileAt(srcRow, biggerCol-1) == Board.BLACK)
               return true;
         }
         else{
            int biggerRow = java.lang.Math.max(srcRow, destRow);
            if(inProgGame.getTileAt(biggerRow-1 , srcCol) == Board.BLACK)
               return true;
         }
      }

      // if the current player is black and the source tile selected is not black, or, if the current
      // player is white and the source tile selected is not white, the move is not valid
      return false;
   }


   //
   // parameters :- srcRow --> source row for the move, srcCol --> source column for the move,
   //               destRow --> destination row for the move, destCol --> destination column for the move
   // returns    :- (null)
   //
   // make the move if valid and add points to the appropriate player
   private void makeMove(int srcRow, int srcCol, int destRow, int destCol){
      // move tile from source slot to destination slot
      inProgGame.setTileAt(destRow, destCol, inProgGame.getTileAt(srcRow, srcCol));

      // put a blank space in the tile that was taken over(i.e. jumped over) by the player
      if(srcCol == destCol){
         int biggerRow = java.lang.Math.max(srcRow, destRow);
         inProgGame.setTileAt(biggerRow-1, srcCol, Board.SPACE);
      }
      else{
         int biggerCol = java.lang.Math.max(srcCol, destCol);
         inProgGame.setTileAt(srcRow, biggerCol-1, Board.SPACE);
      }

      // put a blank space in the source slot as the tile is moved to destination slot
      inProgGame.setTileAt(srcRow, srcCol, Board.SPACE);

      if(turn)
         player1.addScore();
      else
         player2.addScore();
   }


   //
   // parameters :- (null)
   // returns    :- true --> pass valid, false --> pass not valid
   //
   public boolean pass(){
      // the current players character color
      char currentChar = Board.WHITE;
      if(currentPlayer())
         currentChar = Board.BLACK;

      for(int i = 0; i < Board.BOARD_DIM; i++)
      {
         for(int j = 0; j < Board.BOARD_DIM; j++)
         {
            if(inProgGame.getTileAt(i, j) == currentChar)
            {
               // checking for moving two rows up
               // if checkMove returns true, this means there is a valid move and pass is not allowed
               if(((i - 2) >= 0) && checkMove(i, j, i-2, j))
               {
                  return false;
               }

               // checking for moving two rows down
               // if checkMove returns true, this means there is a valid move and pass is not allowed
               if(((i + 2) < Board.BOARD_DIM) && checkMove(i, j, i+2, j))
               {
                  return false;
               }

               // checking for moving two columns left
               // if checkMove returns true, this means there is a valid move and pass is not allowed
               if(((j - 2) >= 0) && checkMove(i, j, i, j-2))
               {
                  return false;
               }

               // checking for moving two columns right
               // if checkMove returns true, this means there is a valid move and pass is not allowed
               if(((j + 2) < Board.BOARD_DIM) && checkMove(i, j, i, j+2))
               {
                  return false;
               }
            }
         }
      }

      // if the above loop does not return, this means there are no valid moves for the player
      changePlayer();
      return true;
   }


   //
   // parameters :- algorithm --> the selected algorithm for the search
   //               depth --> cut-off depth for branch and bound algorithm, any value will do for others
   // returns    :- a linked list of string containing the available moves in order of visiting
   //
   public LinkedList<String> searchMove(String algorithm, int depth){
      char currPlayer = Board.WHITE;
      if(currentPlayer())
         currPlayer = Board.BLACK;

      Search newSearch = new Search(algorithm, getCurrentBoard(), currPlayer, depth);
      return newSearch.getOutput();
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   public void changePlayer() {
      turn = !turn;
   }


   //
   // parameters :- (null)
   // returns    :- true --> player1's (black) turn, false --> player2's (white) turn
   //
   public boolean currentPlayer(){
      return turn;
   }


   //
   // parameters :- (null)
   // returns    :- the current board state of the game as a 2-dimensional character array of size Board.BOARD_DIM
   //
   // returns a copy of the current board state
   public char[][] getCurrentBoard(){
      return inProgGame.getBoardCopy();
   }


   //
   // parameters :- row --> row of the tile, col --> column of the tile
   // returns    :- the tile at [ row, column ] in the board as a character
   //
   public char getTileAt(int row, int col){ return inProgGame.getTileAt(row, col); }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // prints out the current state of the board -- used for debugging in this class's main
   private void printCurrentBoard(){
      // printing the board
      char[][] board_curr = getCurrentBoard();
      for (int i = 0; i < Board.BOARD_DIM; i++) {
         for (int j = 0; j < Board.BOARD_DIM; j++) {
            System.out.print(board_curr[i][j] + " ");
         }
         System.out.println("");
      }
   }


   //
   // parameters :- (null)
   // returns    :- player1's current score in the game as an integer
   //
   public int getPlayer1Score(){
      return player1.getScore();
   }


   //
   // parameters :- (null)
   // returns    :- player2's current score in the game as an integer
   //
   public int getPlayer2Score(){
      return player2.getScore();
   }


   //
   // parameters :- (null)
   // returns    :- player1's name as a String
   //
   public String getPlayer1Type(){ return player1.getPlayerType(); }


   //
   // parameters :- (null)
   // returns    :- player2's name as a String
   //
   public String getPlayer2Type(){ return player2.getPlayerType(); }


   //
   // parameters :- (null)
   // returns    :- the current player's type. "Human" for human player and "Computer" for the computer player
   //
   public String getCurrentPlayerType(){
      if(currentPlayer()) // current player true means its black's turn
         return player1.getPlayerType();
      return player2.getPlayerType();
   }


   // test driver program for game logic. uses the console to act as View
   public static void main(String[] args){
      Game newGame = new Game(6, Player.HUMAN);
      java.util.Scanner reader = new java.util.Scanner(System.in);
      int srcRow, srcCol, destRow, destCol, passFlag;
      newGame.printCurrentBoard();

      int cnt = 4;
      while(cnt-- > 0){
         if(newGame.currentPlayer())
            System.out.println("player 1 (black) moves");
         else
            System.out.println("player 2 (white) moves");

         System.out.print("do you want to pass? (press 0 for yes) (press 1 for no) ");
         passFlag = reader.nextInt();
         if(passFlag == 1) {
            srcRow = reader.nextInt();
            srcCol = reader.nextInt();
            destRow = reader.nextInt();
            destCol = reader.nextInt();

            if(newGame.move(srcRow, srcCol, destRow, destCol)) {
               System.out.println("move successful!");
               // printing the board after every turn
               newGame.printCurrentBoard();
               newGame.changePlayer();
            }
            else {
               System.out.println("move unsuccessful! try again");
               cnt++;
            }
         }
         else{
            newGame.pass();
         }
         // print a new line after every iteration
         System.out.println();
      }
      System.out.println("Player1 (black) score : " + newGame.getPlayer1Score() + "\nPlayer2 (white) score : " + newGame.getPlayer2Score());
      reader.close();
   }
}
