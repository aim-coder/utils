package algorithms;

import java.util.ArrayDeque;
import java.util.Queue;

public class TreasureIsland1{
	
	public int minSteps(char[][] grid) {
		Point destination = checkPath(grid);
		Queue<Point> points = new ArrayDeque<>();
		points.add(new Point(0, 1));
		int i = 0;
		int j = 1;
		boolean isFound = false;
		while (!isFound) {
			System.out.println("Values [i][j] " + i + "," + j + " : " + grid[i][j]);

			if (grid[i][j] == 'X') {
				isFound = true;
			} else if (grid[i + 1][j] != 'D') {
				i++;
				points.add(new Point(i, j));
			} else if (destination.y <= j && grid[i][j - 1] != 'D') {
				j--;
				points.add(new Point(i, j));
			} else {
				j++;
				points.add(new Point(i, j));
			}
		}
		return points.size();
	}

	private Point checkPath(char[][] grid) {
		Point destination = null;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == 'X') {
					destination = new Point(i, j);
					break;
				}
			}
			if (destination != null) {
				break;
			}
		}
		return destination;
	}

	private class Point {
		int x, y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void main(String[] args) {
		char[][] grid = { { 'O', 'O', 'O', 'O' }, { 'D', 'O', 'D', 'O' }, { 'O', 'O', 'O', 'O' },
				{ 'X', 'D', 'D', 'O' } };

		System.out.println(new TreasureIsland1().minSteps(grid));
	}

}
