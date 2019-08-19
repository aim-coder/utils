package algorithms;

public class Movie {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int[] all = {110, 90, 85,  60, 120, 150, 125,109};
		int d = 250;
		int possible = 0;
		int first =0;
		int second = 0;
		int tmp = 0;
		for(int i=0;i<all.length;i++) {
			for(int j=i;j<all.length;j++) {
				if(all[i] + all[j] < (d-30) &&  ((all[i] + all[j])  >= possible)) {
					possible = all[i] + all[j];
					
					//if((all[i] > first && all[i] >second) || (all[j] > first && all[j] >second)) {
						first = all[i];
						second = all[j];
					//}
					
				}
			}
		}
		
		System.out.println("first:: " + first + "second:: " + second);

	}

}
