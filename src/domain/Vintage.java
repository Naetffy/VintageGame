package domain;

import java.io.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;


/**
 * The Vintage class represents a game board for the "Vintage" game.
 * It includes methods for playing moves, scoring, saving and loading game states,
 * and initializing the game board with gems and players.
 *
 * The game board is a square grid, and players take turns swapping adjacent gems
 * to create matches of three or more in a row or column.
 *
 * @author Mateo Forero, Juan Murcia
 * @version 1.0
 */
public class Vintage implements Serializable{

       // Instance variables
	public final int size;
	private int[][] game;
	private boolean[][] visited;
	public LinkedList<int[][]> nextStates;
	public static final int numGems = 7;
	public String player1;
	public String player2;
	public int player1Score;
	public int player2Score;
	private boolean turn = true;
	
	/**
        * Constructs a Vintage object with the specified size.
        *
        * @param n The size of the square game board.
        */
	public Vintage(int n) {
		size = n;
		init(n);
		this.player1 = "Player1";
		this.player2 = "Player2";
		
	}


	/**
     	* Constructs a Vintage object with the specified size and player names.
     	*
     	* @param n        The size of the square game board.
     	* @param player1  The name of player 1.
    	* @param player2  The name of player 2.
     	*/
	public Vintage(int n,String player1, String player2) {
		size = n;
		init(n);
		if (player1.length()==0)player1 = "Player1";
		if (player2.length()==0)player2 = "Player2";
		this.player1 = player1;
		this.player2 = player2;
	}

	/**
	* Performs a move by swapping gems at the specified locations.
	*
	* @param first  The coordinates of the first gem.
	* @param second The coordinates of the second gem.
	* @return true if the move is valid, false otherwise.
	*/
	public boolean play(int[]first,int[]second) {
		boolean valid=false;
		double dis = Math.sqrt(Math.pow(first[0]-second[0],2)+Math.pow(first[1]-second[1],2));
		if ( (first[0]!=second[0] || first[1]!=second[1]) && dis < 2.0 ){
			int temporary = game[first[0]][first[1]];
			game[first[0]][first[1]] = game[second[0]][second[1]];
			game[second[0]][second[1]] = temporary;
			play(first[0],first[1]);
			play(second[0],second[1]);
			if (turn) turn = false;
			else turn = true;
			valid=true;
		}
		return valid;
	}


	/**
     	* Performs actions related to a gem at the specified location.
     	*
     	* @param row    The row of the gem.
     	* @param column The column of the gem.
     	*/
	public void play(int row, int column) {
		int score = 0;
		int[][] move = diagonalMove(row,column);
		score += setScoreMoves(move);
		move = diagonalIMove(row,column);
		score += setScoreMoves(move);
		move = horizontalMove(row,column);
		score += setScoreMoves(move);
		move = verticalMove(row,column);
		score += setScoreMoves(move);
		sumScore(score);
	}

	/**
	* Updates the game state and calculates the score based on the given moves.
	*
	* @param moves A 2D array representing the coordinates of the matched gems.
	* @return The score achieved by the moves.
	*/
	private int setScoreMoves(int [][] moves) {
		int score=0;
		if (moves != null){
			for(int i = 0 ;i < moves.length;i++) {
				game[moves[i][0]][moves[i][1]] = -1;
				if (!visited[moves[i][0]][moves[i][1]]) {
					visited[moves[i][0]][moves[i][1]]=true;
					score++;
				}
			}
			for(int i = 0 ;i < moves.length;i++) {
				makeFall(moves[i][0],moves[i][1]);
			}
			nextStates.add(game);
			for(int i = 0 ;i < moves.length;i++) {
				for(int j = moves[i][0] ; j >= 0 ; j--)
					play(j,moves[i][1]);
			}
		}
		return score;
	}


