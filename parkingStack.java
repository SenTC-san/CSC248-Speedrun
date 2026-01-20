import java.util.LinkedList;

public class parkingStack<Vehicle>{

    private LinkedList<Vehicle> list;
    private int capacity;
    private int liftNum;

    public parkingStack(){
        list = new LinkedList<>();
        capacity = ParkingSystem.CAPACITY;
        liftNum = ParkingSystem.LIFTNUM;
    }
    public parkingStack(int capacity, int liftNum){
        list = new LinkedList<>();
        this.capacity = capacity;
        this.liftNum = liftNum;
    }

    public LinkedList<Vehicle> getStack(){return list; }

    public boolean push(Vehicle vehicle){
        if(list.size() >= capacity){return false; }

        list.addFirst(vehicle);
        return true;
    }
    public Vehicle pop(){
        if(isEmpty()) return null;
        return list.removeFirst();
    }
    public Vehicle peek(){
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