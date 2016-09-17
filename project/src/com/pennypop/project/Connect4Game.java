package com.pennypop.project;

/**
 * The Connect 4 Game implementation
 * 
 * @author Minquan Wang
 */
public class Connect4Game {
	private int[][] board;
	private int player;
	private final int ROW = 6;
	private final int COLUMN = 7;
	private boolean gameOver;

	
	public Connect4Game() {
		// initialize the game
		// 0 for void, 1 for red player, 2 for yellow player
		board = new int[ROW][COLUMN];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				board[i][j] = 0;
			}
		}
		player = 1;
		gameOver = false;
	}
	
	public int getPlayer() {
		return player;
	}
	
	public int[][] getBoard() {
		return board;
	}
	
	public boolean isOver() {
		return gameOver;
	}
	
	/**Put the disc into the specific col, return false if the col is full*/
	public boolean put(int col) {
		if (canPut(col)) {
			for (int i = 0; i < ROW; i++) {
				if (board[i][col] == 0) {
					board[i][col] = player;
					//check to see weather the game is over based on last move;
					gameOver = checkGameOver(i, col);
					break;
				}
			}
			//1 for red, 2 for yellow, consistent with the disk;
			player = 2 - (player + 1) % 2;
			return true;
		} else {
			return false;
		}
	}
	
	/**check if the column is full*/
	public boolean canPut(int col) {
		return board[ROW - 1][col] == 0;
	}
	
	/**Check whether the board is full without a Game Over */
	public boolean isTie() {
		if (gameOver) {
			return false;
		}
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if (board[i][j] == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**Check whether the game is over based on the last move*/
	public boolean checkGameOver(int i, int j) {
		
		//get the current player number
		int cur = board[i][j];
		
		//check horizontal direction
		int hor = 1;
		for (int k = i - 1; k >= 0; k--) {
			if (board[k][j] != cur) {
				break;
			} else {
				hor++;
			}
		}
		for (int k = i + 1; k < ROW; k++) {
			if (board[k][j] != cur) {
				break;
			} else {
				hor++;
			}
		}
		if (hor >= 4) {
			return true;
		}
		
		//check vertical direction
		int ver = 1;
		for (int k = j - 1; k >= 0; k--) {
			if (board[i][k] != cur) {
				break;
			} else {
				ver++;
			}
		}
		for (int k = j + 1; k < COLUMN; k++) {
			if (board[i][k] != cur) {
				break;
			} else {
				ver++;
			}
		}
		if (ver >= 4) {
			return true;
		}
		
		//check top left diagonal direction
		int tl = 1;
		int k = 1;
		while (i - k >= 0 && j + k < COLUMN) {
			if (board[i - k][j + k] != cur) {
				break;
			} else {
				tl++;
				k++;
			}
		}
		k = 1;
		while (j - k >= 0 && i + k < ROW) {
			if (board[i + k][j - k] != cur) {
				break;
			} else {
				tl++;
				k++;
			}
		}
		if (tl >= 4) {
			return true;
		}
		
		//check top right diagonal direction
		int tr = 1;
		k = 1;
		while (i + k < ROW && j + k < COLUMN) {
			if (board[i + k][j + k] != cur) {
				break;
			} else {
				tr++;
				k++;
			}
		}
		k = 1;
		while (j - k >= 0 && i - k >= 0) {
			if (board[i - k][j - k] != cur) {
				break;
			} else {
				tr++;
				k++;
			}
		}
		if (tr >= 4) {
			return true;
		}
		
		return false;
	}
	
}
