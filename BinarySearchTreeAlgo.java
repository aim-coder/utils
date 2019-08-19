package algorithms;

class BinarySearchTreeAlgo{
	Node root;
	class Node{
		Node left, right = null;
		int key = 0;
		Node(int item){
			key = item;
			left = right = null;
		}
	}
	void createNode(int value) {
		root = insertNode(root, value);
	}
	
	Node insertNode(Node root, int value) {
		if(root == null) {
			root = new Node(value);
			return root;
		}
		if(value < root.key) {
			root.left = insertNode(root.left, value);
		}
		if(value > root.key) {
			root.right = insertNode(root.right, value);
		}
		return root;
	}
	void printNode() {
		orderAndPrintNode(root);
	}
	void orderAndPrintNode(Node root) {
		if(root!=null) {
			orderAndPrintNode(root.left);
			System.out.println(root.key);
			orderAndPrintNode(root.right);
		}
		}
	public static void main(String[] args) {
		BinarySearchTreeAlgo algo = new BinarySearchTreeAlgo();
		algo.createNode(50);
		algo.createNode(80);
		algo.createNode(40);
		algo.createNode(10);
		algo.createNode(370);
		algo.createNode(11);
		algo.createNode(41);
		
		algo.printNode();


		
	}
}