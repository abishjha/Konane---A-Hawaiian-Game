package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 2 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     March 5, 2018                                  *
 ************************************************************
*/

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Search {
    private final int VERTICES = Board.BOARD_DIM * Board.BOARD_DIM;

    private List<List<Integer>> adjacencyList; // to store the graph
    private char[][] currBoard; // the state of the board on which the searches are to be run
    // a list which holds the output in order
    private LinkedList<String> result; // data is stored as 'RC:' where R is row, C is column, and : is separator
    private boolean alphabeta; // switch for alpha beta pruning


    //
    // parameters :- algoChoice --> the selected algorithm for the search, boardState --> current state of the board,
    //               currPlayer --> the current player for whom to run the search,
    //               depth --> cut-off depth for the branch and bound algorithm
    // returns    :- (null)
    //
    public Search(String algoChoice, char[][] boardState, char currPlayer, int depth) {
        adjacencyList = new ArrayList<>(VERTICES);
        result = new LinkedList<>();
        currBoard = boardState;
        alphabeta = false;

        // making the first list point to VERTICES number of ArrayList's
        for (int i = 0; i < VERTICES; i++)
            adjacencyList.add(new ArrayList<Integer>());

        populateList(currPlayer);

        switch(algoChoice){
            case "DepthFS":
                DepthFS();
                break;
            case "BreadthFS":
                BreadthFS();
                break;
            case "BestFS":
                BestFS();
                break;
            case "BranchAndBound":
                BranchAndBound(depth);
                break;
            case "MiniMax":
                MiniMax(depth, currPlayer); // depth is the ply cut-off for MiniMax
                break;
            case "MiniMaxAB":
                alphabeta = true;
                MiniMax(depth, currPlayer); // depth is the ply cut-off for MiniMax
                break;
            default:
                break; // do nothing
        }
    }


    //
    // parameters :- currPlayer --> the current player so his moves can be populated in the adjacency list
    // returns    :- (null)
    //
    // fill the adjacency list for the given player
    private void populateList(char currPlayer) {
        for (int i = 0; i < VERTICES; i++) {
            int row = getRow(i), col = getCol(i);

            // only populate board for current player moves irrespective of there being a stone on the tile
            if(((currPlayer == Board.BLACK) && ((row + col) % 2 == 0)) || ((currPlayer == Board.WHITE) && ((row + col) % 2 == 1))) {
                // checking for moving two rows up -- north
                if ((row - 2) >= 0) {
                    if(currBoard[row-1][col] != Board.SPACE)
                        adjacencyList.get(i).add(getNumber(row-2, col));
                }

                // checking for moving two columns right -- east
                if ((col + 2) < Board.BOARD_DIM) {
                    if(currBoard[row][col+1] != Board.SPACE)
                        adjacencyList.get(i).add(getNumber(row, col+2));
                }

                // checking for moving two rows down -- south
                if ((row + 2) < Board.BOARD_DIM) {
                    if(currBoard[row+1][col] != Board.SPACE)
                        adjacencyList.get(i).add(getNumber(row+2, col));
                }

                // checking for moving two columns left -- west
                if ((col - 2) >= 0) {
                    if(currBoard[row][col-1] != Board.SPACE)
                        adjacencyList.get(i).add(getNumber(row, col-2));
                }
            }
            // set moves for other player to empty
            else{
                adjacencyList.get(i).clear();
            }
        }
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    // launcher/entry point for the depth first search
    private void DepthFS(){
        for (int i = 0; i < VERTICES; i++){
            // if a move exists from tile 'i' and 'i' has a tile, we launch a DFS
            if(adjacencyList.get(i).size() != 0 && currBoard[getRow(i)][getCol(i)] != Board.SPACE) {
                String moveTrace = pad(i);
                DFS(i, moveTrace, -1);
            }
        }
        processData();
    }


    //
    // parameters :- start --> initial node for the search,
    //               currMove --> a string that stores the sequence of the current move in 'RC:' form
    //               prevTile --> the id of the previous tile to check for back moves
    // returns    :- (null)
    //
    // recursive algorithm for the depth first search
    private void DFS(int start, String currMove, int prevTile) {
        for (int v : adjacencyList.get(start)) {
            // if the next move is moving back to the previous node, skip it
            if(prevTile != -1 && v == prevTile)
                continue;

            // if the current node has a piece and the piece is not already moved in the current move,
            // we cannot jump to it i.e. that tile still has a piece, so continue
            if(currBoard[getRow(v)][getCol(v)] != Board.SPACE && !currMove.contains(pad(v)))
                continue;

            // if the jump being considered has already taken place in the current move,
            // the tile to be jumped over is already out, so continue
            if(currMove.contains(pad(start) + pad(v)) || currMove.contains(pad(v) + pad(start)))
                continue;

            // add the jump to v in the current move trace
            currMove += pad(v);

            // condition: a move exists from the current tile
            if (adjacencyList.get(v).size() != 0)
                DFS(v, currMove, start);

            // if the size of result list is 0, or if a superset of the move is not contained in the result already, we append to the result
            if (result.size() == 0 || !result.getLast().contains(currMove))
                result.addLast(currMove); // push current string to queue

            // from string remove last position i.e. the DFS has reached a node with no more child to visit
            // so we are moving a step back in the tree
            currMove = currMove.substring(0, (currMove.length() - 3));
        }
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    private void BreadthFS(){
        LinkedList<String> queueBFS = new LinkedList<>();

        for (int i = 0; i < VERTICES; i++){
            // if a move exists from tile 'i' and 'i' has a tile, we push the tile onto the queue
            if(adjacencyList.get(i).size() != 0 && currBoard[getRow(i)][getCol(i)] != Board.SPACE)
                queueBFS.addLast(pad(i));
        }

        while(!queueBFS.isEmpty()){
            String startPath = queueBFS.removeFirst(); // the first element in the queue
            int start = getNumber(startPath); // get the number of the current tile

            for(int v : adjacencyList.get(start)) {
                // if the jump being considered has already taken place in the current move,
                // the tile to be jumped over is already out, so continue
                String temp = pad(start) + pad(v);
                String temp2 = pad(v) + pad(start);
                if(startPath.contains(temp) || startPath.contains(temp2)) continue;

                String path = startPath;
                if(path.length() > 6)
                    path = path.substring(0, path.length()-6);

                // if the node has not been visited in the current move and the node has no tile, or,
                // if the node has a tile and the tile has already been visited in the current move
                //   but not immediately before, we can make a move as we know the tile was moved
                if ((!startPath.contains(pad(v)) && currBoard[getRow(v)][getCol(v)] == Board.SPACE)
                        || (currBoard[getRow(v)][getCol(v)] != Board.SPACE && path.contains(pad(v)))) {
                    queueBFS.addLast(startPath + pad(v));
                    result.addLast(startPath + pad(v));
                }
            }
        }

        processData();
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    private void BestFS() {
        DepthFS();
        if (result.size() == 0) return;

        // ordering the result set so the best moves appear in the front
        // i.e. using score as a heuristic function
        boolean flag = true;
        String temp;
        while(flag){
            flag = false;
            for(int i = 0; i < result.size()-1; i++){
                if(result.get(i).length() < result.get(i+1).length()){
                    temp = result.get(i);
                    result.set(i, result.get(i+1));
                    result.set(i+1, temp);
                    flag = true;
                }
            }
        }
    }


    //
    // parameters :- depth --> the cut-off depth for the branch and bound algorithm's tree
    // returns    :- (null)
    //
    private void BranchAndBound(int depth) {
        DepthFS();
        int max = 0;
        for(int i = 0; i < result.size(); i++)
            if(result.get(i).length() > max)
                max = result.get(i).length();

        depth = depth * 3 + 3;
        if(depth > max || depth == 3)
            depth = max;

        String max_element = "";
        for(int i = 0; i < result.size(); i++){
            if(result.get(i).length() == depth){
                max_element = result.get(i);
                break;
            }

            if(result.get(i).length() > depth){
                max_element = result.get(i).substring(0, depth);
                break;
            }
        }
        result.clear();
        if(!max_element.equals(""))
            result.add(max_element);
    }


    //
    // parameters :- depth --> the ply cut-off for the minimax algorithm's tree,
    //               currPlayer --> the current player who is the maximizing
    // returns    :- (null)
    //
    private void MiniMax(int depth, char currPlayer){
        boolean maximizing = true; // true for maximizing, false otherwise
        String bestMove = "";

        MiniMaxNode temp = new MiniMaxNode(currBoard, "", currPlayer, 0, 0);
        int maxHeuristic = Integer.MIN_VALUE;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        if (currPlayer == Board.BLACK) currPlayer = Board.WHITE;
        else if (currPlayer == Board.WHITE) currPlayer = Board.BLACK;

        for(int i = 0; i < temp.getAvailableMoves().size(); i++) {
            int tmpInt = MiniMaxRecursion(depth - 1, !maximizing, temp.getBoard(), temp.getAvailableMoves().get(i),
                    currPlayer, temp.getOpponentScore(), temp.getMyScore(), false, alpha, beta);

            if(tmpInt > maxHeuristic){
                maxHeuristic = tmpInt;
                bestMove = temp.getAvailableMoves().get(i);
            }
        }

        result.clear();
        if(!bestMove.equals(""))
            result.add(bestMove);
    }


    //
    // parameters :- depth --> the depth of the current node in the tree,
    //               maximizing --> true if the current level is maximizing, false for minimizing,
    //               board --> boarstate at that node,
    //               move --> the move which has to be done to reach that node from its parent node,
    //               player --> the player for the current level,
    //               myScore --> the score of the current player,
    //               opponentScore --> the score of the opponent player,
    //               multiPass --> flag to check if two passes are made one after the other,
    //               alpha --> the value of alpha for the node,
    //               beta --> the value of beta for the node,
    // returns    :- (null)
    //
    private int MiniMaxRecursion(int depth, boolean maximizing, char[][] board,
                                  String move, char player, int myScore, int opponentScore, boolean multiPass, int alpha, int beta) {

        if (depth < 0 && maximizing) return (opponentScore - myScore);
        else if (depth < 0 && !maximizing) return (myScore - opponentScore);

        MiniMaxNode temp = new MiniMaxNode(board, move, player, myScore, opponentScore);

        if (player == Board.BLACK) player = Board.WHITE;
        else if (player == Board.WHITE) player = Board.BLACK;

        // check to see if there are any moves for the other player when the current has none left
        if (temp.isLeafNode() && !multiPass) {
            return MiniMaxRecursion(depth - 1, !maximizing, temp.getBoard(), "",
                    player, temp.getOpponentScore(), temp.getMyScore(), true, alpha, beta);
        }
        // this means both players have to pass which means the game is over
        else if (temp.isLeafNode() && multiPass) {
            if (maximizing) return (opponentScore - myScore);//temp.getHeuristic();
            else return (myScore - opponentScore);
        }

        int max = 0, min = 0;
        if (maximizing) max = Integer.MIN_VALUE;
        else min = Integer.MAX_VALUE;

        for (int i = 0; i < temp.getAvailableMoves().size(); i++) {
            int val = MiniMaxRecursion(depth - 1, !maximizing, temp.getBoard(), temp.getAvailableMoves().get(i),
                    player, temp.getOpponentScore(), temp.getMyScore(), false, alpha, beta);

            if (maximizing && val > max)
                max = val;

            if (!maximizing && val < min)
                min = val;

            if(alphabeta) {
                if (maximizing && val > alpha) alpha = val;
                else if (!maximizing && val < beta) beta = val;
            }

            if(alphabeta && alpha >= beta) break;
        }

        if (maximizing) return max;
        else return min;
    }


    //
    // parameters :- (null)
    // returns    :- a linked list of string containing the available moves in order of visiting
    //
    public LinkedList<String> getOutput(){
        return result;
    }


    //
    // parameters :- (null)
    // returns    :- (null)
    //
    // processes data in result list from vertex number to tile coordinates
    private void processData(){
        for(int i = 0; i < result.size(); i++){
            String element = result.removeFirst();
            StringBuilder converted = new StringBuilder();

            for(int j = 0; j < element.length(); j += 3)
                converted.append(getCoordinates(getNumber(element.substring(j, j + 3))) + ":");

            result.addLast(converted.toString());
        }
    }


    //
    // parameters :- num --> the number to pad as integer
    // returns    :- a string containing the input number padded to two places followed by a ':'
    //
    // pad given number to a 2 digit string by prepending appropriate number of 0's and ':' at the end
    private String pad(int num){
        if(num < 10)
            return "0" + num + ":";
        return "" + num + ":";
    }


    //
    // parameters :- num --> the id (0 - 35) number of a tile
    // returns    :- an integer representing the row of the input tile
    //
    // get row from vertex number
    private int getRow(int num){
        return num / Board.BOARD_DIM;
    }


    //
    // parameters :- num --> the id (0 - 35) number of a tile
    // returns    :- an integer representing the column of the input tile
    //
    // get column from vertex number
    private int getCol(int num){
        return num % Board.BOARD_DIM;
    }


    //
    // parameters :- row --> the row of a tile, col --> the column of a tile
    // returns    :- an integer representing the id (0 - 35) of the input tile
    //
    // get vertex number from row and column
    private int getNumber(int row, int col){
        return (Board.BOARD_DIM * row) + col;
    }


    //
    // parameters :- in --> the coordinates of a tile in the form 'RC:'
    // returns    :- an integer representing the id (0 - 35) of the input tile
    //
    // get number from button format of type "RC:"
    private int getNumber(String in){
        String temp = in.substring(in.length()-3, in.length()-1);
        return Integer.parseInt(temp);
    }


    //
    // parameters :- num --> the id (0 - 35) number of a tile
    // returns    :- a string containing the coordinates of a tile in the form 'RC:'
    //
    private String getCoordinates(int num){
        return "" + getRow(num) + getCol(num);
    }
}


/*           // branch and bound and best first search using breadth first search //
    //
    // parameters :- depth --> the cut-off depth for the branch and bound algorithm's tree
    // returns    :- (null)
    //
    // implementation using breadth first search
    private void BranchAndBound(int depth) {
        BreadthFS();
        if (result.size() == 0) return;

        depth = depth * 3 + 3; // because each of our move is stored as three characters i.e. RC:
                               // and there are three characters for the starting tile which does not earn a point

        if (depth > result.getLast().length() || depth == 3)
            depth = result.getLast().length();
        String max_element = "";

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).length() == depth) {
                max_element = result.get(i);
                break;
            }

            if(result.get(i).length() > depth) {
                max_element = result.get(i - 1);
                break;
            }
        }
        result.clear();
        result.add(max_element);
    }

    //
    // parameters :- (null)
    // returns    :- (null)
    //
    // implementation using breadth first search
    private void BestFS() {
        BreadthFS();

        int min_length = 6, count = 0;
        LinkedList<String> temp_list = new LinkedList<>();

        for(int i = 0; i < result.size(); ) {
            if(result.getFirst().length() == min_length){
                temp_list.add(count, result.removeFirst());
                count++;
            }
            else{
                temp_list.addFirst(result.removeFirst());
                count = 1;
                min_length += 3;
            }
        }

        result = temp_list;
    }

 */
