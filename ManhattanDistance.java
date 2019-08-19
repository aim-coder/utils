package problems;

public class ManhattanDistance {

	 // Return the sum of distance between all 
    // the pair of points. 
    static int distancesum(int x[], int y[], int n) 
    { 
        int sum = 0; 
  
        // for each point, finding distance to 
        // rest of the point 
        for (int i = 0; i < n; i++) 
            for (int j = i + 1; j < n; j++) 
                sum += (Math.abs(x[i] - x[j]) +  
                            Math.abs(y[i] - y[j])); 
        return sum; 
    } 
  
    // Driven Program 
    public static void main(String[] args) 
    { 
        int x[] = { -1, 1, 3, 2 }; 
        int y[] = { 5, 6, 5, 3 }; 
        int n = x.length; 
          
        System.out.println(distancesum(x, y, n)); 
    } 
} 
 