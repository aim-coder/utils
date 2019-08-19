package algorithms;

public class LinkedListAlgo {
	Node head;
	class Node{
		Node next;
		int data;
		Node(int data){
			this.data = data;
			next = null;
		}
	}
	
	void sortedInsert(int data) {
		Node newNode = new Node(data);
		if(head==null || head.data >= newNode.data) {
			newNode.next = head;
			head = newNode;
		}else {
			
			Node current = head;
			while(current.next != null && current.next.data < newNode.data) {
				current = current.next;
			}
			
			newNode.next = current.next;
			current.next = newNode;

		}
	}
	
	 void deleteNode(int data) {
		 Node tmp = head;
		 while(tmp.next !=null) {
			 if(tmp.next.data == data) {
				 tmp.next = tmp.next.next;
			 }
			 tmp = tmp.next;
		 }
	 }
	
	void printList() {
		System.out.println("\n\n");
		Node tmp = head;
		while(tmp!=null) {
			System.out.println(tmp.data);
			tmp = tmp.next;
		}
	}
	
	
	void createList(int data) {
		Node newNode = new Node(data);
		if(head ==null) {
			head = newNode;
			return;
		}
		
		Node current = head;
		
		while(current.next!=null) {
			current = current.next;
		}
		
		current.next = newNode;
		
		
	}
	
	public static void main(String[] args) {
		int[] input = {5,10,2,9, 18,25,46,31,41};
		LinkedListAlgo algo = new LinkedListAlgo();
		for(int i=0;i<input.length;i++) {
			//algo.sortedInsert(input[i]);
			algo.createList(input[i]);
		}
		
		algo.printList();
		
		algo.deleteNode(9) ;
		algo.printList();

	}
}
