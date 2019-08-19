package algorithms.sort;

import java.util.Arrays;

public class SelectionSort {

	//Finding the minimum is O(n) for the length of array because we have to check all of the elements. 
	//We have to find the minimum for each element of the array, making the whole process bounded by O(n^2).
	public static void main(String[] args) {

		int[] array = {4,2,1,5,6,7,15,8,11,6,11};
		selectionSort(array);
		 
	}
	
	public static void selectionSort(int[] array) {
	    for (int i = 0; i < array.length; i++) {
	        int min = array[i];
	        int minId = i;
	        for (int j = i+1; j < array.length; j++) {
	            if (array[j] < min) {
	                min = array[j];
	                minId = j;
	            }
	        }
	        // swapping
	        int temp = array[i];
	        array[i] = min;
	        array[minId] = temp;
	    }
	    System.out.println(Arrays.toString(array));

	}
}
