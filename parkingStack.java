import java.util.LinkedList;

public class parkingStack<V>{

    private LinkedList<V> list;
    private int capacity;

    public parkingStack(int capacity, int liftNum){
        list = new LinkedList<>();
        this.capacity = capacity;
    }

    public LinkedList<V> getStack(){return list; }

    public boolean push(V vehicle){
        if(list.size() >= capacity){return false; }

        list.addFirst(vehicle);
        return true;
    }
    public V pop(){
        if(isEmpty()) return null;
        return list.removeFirst();
    }
    public V peek(){
        if(isEmpty()) return null;
        return list.getFirst();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }
    public boolean isFull(){
        return list.size() >= capacity;
    }
}