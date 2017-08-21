/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
//Implementation of studPlayer class by Erin Winter
//Alpha-beta pruning implementation using IDS
//Variation of a-b pruning from the lecture slides
//################################################################
import java.util.ArrayList;
public class defaultSBEPlayer extends Player {


	/*Use IDS search to find the best move. The step starts from 1 and increments by step 1.
	 *Note that the search can be interrupted by time limit.
	 */

	public void move(GameState state)
	{
		int maxDepth = 1;
		int bestMove = 0;
		//run forever until the time runs out
		while (true){
			//reset the maxSBE after each iteration 
			//(ie, assume that the move at the root found by percolating from depth k + 1 is always better than the best child found from percolating from depth k)
			int maxSbe = Integer.MIN_VALUE;
			for (int i = 0; i < 6; i++){
				if (state.illegalMove(i) == false){
					int sbe;
					GameState x = new GameState(state);
					boolean moveAgain = x.applyMove(i);
					//because the children of the root are populated in move, its children are min nodes
					if (!moveAgain){
						sbe = minAction(x, maxDepth);
					}
					//unless the AI gets to move again
					else{
						sbe = maxAction(x, maxDepth);
					}
					//find the best SBE for each iteration of IDS, not in total (this is a common bug)
					if (sbe > maxSbe){
						maxSbe = sbe;
						bestMove = i;
					}
				}
			}
			//set move instance variable
			move = bestMove;
			
			//increment depth of IDS
			maxDepth++;
		}
	}

	//Return best move for max player. Note that this is a wrapper function created for ease to use.
	public int maxAction(GameState state, int maxDepth)
	{   
		return maxAction(state, 0, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	//return best move for min player. Note that this is a wrapper function created for ease to use.
	public int minAction(GameState state, int maxDepth)
	{
		return minAction(state, 0, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	//return best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		if ((currentDepth == maxDepth) || (state.gameOver())){
			return sbe(state);
		}
		int v = Integer.MIN_VALUE;
		//AI can pick a move from bins 0 through 5
		for (int i = 0; i < 6; i++){
			GameState child = new GameState(state);
			//only search the children of legal moves
			if (child.illegalMove(i) == false){
				//check for repeat moves
				boolean moveAgain = child.applyMove(i);
				//if the AI gets to move again, pick the max of max node children
				if (moveAgain){
					v = Math.max(v,maxAction(child, currentDepth+1, maxDepth, alpha, beta));
				}
				//otherwise, opponent gets to move, pick the max of the min node children
				else{
					v = Math.max(v, minAction(child, currentDepth+1, maxDepth, alpha, beta));
				}
				//beta cutoff
				if (v >= beta){
					return v;
				}
				alpha = Math.max(alpha, v);

			}
		}
		return v;
	}
	//return best move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		if ((currentDepth == maxDepth) || (state.gameOver())){
			return sbe(state);
		}
		int v = Integer.MAX_VALUE;
		//remember, the board isn't rotated, so the opponent has bins 7 through 12 available to move
		for (int i = 7; i < 13; i++){
			GameState child = new GameState(state);
			if (child.illegalMove(i) == false){
				//check for repeat moves
				boolean moveAgain = child.applyMove(i);
				//apply minAction again if opponent gets to move again
				if (moveAgain){
					v = Math.min(v,minAction(child, currentDepth+1, maxDepth, alpha, beta));
				}
				//else search for the min of the maxAction children
				else{
					v = Math.min(v, maxAction(child, currentDepth+1, maxDepth, alpha, beta));
				}
				//alpha cutoff, straight from the lecture slides
				if (alpha >= v){
					return v;
				}
				beta = Math.min(beta, v);

			}
		}
		return v;
	}


	//the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
	private int sbe(GameState state)
	{   
		// numbers of stones in mancala of the current player minus the number in the mancala of the opponent
		return state.stoneCount(6) - state.stoneCount(13);
	}


}
