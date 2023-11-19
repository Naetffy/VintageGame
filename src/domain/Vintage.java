package domain;

import java.io.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;

public class Vintage implements Serializable{
	
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
	
	public Vintage(int n) {
		size = n;
		init(n);
		this.player1 = "Player1";
		this.player2 = "Player2";
		
	}
	public Vintage(int n,String player1, String player2) {
		size = n;
		init(n);
		if (player1 == null || player1.length()==0)player1 = "Player1";
		if (player2 == null || player2.length()==0)player2 = "Player2";
		this.player1 = player1;
		this.player2 = player2;
	}
	public String getWinner() {
		if(player1Score > player2Score)return player1;
		else if(player1Score < player2Score)return player2;
		else return null;
	}
	public boolean verifyWinner() {
		for(int i = 0;i < size;i++) {
			for(int j = 0;j < size; j++) {
				if (!visited[i][j])return false;
			}
		}
		return true;
	}
	
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
	
	public boolean getTurn() {
		return turn;
	}

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
	
	private void sumScore(int s){
		if (turn)player1Score += s;
		else player2Score += s;
	}
	
	private boolean verify(int r, int c) {
		boolean flag = false;
		if (0 <= r && r < size && 0 <= c && c < size) flag = true;
		return flag;
	}
	
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
	
	private void modifyBoard() {
		for(int i = 0;i < size;i++) {
			for(int j = 0;j < size; j++) {
				game[i][j] = bestAssigment(i,j);
			}
		}
	}
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