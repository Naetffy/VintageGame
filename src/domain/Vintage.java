package domain;

import java.io.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;

public class Vintage implements Serializable{
	
	public final int size;
	private int[][] game;
	private boolean[][] visited;
	public static final int numGems = 7;
	public String player1;
	public String player2;
	public int player1Score;
	public int player2Score;
	private boolean turn = true;
	
	public Vintage(int n) {
		size = n;
		game = new int[n][n];
		visited = new boolean[n][n];
		for(int i = 0;i < n;i++) {
			for(int j = 0;j < n; j++) {
				game[i][j] = bestAssigment(i,j);
				visited[i][j] = false;
			}
		}
		this.player1 = "Jugador1";
		this.player2 = "Jugador2";
		player1Score = 0;
		player2Score = 0;
	}
	public Vintage(int n,String player1, String player2) {
		size = n;
		game = new int[n][n];
		visited = new boolean[n][n];
		for(int i = 0;i < n;i++) {
			for(int j = 0;j < n; j++) {
				game[i][j] = bestAssigment(i,j);
				visited[i][j] = false;
			}
		}
		this.player1 = player1;
		this.player2 = player2;
		player1Score = 0;
		player2Score = 0;
	}
	
	public boolean play(int[]first,int[]second) {
		boolean valid=false;
		double dis = Math.sqrt(Math.pow(first[0]-second[0],2)+Math.pow(first[1]-second[1],2));
		System.out.println(dis);
		if ( (first[0]!=second[0] || first[1]!=second[1]) && dis < 2.0 ){
			int temporary = game[first[0]][first[1]];
			game[first[0]][first[1]] = game[second[0]][second[1]];
			game[second[0]][second[1]] = temporary;
			makeMove(first[0],first[1]);
			makeMove(second[0],second[1]);
			play();
			if (turn) turn = false;
			else turn = true;
			valid=true;
		}
		return valid;
	}
	
	public void play() {
		int lastPlayerScore = -1;
		while (lastPlayerScore != player1Score+player2Score) {
			for(int i = 0;i < size;i++) {
				for(int j = 0;j < size; j++) {
					makeMove(i,j);
				}
			}
			lastPlayerScore = player1Score+player2Score;
		}
		
	}
	
	public int[][] getState() {
		return game;
	}
	
	public boolean[][] getVisited() {
		return visited;
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
    
	
	private void makeMove(int row, int column) {
		diagonalMove(row,column);	
		diagonalIMove(row,column);
		horizontalMove(row,column);	
		verticalMove(row,column);
	}
	
	private void diagonalMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column-up-1) && game[row-up-1][column-up-1] == game[row][column]) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column+dw+1) && game[row+dw+1][column+dw+1] == game[row][column]) 
			dw++;
		if (up+dw >= 2) {
			int dis = up + dw;
			int inR = row-up;int inC = column-up;
			for(int i = 0; i <= dis;i++) { 
				game[inR+i][inC+i]=-1;	
				visited[inR+i][inC+i]=true;
				makeFall(inR+i,inC+i);
			}
			sumScore(up+dw+1) ;
		}
	}
	private void diagonalIMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column+up+1) && game[row-up-1][column+up+1] == game[row][column] && up < 3) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column-dw-1) && game[row+dw+1][column-dw-1] == game[row][column] && dw < 3) 
			dw++;
		if (up + dw >= 2) {
			int dis = up + dw;
			int inR = row-up;int inC = column+up;
			for(int i = 0; i <= dis;i++) { 
				game[inR+i][inC-i]=-1;	
				visited[inR+i][inC-i]=true;
				makeFall(inR+i,inC-i);
			}
			sumScore(up+dw+1) ;
		}
	}
	
	private void horizontalMove(int row, int column) {
		int lf = 0;
		while (verify(row,column-lf-1) && game[row][column-lf-1] == game[row][column] && lf < 3) 
			lf++;
		int rg = 0;
		while (verify(row,column+rg+1) && game[row][column+rg+1] == game[row][column] && rg < 3) 
			rg++;
		if (lf + rg >= 2) {
			int dis = rg + lf;
			int inR = row;int inC = column-lf;
			for(int i = 0; i <= dis;i++) {
				game[inR][inC+i]=-1;	
				visited[inR][inC+i]=true;
				makeFall(inR,inC+i);
			}
			sumScore(lf+rg+1) ;
		}
	}
	
	private void verticalMove(int row, int column) {
		int up = 0;
		while (verify(row-up-1,column) && game[row-up-1][column] == game[row][column] && up < 3) 
			up++;
		int dw = 0;
		while (verify(row+dw+1,column) && game[row+dw+1][column] == game[row][column] && dw < 3) 
			dw++;
		if (up + dw >= 2) {
			int dis = up + dw;
			int inR = row-up;int inC = column;
			for(int i = 0; i <= dis;i++) {
				game[inR+i][inC]=-1;
				visited[inR+i][inC]=true;
				makeFall(inR+i,inC);
			}
			sumScore(up+dw+1) ;
		}
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
			else {
				game[i][c] = bestAssigment(i,c);
			}
		} 
	}
	private int bestAssigment(int row, int column) {
		boolean[] exist = new boolean[7];
		for (int i = row - 1 ; i < row + 1; i ++) {
			for(int j = column - 1; i < column - 1;i++) {
				if (verify(i,j) && game[i][j] != -1) exist[game[i][j]] = true;
			}
		}
		ArrayList<Integer> works = new ArrayList<Integer>();
		for (int i = 0 ; i < numGems; i++) {
			if (!exist[i])works.add(i);
		}
		int r;
		if (works.size() > 0)r = works.size();
		else r = numGems;
		Random random = new Random();
		int p = random.nextInt(r);
		return p;
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
	
}