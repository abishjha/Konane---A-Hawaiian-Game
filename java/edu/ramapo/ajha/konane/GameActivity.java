package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 1 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     February 2, 2018                               *
 ************************************************************
*/

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
   public static final String BLACK_PIECE = "⚉";
   public static final String WHITE_PIECE = "⚇";
   public static final String BLANK_PIECE = "×";

   private Game newGame; // store the currently played game
   private ArrayList<Integer> moves; // ArrayList to store moves at any given turn
   private boolean multiPass; // setting the multiPass flag to false indicating the previous move was not a pass

   private LinkedList<String> algoResult; // store the result of a search algorithm
   private String algorithm ; // stores the algorithm selected by the user. "nan" for no or invalid choice.
   private int depth, plyCutOff;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_game);
      this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // setting up default values for global variables
      moves = new ArrayList<>();
      multiPass = false;
      algoResult = new LinkedList<>();
      algorithm = "nan";
      plyCutOff = 1;

      // if a new game is selected
      if ((getIntent().getStringExtra("game")).equals("1")) {
         int size = getIntent().getIntExtra("size", 0);
         newGame = new Game(size, getIntent().getStringExtra("blackPlayer"));
      }
      // if a game state is loaded
      else if ((getIntent().getStringExtra("game")).equals("2")) {
         String file = getIntent().getStringExtra("filepath");
         newGame = new Game(file);
      }

      // make buttons on the board with incrementing id's starting from 0
      fillBoard();

      // setting up onClick listeners
      for (int i = 0; i < (Board.BOARD_DIM * Board.BOARD_DIM); i++) {
         ((Button) findViewById(i)).setOnClickListener(this);
      }

      // initialize and setup the spinner for the search algorithms
      algoSpinner();

      // initialize and setup the spinner for the ply cut-off length
      plySpinner();

      ((Switch) findViewById(R.id.alphabeta)).setOnClickListener(this);

      // updates the GameActivity screen
      updateScreen();
   }


   // creates the board on the screen
   public void fillBoard() {
      // attach the table layout element in the .xml file to board variable
      TableLayout board = findViewById(R.id.tableLayout);
      int count = 0;

      for (int i = 0; i < Board.BOARD_DIM; i++) {
         TableRow row = new TableRow(this);

         for (int j = 0; j < Board.BOARD_DIM; j++) {
            Button btn = new Button(this);
            btn.setId(count);
            count++;

            if ((i + j) % 2 == 0) { // for black tiles
               btn.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
               btn.getBackground().setAlpha(64);
            } else // for white tiles
               btn.setBackgroundColor(getResources().getColor(android.R.color.background_light));

            row.addView(btn); // add button to row
         }

         board.addView(row);
      }
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // initialize and setup the spinner for the search algorithms
   public void algoSpinner() {
      final Spinner spinner = (Spinner) findViewById(R.id.algo_spinner);

      String[] menuItems = new String[]{"(choose algorithm)", "DepthFS", "BreadthFS", "BestFS", "BranchAndBound"};

      // Creating adapter for spinner
      ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, menuItems);

      // Drop down layout style - list view with radio button
      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // attaching data adapter to spinner
      spinner.setAdapter(dataAdapter);

      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String item = spinner.getItemAtPosition(position).toString();
            if (item.equals("BranchAndBound")) {
               depth = 0;
               final String[] depths = new String[]{"Full", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                       "12", "13", "14", "15", "16", "17", "18", "19", "20"};

               AlertDialog.Builder depthPicker = new AlertDialog.Builder(view.getContext());
               depthPicker.setTitle("pick a cut-off depth");
               depthPicker.setItems(depths, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                     depth = i; // if the user picks the full tree, the depth is 0
                     displayMessage("selected cut-off length : " + ((depth == 0) ? "full" : depth));
                  }
               });
               depthPicker.show();
            }

            // clear the result structure only when there is a change in algorithm or player
            algoResult.clear();
            // clear the animations and selections from before algorithm change
            resetButtons();
            // reset player's score to current score
            updateScore();

            algorithm = item;
            if (item.equals("(choose algorithm)"))
               algorithm = "nan";

            ((Button) findViewById(R.id.next_button)).setText("Press for Move");
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) { }
      });
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // initialize and setup the spinner for the ply cut-off
   public void plySpinner() {
      final Spinner spinner = (Spinner) findViewById(R.id.ply_spinner);

      String[] menuItems = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
              "12", "13", "14", "15", "16", "17", "18", "19", "20"};

      // Creating adapter for spinner
      ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, menuItems);

      // Drop down layout style - list view with radio button
      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // attaching data adapter to spinner
      spinner.setAdapter(dataAdapter);

      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            plyCutOff = position + 1;
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) { }
      });
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // handle on click events
   @Override
   public void onClick(View view) {
      // do nothing for the alphabeta switch change
      if(view.getId() == R.id.alphabeta) return;

      // if its the computer's turn and a move has been looked for, apply the move when any button on the board is pressed
      if(newGame.getCurrentPlayerType().equals(Player.COMPUTER) && (algoResult.size() == 1)){
         String move = algoResult.getFirst();
         for (int i = 0; i < move.length(); i += 3) {
            int r = Character.getNumericValue(move.charAt(i));
            int c = Character.getNumericValue(move.charAt(i + 1));
            moves.add(r); moves.add(c);
         }

         ((Button) findViewById(R.id.move_button)).performClick();
         return;
      }

      // dont allow any tile selections if its the computer player's turn
      if(newGame.getCurrentPlayerType().equals(Player.COMPUTER)) return;

      int id = view.getId();
      int row = id / Board.BOARD_DIM;
      int col = id % Board.BOARD_DIM;

      // if a player tries to select tiles that belong to a different player, don't allow it
      if ((row + col) % 2 == 0 && !newGame.currentPlayer()) return;
      else if ((row + col) % 2 == 1 && newGame.currentPlayer()) return;

      // if the first selection is a blank tile, don't allow it
      if (newGame.getTileAt(row, col) == Board.SPACE && moves.isEmpty()) return;

      ((Button) findViewById(id)).setTextColor(Color.RED);
      if ((newGame.getTileAt(row, col) == Board.SPACE))
         ((Button) findViewById(id)).setText(BLANK_PIECE);

      moves.add(row); moves.add(col);
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // handles next button click and shows the next move visited by the selected algorithm
   public void next_button(View view) {
      resetButtons();
      updateScore();

      // if no algorithm is selected, do nothing
      if (algorithm.equals("nan")) return;

      Button nextButton = findViewById(R.id.next_button);

      // if there is no result-set loaded, load one based on the algorithm chosen
      if (algoResult.size() == 0)
         algoResult = newGame.searchMove(algorithm, depth);

      // if the result list is still 0, this means the player has no more moves
      if (algoResult.size() == 0) {
         ((Button) findViewById(R.id.next_button)).setText("No Moves. Pass.");
         return;
      }

      // remove first element from result, record it, and push it back at the end of result
      String move = algoResult.removeFirst();
      algoResult.addLast(move);
      // setting the current set of moves as display on nextButton
      nextButton.setText(addOneForHumanCoordinates(move));

      // function sets a cross mark in the tiles that are involved in the move and starts the animation
      // also updates the players score if they go according to the suggested move
      updateNextTurn(move);
   }


   //
   // parameters :- in --> a string containing a move sequence in the standard "RC:"
   // returns    :- a string containing the human coordinates(i.e. starting from 1) of the tiles
   //               with the row and column separated by a comma
   //
   // use only when printing to screen as the standard which is returned is not understood by any
   // logical part of the game
   public String addOneForHumanCoordinates(String in) {
      String outputStr = "";
      for (int i = 0; i < in.length(); i += 3) {
         outputStr += (Character.getNumericValue(in.charAt(i)) + 1) + ",";
         outputStr += "" + (Character.getNumericValue(in.charAt(i + 1)) + 1);

         if(i != in.length()-3)
            outputStr += ":";
      }
      return outputStr;
   }


   //
   // parameters :- move --> a string with move details in the form "RC:" with repeated chunks,
   //                        one for each tile in the move
   // returns    :- (null)
   //
   // function for setting tile-text, animation and score to be used by the search algorithm's display feature
   public void updateNextTurn(String move) {
      if (newGame.currentPlayer()) // true means black players turn
         ((EditText) findViewById(R.id.player1_score)).setText(newGame.getPlayer1Score() + " +" + (move.length() / 3 - 1));
      else
         ((EditText) findViewById(R.id.player2_score)).setText(newGame.getPlayer2Score() + " +" + (move.length() / 3 - 1));

      // looping through the tile path in one move generated by the search algorithm.
      // every move in the path is stored as "RC:" in the appropriate sequence
      for (int i = 0; i < move.length(); i += 3) {
         // the current move stored as "RC" where R is row, C is column
         String oneMove = move.substring(i, i + 2);

         int row = Character.getNumericValue(oneMove.charAt(0));
         int col = Character.getNumericValue(oneMove.charAt(1));
         int buttonID = row * Board.BOARD_DIM + col;

         // if the tile at the location is blank, set it to '×' so the path is visible
         if (newGame.getTileAt(row, col) == Board.SPACE)
            ((Button) findViewById(buttonID)).setText(BLANK_PIECE);

         animate(buttonID);
      }
   }


   //
   // parameters :- number --> the id number of the button to animate
   // returns    :- (null)
   //
   // makes the button passed as input blink
   private void animate(int number){
      // setting up animation for the blinkers
      final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
      animation.setDuration(500); // duration - half a second
      animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
      animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
      animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

      // activating the blinkers on the button
      ((Button) findViewById(number)).startAnimation(animation);
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // check if the current set of moves is valid and act accordingly
   public void move_button(View view) {
      boolean changeFlag = false;
      boolean errorFlag = false;

      // this means either zero or one button is selected
      if (moves.size() <= 2) {
         errorFlag = true;
      } else {
         for (int i = 0; i < (moves.size() - 2); i += 2) {
            if (newGame.move(moves.get(i), moves.get(i + 1), moves.get(i + 2), moves.get(i + 3))) {
               changeFlag = true;
            } else {
               errorFlag = true;
               break;
            }
         }
      }

      // if one or all the moves are valid, the players turn is changed
      if (changeFlag) {
         newGame.changePlayer();
         // multiPass flag set to false indicating a valid move was taken in the current turn
         multiPass = false;

         ((Spinner) findViewById(R.id.algo_spinner)).setSelection(0);
      }

      // this means one of the multiply chained move is invalid. the players turn is changed
      if (errorFlag && changeFlag)
         displayMessage("error. partly invalid move");
         // this means the move(s) including the first move is not valid and players turn is NOT changed
      else if (errorFlag)
         displayMessage("error. invalid move");
         // this means the move(s) is valid
      else
         displayMessage("move completed");

      // if there is a change in the players turn, we update the whole screen's elements
      if (changeFlag) updateScreen();
         // else we only reset the selected buttons and the moves array, keeping intact the move animation
      else reset_button(view);
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // the click handler for the best move button which utilises MiniMax search algorithm
   public void best_move(View view){
      resetButtons(); // reset the animations and stuff on screen

      long startTime = System.currentTimeMillis();
      if(((Switch) findViewById(R.id.alphabeta)).isChecked())
         algoResult = newGame.searchMove("MiniMaxAB", plyCutOff);
      else
         algoResult = newGame.searchMove("MiniMax", plyCutOff);
      long endTime = System.currentTimeMillis();

      // update the time taken to calculate the move
      ((TextView) findViewById(R.id.search_time)).setText("time taken : " + getTime(startTime, endTime) + " | " + (endTime - startTime) + "ms");

      // no available moves
      if (algoResult.isEmpty()) {
         ((Button) findViewById(R.id.best_move)).setText("No Moves. Pass.");

         // wait and pass turn
         if(newGame.getCurrentPlayerType().equals(Player.COMPUTER)) {
            new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                  ((Button) findViewById(R.id.pass_button)).performClick();
               }
            }, 3000);
         }
         return;
      }

      // get the calculated best move
      final String move = algoResult.getFirst();
      // set the best move as the text in the best_move button
      ((Button) findViewById(R.id.best_move)).setText(addOneForHumanCoordinates(move));
      // animate the best move and display expected score
      updateNextTurn(move);

      /** // only use this when you want the computer to make an automatic move once it is calculated
      // if its a human player, do not make the move. just showing it is enough
      if(newGame.getCurrentPlayerType().equals(Player.HUMAN))
         return;

      // wait and execute calculated moves
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            for (int i = 0; i < move.length(); i += 3) {
               int r = Character.getNumericValue(move.charAt(i));
               int c = Character.getNumericValue(move.charAt(i + 1));
               moves.add(r); moves.add(c);
            }

            ((Button) findViewById(R.id.move_button)).performClick();
         }
      }, 3000); **/
   }


   //
   // parameters :- startTime --> the initial time in milliseconds,
   //               endTime --> the final time in milliseconds
   // returns    :- a string containing the difference in the time between the startTime and endTime
   //               as minute, second, and millisecond
   //
   // converts the input time difference which is in milliseconds to minutes, seconds, and milliseconds
   private String getTime(long startTime, long endTime){
      long timeDiff = endTime - startTime;
      long minutes, seconds, milliseconds;

      // 1 minute -- 60000 milliseconds
      // 1 second --  1000 milliseconds
      minutes = timeDiff / 60000;
      timeDiff = timeDiff % 60000;
      seconds = timeDiff / 1000;
      milliseconds = timeDiff % 1000;

      return minutes + "m " + seconds + "s " + milliseconds + "ms";
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // check if pass is valid and acts accordingly
   public void pass_button(View view) {
      if (newGame.pass()) {
         if (multiPass)
            isDone();

         multiPass = true; // the first player passed
         updateScreen();
         displayMessage("pass completed");

         ((Spinner) findViewById(R.id.algo_spinner)).setSelection(0);
      } else {
         displayMessage("error. invalid pass");
         // we reset the selected buttons and the moves array, keeping intact the move animation
         reset_button(view);
      }
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   public void reset_button(View view) {
      resetButtons();

      // reapply the button animations and the score display if reset move is pressed when a search move was selected/being displayed
      if (!algorithm.equals("nan") && algoResult.size() != 0)
         updateNextTurn(algoResult.getLast());
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   // moves to the FinishActivity when game is finished
   private void isDone() {
      // move to the next activity
      Intent intent = new Intent(this, FinishActivity.class);
      intent.putExtra("player1_score", newGame.getPlayer1Score());
      intent.putExtra("player2_score", newGame.getPlayer2Score());

      intent.putExtra("size", Board.BOARD_DIM);

      if((newGame.currentPlayer() && newGame.getCurrentPlayerType().equals(Player.HUMAN)) ||
              (!newGame.currentPlayer() && newGame.getCurrentPlayerType().equals(Player.COMPUTER)))
         intent.putExtra("blackPlayer", Player.HUMAN);
      else
         intent.putExtra("blackPlayer", Player.COMPUTER);

      startActivity(intent);
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   private void updateScreen() {
      // if updateBoard returns true, it means the board has only tiles for one player which means the game has ended
      if (updateBoard()) {
         isDone();
      }

      // else the game goes on
      updateScore();
      updateCurrentPlayerDisplay();
      resetButtons();

      if(newGame.getCurrentPlayerType().equals(Player.COMPUTER))
         ((Button) findViewById(R.id.best_move)).setText("PRESS FOR COMPUTER'S MOVE");
      else
         ((Button) findViewById(R.id.best_move)).setText("PRESS FOR BEST MOVE");
   }


   //
   // parameters :- (null)
   // returns    :- true --> if there are no valid moves for either player, false --> one or both player has valid moves
   //
   // updates the on screen board to reflect the current state on the game board
   // also checks if the board has remaining tiles that are all of the same color indicating the game has no more moves
   private boolean updateBoard() {
      char[][] board = newGame.getCurrentBoard();
      int count = 0;

      char currentChar = Board.WHITE;
      if (newGame.currentPlayer()) // true means its black's turn
         currentChar = Board.BLACK;

      boolean remFlag = true;

      for (int i = 0; i < Board.BOARD_DIM; i++) {
         for (int j = 0; j < Board.BOARD_DIM; j++) {
            String str = " ";

            if (board[i][j] == Board.BLACK) {
               str = BLACK_PIECE;
            } else if (board[i][j] == Board.WHITE) {
               str = WHITE_PIECE;
            }

            ((Button) findViewById(count)).setText(str);
            ((Button) findViewById(count)).setTextSize(36);

            // this condition is true if the current player has any tile remaining on the board
            if (remFlag && (board[i][j] == currentChar))
               remFlag = false;

            count++;
         }
      }

      return remFlag;
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   private void updateScore() {
      ((EditText) findViewById(R.id.player1_score)).setText("" + newGame.getPlayer1Score());
      ((EditText) findViewById(R.id.player2_score)).setText("" + newGame.getPlayer2Score());
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   private void updateCurrentPlayerDisplay() {
      // set the current player box
      EditText playerDisplay = ((EditText) findViewById(R.id.current_move));

      if(newGame.getCurrentPlayerType().equals(Player.COMPUTER))
         playerDisplay.setText("C");
      else
         playerDisplay.setText("H");

      // true means the current player is black
      if (newGame.currentPlayer()) {
         playerDisplay.setBackgroundColor(Color.BLACK);
         playerDisplay.setTextColor(Color.WHITE);
      } else {
         playerDisplay.setBackgroundColor(Color.WHITE);
         playerDisplay.setTextColor(Color.BLACK);
      }
   }


   //
   // parameters :- (null)
   // returns    :- (null)
   //
   private void resetButtons() {
      for (int i = 0; i < (Board.BOARD_DIM * Board.BOARD_DIM); i++) {
         ((Button) findViewById(i)).setTextColor(Color.BLACK);
         ((Button) findViewById(i)).clearAnimation();
         if (((Button) findViewById(i)).getText().equals(BLANK_PIECE))
            ((Button) findViewById(i)).setText("");
      }

      // clearing the moves ArrayList
      moves.clear();
   }


   //
   // parameters :- in --> a string which is displayed to the GameActivity
   // returns    :- (null)
   //
   private void displayMessage(String in) {
      ((EditText) findViewById(R.id.move_message)).setText(in);
   }


   //
   // parameters :- view --> the current view
   // returns    :- (null)
   //
   // saves the current board state
   public void save_button(View view) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("enter a name for the file");

      // Set up the input
      final EditText input = new EditText(this);
      builder.setView(input);

      // Set up the buttons
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            String fileName = input.getText().toString() + ".txt";
            try {
               File writeFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + fileName);
               if (!writeFile.exists())
                  writeFile.createNewFile();

               FileOutputStream out = new FileOutputStream(writeFile);
               out.write(("Black: " + newGame.getPlayer1Score() + '\n').getBytes());
               out.write(("White: " + newGame.getPlayer2Score() + '\n').getBytes());
               out.write(("Board:" + '\n').getBytes());

               for (int i = 0; i < Board.BOARD_DIM; i++) {
                  for (int j = 0; j < Board.BOARD_DIM; j++) {
                     out.write((newGame.getTileAt(i, j) + " ").getBytes());
                  }
                  out.write('\n');
               }

               String player = "White";
               if (newGame.currentPlayer()) player = "Black";
               out.write(("Next Player: " + player + '\n').getBytes());

               if (newGame.getPlayer1Type().equals(Player.HUMAN))
                  out.write(("Human: Black" + '\n').getBytes());
               else
                  out.write(("Human: White" + '\n').getBytes());

               out.close();

               displayMessage("Game saved as " + fileName);
            } catch (Exception ex) {
               System.out.println(ex.toString());
               displayMessage("Game failed to save");
            }
         }
      });

      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
         }
      });

      builder.show();
   }

}
