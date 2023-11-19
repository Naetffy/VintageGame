package test;

import domain.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VintageTest{
	
	@Test
	public void shoudModifyTheBoardAndMakesPerfectAssigments() {
		int[][] state = new int[][] {{-1,1,1,0},{0,2,0,0},{0,3,2,4},{5,6,6,5}};
		Vintage test =  new Vintage(4);
		test.setGameState(state);
		int n=0;
		while (n < 1000) {
			state[0][0] = test.bestAssigment(0,0);
			test.setGameState(state);
			state = test.getState();
			assertTrue(state[0][0]==3||state[0][0]==4||state[0][0]==5||state[0][0]==6);	
			n++;
		}
		
	}
	
	@Test
	public void shoudModifyTheBoardAndMakeARandomAssigmentBeacoseAllColorsAreUsed() {
		int[][] state = new int[][] {{2,1,1,0,0},{0,2,1,0,1},{3,3,-1,4,2},{5,4,5,6,3},{4,2,5,4,6}};
		Vintage test =  new Vintage(5);
		test.setGameState(state);
		int n=0;
		while (n < 1000) {
			state[2][2] = test.bestAssigment(2,2);
			test.setGameState(state);
			state = test.getState();
			assertTrue(state[2][2]>=0 && state[2][2]<7);	
			n++;
		}
		
	}
	
	@Test
	public void shoudModifyTheBoardAndUseTheZeroAsBestAssigment() {
		int[][] state = new int[][] {{2,1,1,0,0},{0,2,1,6,1},{3,3,-1,4,2},{5,4,5,6,3},{4,2,5,4,6}};
		Vintage test =  new Vintage(5);
		test.setGameState(state);
		int n=0;
		while (n < 1000) {
			state[2][2] = test.bestAssigment(2,2);
			test.setGameState(state);
			assertEquals(state[2][2],0);	
			n++;
		}
		
	}

}