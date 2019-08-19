package problems;

import java.util.Scanner;

public class RemoveKNumbers {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Scanner sc = new Scanner(System.in);
		//String str = sc.next();
		//int length = sc.nextInt();
		String str = "456";
		int length = 2;
		new RemoveKNumbers().removeKdigits(str, length);
	}

	public String removeKdigits(String num, int k) {
		
		int[] count = new int[10];
		for(int i=0;i<num.length();i++) {
			count[num.charAt(i)-'0']++;
		}
		int tmpNbr = Integer.parseInt(num.substring(k));
		String str = null;
		return str;
	}

}
