import java.util.LinkedList;

public class parkingQueue<Vehicle>{
    
    private LinkedList<Vehicle> list;
    private int capacity;

    public parkingQueue(){
        list = new LinkedList<>();
        capacity = ParkingSystem.CAPACITY;
    }
    public parkingQueue(int capacity){
        list = new LinkedList<>();
        this.capacity = capacity;
    }

    public LinkedList<Vehicle> getQueue(){return list; }
    public int getCapacity(){return capacity; }
    public void setCapacity(int capacity){this.capacity = capacity; }

    public boolean enqueue(Vehicle vehicle){
        if(list.size() >= capacity){return false; }

        list.addLast(vehicle);
        return true;
    }
    public Vehicle dequeue(){
        if(isEmpty()) return null;
        return list.removeFirst();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }
    public Vehicle peek(){
        if(isEmpty()) return null;
        return list.getFirst();
    }
    public boolean isFull(){
        return list.size() >= capacity;
    }

    //getSize() :int
}
