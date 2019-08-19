package algorithms;

public class SingleLinkedList<E> {  
    Node<E> start; // points to the head or first node  
 
    /**  
     * Node class    
     */  
    private class Node<E> {  
         E data;  
         Node<E> next;  
 
         public Node(E data, Node<E> next) {  
              this.data = data;  
              this.next = next;  
         }  
 
         public E getData() {  
              return data;  
         }  
 
         public Node<E> getNext() {  
              return next;  
         }  
 
         public void setNext(Node<E> next) {  
              this.next = next;  
         }  
    }  
 
    public void add(E d) { // add at the end of list  
         if (start == null) {  
              start = new Node<E>(d, null);  
         } else {  
              Node<E> tmp = start;  
              while (tmp.next != null) {  
                   tmp = tmp.next;  
              }  
              tmp.setNext(new Node<E>(d, null));  
         }  
    }  
 
    public void print() {  
         Node<E> current = start;  
         System.out.print(" values in link-list are :");  
         while (current != null) {  
              System.out.print(current.getData() + "--> ");  
              current = current.getNext();  
         }  
         System.out.println("null");  
    }  
 
    public static void main(String[] args) {  
         SingleLinkedList<Integer> sll = new SingleLinkedList<>();  
         sll.add(5);  
         sll.add(10);  
         sll.add(42);  
         sll.add(23);  
         sll.add(19);  
         sll.add(11);  
         sll.add(12);  
         sll.add(51);  
         sll.add(31);  


         sll.print();  
    }  
} 
