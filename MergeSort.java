package algorithms.sort;

import java.util.Arrays;

//Divide and Conquer
public class MergeSort {

	/*
	 Time complexity
	T(n) = 2T(n/2) + O(n)
	2T(n/2) corresponds to the time required to sort the sub-arrays and O(n) time to merge the entire array.
	When solved, the time complexity will come to O(nLogn).
	Space Complexity
	The space complexity of the algorithm is O(n) as we’re creating temporary arrays in every recursive call.
	*/
	
	public static void main(String[] args) {
		int[] array = {4,2,1,5,6,7,15,8,11,6,11};
		mergeSort(array,11);
		
	}
	
	public static void mergeSort(int[] a, int n) {
	    if (n < 2) {
	        return;
	    }
	    int mid = n / 2;
	    int[] l = new int[mid];
	    int[] r = new int[n - mid];
	 
	    for (int i = 0; i < mid; i++) {
	        l[i] = a[i];
	    }
	    for (int i = mid; i < n; i++) {
	        r[i - mid] = a[i];
	    }
	    mergeSort(l, mid);
	    mergeSort(r, n - mid);
	 
	    merge(a, l, r, mid, n - mid);
	    System.out.println(Arrays.toString(a));
	}
	
	public static void merge(
			  int[] a, int[] l, int[] r, int left, int right) {
			  
			    int i = 0, j = 0, k = 0;
			    while (i < left && j < right) {
			        if (l[i] <= r[j]) {
			            a[k++] = l[i++];
			        }
			        else {
			            a[k++] = r[j++];
			        }
			    }
			    while (i < left) {
			        a[k++] = l[i++];
			    }
			    while (j < right) {
			        a[k++] = r[j++];
			    }
			}

}
