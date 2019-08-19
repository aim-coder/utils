package algorithms.sort;

import java.util.Arrays;

/*
The idea behind Insertion Sort is dividing the array into the sorted and unsorted subarrays.

The sorted part is of length 1 at the beginning and is corresponding to the first (left-most) element in the array. We iterate through the array and during each iteration, we expand the sorted portion of the array by one element.
 */
public class InsertionSort {

	//Time complexity - O(n^2)
	public static void main(String[] args) {

		int[] array = {4,2,1,5,6,7,15,8,11,6,11};
		insertionSort(array);
		 
	}

	public static void insertionSort(int[] array) {
	    for (int i = 1; i < array.length; i++) {
	        int current = array[i];
	        int j = i - 1;
	        while(j >= 0 && current < array[j]) {
	            array[j+1] = array[j];
	            j--;
	        }
	        // at this point we've exited, so j is either -1
	        // or it's at the first element where current >= a[j]
	        array[j+1] = current;
	    }
	    System.out.println(Arrays.toString(array));
	}
}
