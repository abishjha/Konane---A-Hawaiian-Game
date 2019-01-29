<html>
<body>
<h1>Konane</h1>

<h2>The Players</h2>

Konane is a Hawaiian game for two players.
<!--
In our case, the two players will be the computer and the human user
of your program.
The computer must play to win.
  -->

<h2>The Board</h2>

The board consists of 36 puka or slots, arranged in 6 rows X 6 columns.
Initially, it is filled with 18 black stones (ili ele) and 18 white stones (ili kea)
as follows:
<pre>
  B W B W B W
  W B W B W B
  B W B W B W
  W B W B W B
  B W B W B W
  W B W B W B  
  </pre>

<h2>The Objective</h2>

The player who earns the most points wins.

<h2>The Game</h2>

In the beginning, the computer randomly removes one black and one white stone from 
the board.
<!--
It gives the human player the option of choosing the left or the right hand,
each hand bearing one of the two picked stones. The color of the stone 
in the hand picked by the human player will now be the human player's color.
-->
<p>
  The player playing black stones plays first, followed by the player playing white stones.
  After this, the players alternate.<br>
  If a player does not have a valid move, s/he passes.
  But, if a player does have a valid move, s/he must make a move.<br>
  The game ends when neither player can make a move.<br>
  The player who scores the most points is declared the winner
  of the game.

<h2>Rules of Movement</h2>

A player moves <b>one</b> of his/her stones by jumping over an adjacent stone 
belonging to the opponent, and landing in a vacant puka two removed from the current
puka. 
<ul>
<li> The player captures the opponent's stone during the jump, and removes
it from the board.
<li> A player may move in one of four directions - front, back, left or right
when jumping. Diagonal moves are not permitted.
<li> A player cannot jump over his/her own stones.
<li> A player may jump over more than one of opponent's stones in one move, as long
as each jump is legal at the time it is made.
</ul>
The player earns one point for each captured stone.

<h2> Implementation</h2>

<ul>
<!--
  <li> When it is the human player's turn, the human player should have the option to 
obtain a list of all the possible moves available to him/her.
-->

<li> 
A move should be accepted from the human player only if it is legal according to the 
rules of the game.
<li> 
After each turn, the scores of the two players should be displayed.
</ul>

<!--
<h2>Grading</h2>

The following are some of the features on which your program will be graded:
<ul>
  <li> Setting up the board, properly updating  and displaying it
  <li> Providing the human player the option to view available moves
  <li> Ensuring that the move entered by the human player is legal
  <li> Correctly updating the board and the scores after each move
</ul>
-->

</html>
