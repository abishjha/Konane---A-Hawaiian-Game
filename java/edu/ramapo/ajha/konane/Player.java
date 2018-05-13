package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 1 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     February 2, 2018                               *
 ************************************************************
*/

public class Player {
   private String playerName;
   private char playerColor;
   private int playerScore;
   private String playerType;

   public static final String HUMAN = "Human";
   public static final String COMPUTER = "Computer";


   //
   // parameters :- name --> name of the player, color --> the color the player should be assigned
   // returns    :- the tile at [ row, column ] in the board as a character
   //
   public Player(char color) {
      playerColor = color;
      playerScore = 0;

      if(color == Board.BLACK)
         playerName = "Black";
      else
         playerName = "White";
   }


   //
   // parameters :- name --> name of the player, color --> the color the player should be assigned
   //               score --> score of the player
   // returns    :- the tile at [ row, column ] in the board as a character
   //
   public Player(char color, int score) {
      playerColor = color;
      playerScore = score;

      if(color == Board.BLACK)
         playerName = "Black";
      else
         playerName = "White";
   }


   //
   // parameters :- (null)
   // returns    :- name of the player as a String
   //
   public String getName() {
      return playerName;
   }


   //
   // parameters :- (null)
   // returns    :- color of the player as a character
   //
   public char getColor() {
      return playerColor;
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   public void addScore() {
      playerScore += 1;
   }


   //
   // parameters :- (null)
   // returns    :- current score of the player as an integer
   //
   public int getScore() {
      return playerScore;
   }


   //
   // parameters :- in --> the player type (computer or human)
   // returns    :- (null)
   //
   public void setPlayerType(String in){ playerType = in; }


   public String getPlayerType(){ return playerType; }
}
