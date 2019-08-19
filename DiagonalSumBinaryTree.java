package problems;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


	class Node{
	    int data;
	    Node left,right;
	    Node(int d){
	        data=d;
	        left=right=null;
	    }
	}
	class DiagonalSumBinaryTree{
	    public static void main(String[] args){
	        Scanner sc=new Scanner(System.in);
	        int t=sc.nextInt();
	        while(t-->0){
	            int n=sc.nextInt();
	            Node root=null;
	            while(n-->0){
	                int a1=sc.nextInt();
	                int a2=sc.nextInt();
	                char lr=sc.next().charAt(0);
	                if(root==null){
	                    root=new Node(a1);
	                    switch(lr){
	                        case 'L':root.left=new Node(a2);
	                        break;
	                        case 'R':root.right=new Node(a2);
	                        break;
	                    }
	                }
	                else{
	                    insert(root,a1,a2,lr);
	                }
	            }
	            GfG g=new GfG();
	            g.diagonalsum(root);
	            System.out.println();
	        }
	    }
	    public static void insert(Node root,int a1,int a2,char lr){
	        if(root==null)
	            return;
	        if(root.data==a1){
	            switch(lr){
	                case 'L':root.left=new Node(a2);
	                break;
	                case 'R':root.right=new Node(a2);
	                break;
	            }
	        }
	        else{
	            insert(root.left,a1,a2,lr);
	            insert(root.right,a1,a2,lr);
	        }
	    }
	}

	class GfG
	{
	    static Queue<Node> q = new LinkedList<>();
	    static int count1 = 0;
	    static int sum = 0;
	    public void diagonalsum(Node root)
	    {
	      //add code here.
	        if(root == null && q.isEmpty()){
	            System.out.print(sum);
	            sum = 0;//sum is initialized as 0 at the end because it will store the value
	            return; //from the previous test cases.
	        }
	        else if(root == null && count1 == 0){
	            System.out.print(sum+" ");
	            root = q.poll();
	            count1 = q.size();
	            sum = 0;
	        }
	        else if(root == null){
	            count1--;
	            root = q.poll();
	        }
	        if(root.left != null){
	            q.add(root.left);
	        }
	        sum = sum + root.data;
	        diagonalsum(root.right);
	    }
	}
