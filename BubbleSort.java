package algorithms.sort;

import java.util.Arrays;

/*
 Bubble sort works by swapping adjacent elements if they're not in the desired order. 
 This process repeats from the beginning of the array until all elements are in order.
 */
public class BubbleSort {

	//Time complexity - O(n^2)
	public static void main(String[] args) {
		int[] array = {4,2,1,5,6,7,15,8,11,6,11};
		 bubbleSort(array);
	}

	public static void bubbleSort(int[] array) {
	    boolean sorted = false;
	    int temp;
	    while(!sorted) {
	        sorted = true;
	        for (int i = 0; i < array.length - 1; i++) {
	            if (array[i] > array[i+1]) {
	                temp = array[i];
	                array[i] = array[i+1];
	                array[i+1] = temp;
	                sorted = false;
	            }
	        }
	    }
	    System.out.println(Arrays.toString(array));
	}
	
}
