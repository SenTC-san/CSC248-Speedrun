import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class ParkingSystem {
    public static void main(String [] args){
        //quick program changes data : name, capacity, payment rate
        String PARKINGNAME = "King Centre parking";
        int CAPACITY = 3;
        int LIFTNUM = 3;
        double RATEPERHOUR = 4.0;
        Object[] data = {PARKINGNAME, CAPACITY, CAPACITY, RATEPERHOUR};
        DataProcessor dp = new DataProcessor(data);

        Scanner sc = new Scanner(System.in);
        parkingQueue<Vehicle> queue = new parkingQueue(CAPACITY);
        parkingStack<Vehicle>[] lifts;
        lifts = new parkingStack[LIFTNUM];

        for (int i = 0; i < LIFTNUM; i++) {
            lifts[i] = new parkingStack<>(CAPACITY, i + 1);
        }

        startSystem(sc, queue, lifts, dp);
    }

    public static void startSystem(Scanner sc, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int selection;
        boolean valid;
        String PARKINGNAME = (String) dp.dataAtIndex(0);
        do{ 
            System.out.println("\tWelcome to " + PARKINGNAME);
            //System.out.println("Parking Lot:");
            visualiser(queue, lifts, dp);
            String menu = """
                    \n========== MAIN MENU ==========
                    1. Register Vehicle
                    2. Load Vehicle
                    3. Unload Vehicle
                    4. View Vehicle History
                    5. Parking Lot Details
                    0. Exit
                    ===============================
                    """;
            System.out.print(menu);
            System.out.print("Enter option: ");
            String input = sc.nextLine();

            if(input == null){ return; }
                else{ valid = true; }
                try{
                    selection = Integer.parseInt(input);
                }
                catch (NumberFormatException e){
                    System.out.println("Invalid input! Please enter a number.");
                    continue;
                }

            switch(selection){
                    case 1:
                        RegisterVehicle(sc, queue, lifts);
                        break;
                    case 2:
                        LoadVehicle(queue, lifts, dp);
                        break;
                    case 3:
                        UnloadVehicle(sc, queue, lifts, dp);
                        break;
                    case 4:
                        VehicleHistory();
                        break;
                    case 0:
                        System.out.println("Thank you & Drive Safely!");
                        valid = false;
                        break;
                    default:    
                       System.out.println("Invalid Option!");
                       break;
            }
            waitForKey(sc);
            clearScreen();
        }while (valid);
    }

    public static void RegisterVehicle(Scanner sc, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts){
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine().toUpperCase();
        Vehicle v = new Vehicle(plateNum, LocalDateTime.now() ,null);
        if(queue.enqueue(v)){
            System.out.println("Vehicle has entered queue.");
        } else {
            System.out.println("Queue is full!");
        }
    }
    public static void LoadVehicle(parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        for(int i=0; i<LIFTNUM; i++){
            if(!lifts[i].isFull()){
                Vehicle v = (Vehicle) queue.dequeue();
                lifts[i].push(v);
                System.out.println(v.getPlateNumber() + " moved to Lift " + (i+1));
                return;
            }
        }
        System.out.println("All lifts are full.");
    }
    public static void UnloadVehicle(Scanner sc, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        int CAPACITY = (int) dp.dataAtIndex(1);
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine().trim();
        boolean found = false;

        for(int i=0; i<LIFTNUM; i++){
            parkingStack<Vehicle> currentLift = lifts[i];
            parkingStack<Vehicle> tempS = new parkingStack<>(CAPACITY, i);

            while(!currentLift.isEmpty()){
                Vehicle v = (Vehicle) currentLift.pop();

                if(v.getPlateNumber().trim().equalsIgnoreCase(plateNum)){
                    v.setExitTime(LocalDateTime.now());
                    System.out.println("Vehicle [" + plateNum + "]found in Lift" + (i+1));
                    Receipt t = new Receipt(v);
                    t.generateReceipt(true, dp);
                    found = true;
                    break;
                }else{ tempS.push(v); }
            }
            while(!tempS.isEmpty()){
                currentLift.push(tempS.pop());
            }
            if(found){break; }
        }
        if(!found){
            parkingQueue<Vehicle> tempQ = new parkingQueue<>(CAPACITY);
            while(!queue.getQueue().isEmpty()){
                Vehicle v = (Vehicle) queue.dequeue();
                
                if(v.getPlateNumber().trim().equalsIgnoreCase(plateNum)){
                    v.setExitTime(LocalDateTime.now());
                    System.out.println("Vehicle [" + plateNum + "]found in Queue" );
                    Receipt r = new Receipt(v);
                    r.generateReceipt(false, dp);
                    found = true;
                    break;
                }else{ tempQ.enqueue(v); }
                System.out.println("Vehicle not found.");
            }
            while(!tempQ.isEmpty()){
                queue.enqueue(tempQ.dequeue());
            }
        }
    }
    public static void VehicleHistory(){
        
        return;
    }

    public static void visualiser(parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        int CAPACITY = (int) dp.dataAtIndex(1);
        //Header
        System.out.print("   Queue      |     ");
        for (int l = 1; l <= LIFTNUM; l++){
            System.out.printf("Lift %-7d ", l);
        }
        System.out.println();

        //Rows
        for(int r=0; r<CAPACITY; r++){

            //Queue column
            if(r < queue.getQueue().size()){
                Vehicle v = (Vehicle) queue.getQueue().get(r);
                printSlot(v.getPlateNumber(), 10);
            } else
                printSlot("",10);
            System.out.print("  |  ");
        
            //Lifts
            for(int l=0; l<LIFTNUM; l++){
                LinkedList<Vehicle> slist = lifts[l].getStack();
                int indexFromTop = slist.size() - 1 - r; 
                if(indexFromTop >= 0 && indexFromTop < slist.size()){
                    Vehicle v = slist.get(indexFromTop);
                    printSlot(v.getPlateNumber(), 10);
                } else
                    printSlot("",10);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void printSlot(String text, int slotWidth){
        if(text.length() > slotWidth){
            text = text.substring(0, slotWidth);
        }

        int totalSpace = slotWidth - text.length();
        int leftSpace = totalSpace / 2;
        int rightSpace = totalSpace - leftSpace;

        System.out.print("[");
        for (int i = 0; i < leftSpace; i++) {
            System.out.print(" ");
        }

        System.out.print(text);

        for (int i = 0; i < rightSpace; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }

    public static void waitForKey(Scanner sc){
        System.out.print("\nPress ENTER to continue...");
        sc.nextLine();
    }
    // Sadia. (2011, February 2). How to clear the console using Java? Stack Overflow. https://stackoverflow.com/questions/2979383/how-to-clear-the-console-using-java/
    public static void clearScreen() { //apparently the previous clear screen does not work with windows cmd prompt, this one works with any
    try {
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        else {
            System.out.print("\033\143");
        }
        } catch (IOException | InterruptedException ex) {}
    }
}