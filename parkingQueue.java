import java.util.LinkedList;

public class parkingQueue<T>{
    
    private LinkedList<T> list;
    private int capacity;

    public parkingQueue(int capacity){
        list = new LinkedList<>();
        this.capacity = capacity;
    }

    public LinkedList<T> getQueue(){return list; }
    public int getCapacity(){return capacity; }
    public void setCapacity(int capacity){this.capacity = capacity; }

    public boolean enqueue(T vehicle){
        if(list.size() >= capacity){return false; }

        list.addLast(vehicle);
        return true;
    }
    public T dequeue(){
        if(isEmpty()) return null;
        return list.removeFirst();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }
    public T peek(){
        if(isEmpty()) return null;
        return list.getFirst();
    }
    public boolean isFull(){
        return list.size() >= capacity;
    }
}