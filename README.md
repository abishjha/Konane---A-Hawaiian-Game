# Konane---A-Hawaiian-Game
a one player game played versus the computer with AI which uses MiniMax algorithm for moves

platform:       android 4.4 or higher

developer: Abish Jha


about the game: 

Konane
The Players
Konane is a Hawaiian game for two players.
The Board
The board consists of 36 puka or slots, arranged in 6 rows X 6 columns. Initially, it is filled with 18 black stones (ili ele) and 18 white stones (ili kea) as follows:
  B W B W B W
  W B W B W B
  B W B W B W
  W B W B W B
  B W B W B W
  W B W B W B  
  
The Objective
The player who earns the most points wins.
The Game
In the beginning, the computer randomly removes one black and one white stone from the board.
The player playing black stones plays first, followed by the player playing white stones. After this, the players alternate.
If a player does not have a valid move, s/he passes. But, if a player does have a valid move, s/he must make a move.
The game ends when neither player can make a move.
The player who scores the most points is declared the winner of the game.

Rules of Movement
A player moves one of his/her stones by jumping over an adjacent stone belonging to the opponent, and landing in a vacant puka two removed from the current puka.
The player captures the opponent's stone during the jump, and removes it from the board.
A player may move in one of four directions - front, back, left or right when jumping. Diagonal moves are not permitted.
A player cannot jump over his/her own stones.
A player may jump over more than one of opponent's stones in one move, as long as each jump is legal at the time it is made.
The player earns one point for each captured stone.
Implementation
A move should be accepted from the human player only if it is legal according to the rules of the game.
After each turn, the scores of the two players should be displayed.

