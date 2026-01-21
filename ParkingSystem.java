import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class ParkingSystem {
    public static void main(String [] args){

        //---------------------------------------------------------
        //quick program changes data : name, capacity, payment rate
        String PARKINGNAME = "King Centre parking";
        int CAPACITY = 3;
        int LIFTNUM = 3;
        double RATEPERHOUR = 4.0;
        //----------------------------------------------------------

        Object[] data = {PARKINGNAME, CAPACITY, CAPACITY, RATEPERHOUR};
        DataProcessor dp = new DataProcessor(data);

        Scanner sc = new Scanner(System.in);
        LinkedList<Receipt> historyList = new LinkedList<>();
        parkingQueue<Vehicle> queue = new parkingQueue(CAPACITY);
        parkingStack<Vehicle>[] lifts = new parkingStack[LIFTNUM];

        Vehicle v = new Vehicle();
        historyList = v.toFileRead(); //access history file data

        for (int i = 0; i < LIFTNUM; i++) {
            lifts[i] = new parkingStack<>(CAPACITY, i + 1);
        }

        startSystem(sc, historyList, queue, lifts, dp);
    }

    public static void startSystem(Scanner sc, LinkedList<Receipt> historyList, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
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
                        RegisterVehicle(sc, historyList, queue, lifts);
                        break;
                    case 2:
                        LoadVehicle(queue, lifts, dp);
                        break;
                    case 3:
                        UnloadVehicle(sc, historyList, queue, lifts, dp);
                        break;
                    case 4:
                        VehicleHistory(historyList);
                        break;
                    case 5:
                        LotDetails();
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

    //Operation 1: Register Vehicle
    public static void RegisterVehicle(Scanner sc, LinkedList<Receipt> historyList, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts){ 
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine().toUpperCase();

        int highestID = 1000;
        for(Receipt r :historyList){
            if(r.getVehicle().getVehicleID() > highestID){
                highestID = r.getVehicle().getVehicleID();
            }
        }
        Vehicle v = new Vehicle(highestID, plateNum, LocalDateTime.now() ,null);

        if(queue.enqueue(v)){
            System.out.println("Vehicle has entered queue.");
        } else {
            System.out.println("Queue is full!");
        }
    }

    //Operation 2: Park Vehicle
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

    //Operation 3: Unpark Vehicle
    public static void UnloadVehicle(Scanner sc, LinkedList<Receipt> historyList, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        int CAPACITY = (int) dp.dataAtIndex(1);
        double RATEPERHOUR = (double) dp.dataAtIndex(3);

        int highestID = 10000;
        for(Receipt r : historyList){
            if(r.getReceiptID() > highestID){
                highestID = r.getReceiptID();
            }
        }

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
                    Receipt r = new Receipt(highestID, v, v.calcTotal(RATEPERHOUR), true);
                    r.generateReceipt(dp);
                    found = true;

                    v.toFileWrite(r); //file io process

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
                    Receipt r = new Receipt(highestID, v, v.calcTotal(RATEPERHOUR), false);
                    r.generateReceipt(dp);
                    found = true;
                    
                    v.toFileWrite(r); //file io process
                    break;
                }else{ tempQ.enqueue(v); }
                System.out.println("Vehicle not found.");
            }
            while(!tempQ.isEmpty()){
                queue.enqueue(tempQ.dequeue());
            }
        }
    }

    //Operation 4: Vehicle History in history text file
    public static void VehicleHistory(LinkedList <Receipt> historyList ){
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Vehicle v = new Vehicle();
        historyList = v.toFileRead();
        for (Receipt h: historyList){
            System.out.printf("=========================================================================================%n");
            System.out.printf("|                               RECEIPT ID : %-5d                                      |%n", (h.getReceiptID()-1));
            System.out.printf("=========================================================================================%n");
            System.out.printf("| Vehicle ID | Plate Number |     Entry Time        |      Exit Time        | Total Fee |%n");
            System.out.printf("| %-10d | %-12s | %-21s | %-21s | %7.2f   |%n" , (h.getVehicle().getVehicleID()-1), h.getVehicle().getPlateNumber(), f1.format(h.getVehicle().getEntryTime()), f1.format(h.getVehicle().getExitTime()), h.getTotalPayment());
            System.out.printf("=========================================================================================%n");
        } 
    }

    public static void LotDetails(LinkedList <Receipt> historyList){
        Vehicle v = new Vehicle();
        historyList = v.toFileRead();
        for (Receipt h: historyList){
            
        }
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