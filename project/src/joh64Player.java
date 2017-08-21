import java.util.*;
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
// studPlayer class
//################################################################

public class joh64Player extends Player {
//	int alpha = Integer.MIN_VALUE; //Integer.MIN_VALUE infinite num
//	int beta = Integer.MAX_VALUE;

    /*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
     */
    public void move(GameState state)
    {
    	int depth = 1; 
    	while(true){	
    		this.move = maxAction(state, depth);
    		depth++;
    	}    	
    }

    // Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
    public int maxAction(GameState state, int maxDepth)
    {
    	int alpha = Integer.MIN_VALUE; 
    	int beta = Integer.MAX_VALUE;

    	int bin = 0;
    	GameState copyState = new GameState(state);//copy current state
    	ArrayList<GameState> list = new ArrayList<GameState>();
    	for(int i = 0; i < 6; i++){
    		list.add(new GameState(copyState.toArray()));
    	}
    	int maxSbe = Integer.MIN_VALUE;
    	for(int i = 0; i < 6; i++){
    		if (list.get(i).illegalMove(i)){
    			continue;
    		}
    		int tempSbe = Integer.MIN_VALUE;
    		list.get(i).applyMove(i);
			if(list.get(i).applyMove(i)){
				tempSbe = maxAction(list.get(i), 0, maxDepth, alpha, beta);
			}else{
				list.get(i).rotate();
				tempSbe = minAction(list.get(i), 1, maxDepth, alpha, beta);
			}
			if(tempSbe > alpha) alpha = tempSbe;
			
        	//System.out.println("tempSbe: "+tempSbe);
        	//System.out.println("MaxSbe: "+ maxSbe);
    		if(tempSbe > maxSbe){
    			maxSbe = tempSbe;
    			bin = i;
    		}
    	}
    	return bin;
    }
    
	//return sbe value related to the best move for max player
    public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	//System.out.println(state.toString());
    	//System.out.println("Max current: " + currentDepth + " alpha: " + this.alpha + " beta: " + this.beta);
    	if(currentDepth == maxDepth || state.gameOver()){
    		return sbe(state);
    	}
    	//currentDepth++;
    	int v = Integer.MIN_VALUE;
    	ArrayList<GameState> list = new ArrayList<GameState>();
    	for(int i = 0; i < 6; i++){
    		list.add(new GameState(state.toArray()));
    	}
    	for(int i = 0; i < 6; i++){
    		if(list.get(i).illegalMove(i)){
    			continue;
    		}
    		int temp;
			if(list.get(i).applyMove(i)){
				temp = maxAction(list.get(i), currentDepth, maxDepth, alpha, beta);
			}
			else { 
				list.get(i).rotate();
				temp = minAction(list.get(i), currentDepth+1, maxDepth, alpha,  beta);
			}
			v = Math.max(v, temp);
    		if(v >= beta){
    			return v;
    		}	
			alpha = Math.max(alpha, v);
  
    	}
    	return v;
    }
    //return sbe value related to the bset move for min player
    public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	//System.out.println("Min current: " + currentDepth + " alpha: " + this.alpha + " beta: " + this.beta);
    	if(currentDepth == maxDepth || state.gameOver()){
    		return sbe(state);
    	}
    	//currentDepth++;
    	int v = Integer.MAX_VALUE;
    	//generate children
    	ArrayList<GameState> list = new ArrayList<GameState>();
    	for(int i = 0; i < 6; i++){
    		list.add(new GameState(state.toArray()));
    	}
    	for(int i = 0; i < 6; i++){

    		if(list.get(i).illegalMove(i)){
    			continue;
    		}
    		int temp;
    		if(list.get(i).applyMove(i)){
    			temp = minAction(list.get(i), currentDepth, maxDepth, alpha, beta);
    		}else{
				list.get(i).rotate();
    			temp = maxAction(list.get(i), currentDepth+1, maxDepth, alpha, beta);
    		}
			v = Math.min(v, temp);
    		if(alpha >= v){
    			return v;
    		}
    		beta = Math.min(beta, v);
    	}
    	return v;
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(GameState state)
    {
//    	int sbe = 0;
//    	int enemy = state.stoneCount(1, 5)+state.stoneCount(1, 4)+state.stoneCount(1, 3)+
//    			state.stoneCount(1, 2)+state.stoneCount(1, 1)+state.stoneCount(1, 0);
//    	int mine = state.stoneCount(0, 5)+state.stoneCount(0, 4)+state.stoneCount(0, 3)+
//    			state.stoneCount(0, 2)+state.stoneCount(0, 1)+state.stoneCount(0, 0);
//    	if(state.stoneCount(0) == 0 && state.stoneCount(1) == 0 && state.stoneCount(2) == 0){
//    		sbe -= 10;
//    	}

//        -----------------------------------------
//        |    | 12 |  1 | 10 |  9 |  8 |  7 |    |
//        | 13 |-----------------------------|  6 |
//        |    |  0 |  1 |  2 |  3 |  4 |  5 |    |
//        -----------------------------------------</pre>
//       
//        -------------------------------------------------
//        |     |(1,5)|(1,4)|(1,3)|(1,2)|(1,1)|(1,0)|     |
//        |(1,6)|-----------------------------------|(0,6)|
//        |     |(0,0)|(0,1)|(0,2)|(0,3)|(0,4)|(0,5)|     |
    	int sbe = state.stoneCount(6) - state.stoneCount(13);
    	return sbe;

   
    }
}

