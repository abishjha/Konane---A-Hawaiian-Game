<html>
<body>
<h1>Konane</h1>

<h2>The Players</h2>

Konane is a Hawaiian game for two players. In our case, the two players will be the computer and the human user
of your program.
The computer must play to win.

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

It gives the human player the option of choosing the left or the right hand,
each hand bearing one of the two picked stones. The color of the stone 
in the hand picked by the human player will now be the human player's color.

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

<li> 
A move should be accepted from the human player only if it is legal according to the 
rules of the game.
<li> 
After each turn, the scores of the two players should be displayed.
</ul>

<h2>Serialization from and to text file</h2>

<ul>
<li> Read the current state of the game from a serialized text file and
load it. The format for serialization is as follows:
<hr>
<pre>
  Black: 6
  White: 4
  Board:
  B W B W B W
  W B O B O B
  B W O W B W
  O B O O O O
  B W B O O W
  W B W O O B  
  Next Player: White
</pre>
<hr>
In the above game, black has 6 points after having captured 6 white stones and white has 4 points
after having captured 4 black stones. It is white player's turn next.
</ul>


<h2>Search algorithms for Konane</h2>

<b>Purpose:</b> Heuristics, blind searches (depth-first and breadth-first searches), heuristic searches (best-first search  and branch-and-bound search)
<p>

First, implement the following algorithms:
<ul>
  <li> Depth-first search 
  <li> Breadth-first search
  <li> Best-first search, which uses the score as its heuristic function
</ul>
The players should be able to use these algorithms to determine their next N possible moves.
<br>
In addition, use Branch-and-Bound algorithm to calculate the best possible score for the given board and player. Display the score as well as the sequence of jumps that earn the score.
Provide interface for the user to constrain the depth of the tree
considered by branch and bound. The interface should also let the user select that the entire tree should be searched.

 </body>

</html>