	/**
	* Checks for matched gems in the diagonal direction starting from the specified cell.
	*
	* @param row    The row of the cell.
	* @param column The column of the cell.
	* @return A 2D array containing the coordinates of the matched gems if found; otherwise, returns null.
	*/
	private int[][] diagonalMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column-up-1) && game[row-up-1][column-up-1] == game[row][column] && up < 3) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column+dw+1) && game[row+dw+1][column+dw+1] == game[row][column] && dw < 3) 
			dw++;
		int[][] res = null; 
		if (up+dw >= 2) {
			res = new int[up+dw+1][2];
			int dis = up + dw;
			int inR = row-up;int inC = column-up;
			for(int i = 0; i <= dis;i++) 
				res[i] = new int[] {inR+i,inC+i};	
		}
		return res;
	}


	/**
	* Checks for matched gems in the reverse diagonal direction starting from the specified cell.
	*
	* @param row    The row of the cell.
	* @param column The column of the cell.
	* @return A 2D array containing the coordinates of the matched gems if found; otherwise, returns null.
	*/
	private int[][] diagonalIMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column+up+1) && game[row-up-1][column+up+1] == game[row][column] && up < 3) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column-dw-1) && game[row+dw+1][column-dw-1] == game[row][column] && dw < 3) 
			dw++;
		int[][] res = null; 
		if (up + dw >= 2) {
			res = new int[up+dw+1][2];
			int dis = up + dw;
			int inR = row-up;int inC = column+up;
			for(int i = 0; i <= dis;i++) 
				res[i] = new int[] {inR+i,inC-i};	
		}
		return res;
	}


	/**
	* Checks for matched gems in the horizontal direction starting from the specified cell.
	*
	* @param row    The row of the cell.
	* @param column The column of the cell.
	* @return A 2D array containing the coordinates of the matched gems if found; otherwise, returns null.
	*/
	private int[][] horizontalMove(int row, int column) {
		int lf = 0;
		while (verify(row,column-lf-1) && game[row][column-lf-1] == game[row][column] && lf < 3) 
			lf++;
		int rg = 0;
		while (verify(row,column+rg+1) && game[row][column+rg+1] == game[row][column] && rg < 3) 
			rg++;
		int[][] res = null; 
		if (lf + rg >= 2) {
			res = new int[lf+rg+1][2];
			int dis = rg + lf;
			int inR = row;int inC = column-lf;
			for(int i = 0; i <= dis;i++) 
				res[i] = new int[] {inR,inC+i};
		}
		return res;
	}

	/**
 	* Checks for matched gems in the vertical direction starting from the specified cell.
 	*
 	* @param row    The row of the cell.
	* @param column The column of the cell.
 	* @return A 2D array containing the coordinates of the matched gems if found; otherwise, returns null.
 	*/
	private int[][] verticalMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column) && game[row-up-1][column] == game[row][column] && up < 3) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column) && game[row+dw+1][column] == game[row][column] && dw < 3) 
			dw++;
		int[][] res = null; 
		if (up + dw >= 2) {
			res = new int[up+dw+1][2];
			int dis = up + dw;
			int inR = row-up;int inC = column;
			for(int i = 0; i <= dis;i++) 
				res[i] = new int[] {inR+i,inC};
		}
		return res;
	}

	/**
	* Performs the falling animation for gems in the specified column after some gems have been removed.
	*
	* @param r The row of the cell.
	* @param c The column of the cell.
	*/
	private void makeFall(int r,int c) {
		ArrayList<Integer> arr1 = new ArrayList<Integer>();	
		for(int i = r ; i >= 0 ; i--) {
			if (game[i][c] != -1) {
				arr1.add(game[i][c]);
			}
		}
		int cont = 0;
		for(int i = r ; i >= 0 ; i--) {
			if (cont < arr1.size()) {
				game[i][c] = arr1.get(cont);
				cont++;
			}
			else 
				game[i][c] = bestAssigment(i,c);
		} 
	}
	
	public int[][] getState() {
		return game;
	}
	
	public void setGameState(int[][] newState) {
		game = newState;
	}
	
	public boolean[][] getVisited() {
		return visited;
	}



	/**
	* Saves the current game state to a specified file.
	*
	* @param nombreArchivo The name of the file to save the game state.
	*/
	public void guardarPartida(String nombreArchivo) {
	    ObjectOutputStream salida = null;
	    try {
	        salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo));
	        salida.writeObject(this);
	        salida.flush(); 
	        System.out.println("Partida guardada con éxito. Ruta: " + new File(nombreArchivo).getAbsolutePath());
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (salida != null) {
	            try {
	                salida.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}


	/**
 	* Loads a game state from a specified file and returns a Vintage object.
 	*
 	* @param nombreArchivo The name of the file to load the game state.
 	* @return A Vintage object representing the loaded game state.
	*/
	public static Vintage cargarPartida(String nombreArchivo) {
		Vintage partidaCargada = null;
	    try {
	    	ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(nombreArchivo));
	    	partidaCargada = (Vintage) entrada.readObject();

	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } 
	    return partidaCargada;
	}

	/**
	* Updates the score based on the provided value.
	*
	* @param s The score to be added.
	*/
	private void sumScore(int s){
		if (turn)player1Score += s;
		else player2Score += s;
	}


	/**
 	* Checks whether the given coordinates are within the bounds of the game board.
 	*
 	* @param r The row coordinate.
 	* @param c The column coordinate.
 	* @return true if the coordinates are within bounds, otherwise false.
 	*/
	private boolean verify(int r, int c) {
		boolean flag = false;
		if (0 <= r && r < size && 0 <= c && c < size) flag = true;
		return flag;
	}
	
	
	
	/**
 	* Initializes the game by creating the game board, setting initial values, and preparing the game state.
	*
 	* @param n The size of the game board.
 	*/
	private void init(int n) {
		game = new int[n][n];
		for(int i = 0;i < n;i++) {
			for(int j = 0;j < n; j++) {
				game[i][j]=-1;
			}
		}
		visited = new boolean[n][n];
		for(int i = 0;i < n;i++) {
			for(int j = 0;j < n; j++) {
				game[i][j] = bestAssigment(i,j);
				visited[i][j] = false;
			}
		}
		nextStates=new LinkedList<int[][]>();
		modifyBoard();
		player1Score = 0;
		player2Score = 0;
	}
	
	/**
 	* Modifies the entire game board by assigning the best possible assignment for each cell.
	*/
	private void modifyBoard() {
		for(int i = 0;i < size;i++) {
			for(int j = 0;j < size; j++) {
				game[i][j] = bestAssigment(i,j);
			}
		}
	}

	/**
 	* Determines the best assignment for a given cell based on the surrounding gems.
 	*
 	* @param row    The row of the cell.
 	* @param column The column of the cell.
	 * @return The best assignment for the specified cell.
	 */
	public int bestAssigment(int row, int column) {
		int bestMove = 0;
		boolean[] fail = new boolean[numGems];
		while(bestMove < numGems) {
			game[row][column] = bestMove;
			if (diagonalMove(row,column) != null) fail[bestMove] = true;
			if (diagonalIMove(row,column) != null) fail[bestMove] = true;
			if (horizontalMove(row,column) != null) fail[bestMove] = true;
			if (verticalMove(row,column) != null) fail[bestMove] = true;
			bestMove++;
		}
		ArrayList<Integer> works = new ArrayList<Integer>();
		for (int i = 0 ; i < numGems; i++) {
			if (!fail[i])works.add(i);
		}
		int r;
		Random random = new Random();
		if (works.size() > 0) {
			r = random.nextInt(works.size());
			r = works.get(r);
		}
		else { 
			r = random.nextInt(numGems);
		}
		return r;
	}
	
}
