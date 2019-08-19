package algorithms;

import java.util.Queue;

public class TreasureIsland {
	
	private int traverseIsland(char[][] island) {
		Point destination = getDestiation(island);
		Queue<Point> points = new java.util.LinkedList<Point>();
		boolean found = false;
		int i = 0;
		int j = 1;
		points.add(new Point(0,0));
		while(!found) {
			if(island[i][j] == 'X') {
				found = true;
			}else if(island[i+1][j]!='D') {
				i++;
				points.add(new Point(i,j));
			}else if(destination.y <= j && island[i][j-1]!='D') {
				points.add(new Point(i,j));
				j--;
			}else {
				j++;
				points.add(new Point(i,j));
			}
		}
		return points.size();
	}
	
	private Point getDestiation(char[][] island) {
		Point point = null;
		
		for(int i=0;i<island.length;i++) {
			for(int j=0;j<island.length;j++) {
				if(island[i][j] == 'X') {
					return new Point(i,j);
				}
			}
		}
		return point;
	}
	
	class Point{
		int x;
		int y;
		
		Point(int x, int y){
			this.x=x;
			this.y =y;
		}
	}

	public static void main(String[] args) {
		
		char[][] island = new char[][]{
			{'O', 'O', 'O', 'O'},
			{'D', 'O', 'D', 'O'},
			{'O', 'O', 'O', 'O'},
			{'X', 'D', 'D', 'O'}
	};
	
		int shortestDest = new TreasureIsland().traverseIsland(island);
		System.out.println(shortestDest);
	}
}