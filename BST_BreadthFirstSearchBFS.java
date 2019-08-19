package algorithms;

import java.util.Queue;

public class BST_BreadthFirstSearchBFS {
	public void levelOrderQueue(Node root) {
		Queue<Node> q = new java.util.LinkedList<Node>();
		if (root == null)
			return;
		q.add(root);
		while (!q.isEmpty()) {
			Node n = (Node) q.remove();
			System.out.print(" " + n.data);
			if (n.left != null)
				q.add(n.left);
			if (n.right != null)
				q.add(n.right);
		}
	}

	public static void main(String[] args) throws java.lang.Exception {
		Node root = new Node(5);
		root.left = new Node(10);
		root.right = new Node(15);
		root.left.left = new Node(20);
		root.left.right = new Node(25);
		root.right.left = new Node(30);
		root.right.right = new Node(35);

		BST_BreadthFirstSearchBFS i = new BST_BreadthFirstSearchBFS();
		System.out.println("Breadth First Search : ");
		i.levelOrderQueue(root);
	}
}

class Node {
	int data;
	Node left;
	Node right;

	public Node(int data) {
		this.data = data;
		this.left = null;
		this.right = null;
	}
}