import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class ParkingSystem {
    public static void main(String [] args){

        //---------------------------------------------------------
        //quick program data changes : lot name, capacity, lift num, payment rate
        String PARKINGNAME = "King Centre parking";
        int CAPACITY = 6;
        int LIFTNUM = 4;
        double RATEPERHOUR = 4.0;
        //----------------------------------------------------------

        Object[] data = {PARKINGNAME, CAPACITY, LIFTNUM, RATEPERHOUR};
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

        int selection;
        boolean valid;
        do{ 
            System.out.println("\tWelcome to " + PARKINGNAME);
            //System.out.println("Parking Lot:");
            v.visualiser(queue, lifts, dp);
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
                        LotDetails(historyList);
                        break;
                    case 0:
                        System.out.println("Thank you & Drive Safely!");
                        valid = false;
                        break;
                    default:    
                       System.out.println("Invalid Option!");
                       break;
            }
            dp.waitForKey(sc);
            dp.clearScreen();
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
            }
            while(!tempQ.isEmpty()){
                queue.enqueue(tempQ.dequeue());
            }
        }
        if(!found){
            System.out.println("Vehicle not found.");
        }
    }

    //Operation 4: Vehicle History in history text file
    public static void VehicleHistory(LinkedList <Receipt> historyList ){
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Vehicle v = new Vehicle();
        historyList = v.toFileRead();
        for (Receipt h: historyList){
            System.out.printf("| RECEIPT ID | Vehicle ID | Plate Number |     Entry Time       |      Exit Time       | Total Fee |%n");
            System.out.printf("| %-10d |%-11d | %-12s | %-20s | %-20s | %7.2f   |%n%n" , (h.getReceiptID()-1), (h.getVehicle().getVehicleID()-1), h.getVehicle().getPlateNumber(), f1.format(h.getVehicle().getEntryTime()), f1.format(h.getVehicle().getExitTime()), h.getTotalPayment());
        } 
    }

    //Operation 5: View Lot Details
    public static void LotDetails(LinkedList <Receipt> historyList){
        Vehicle v = new Vehicle();
        historyList = v.toFileRead();
        int totalReceipt = 0;
        double totalRevenue = 0.0;
        double avgRevenue = 0.0;
        int zcParking = 0;
        int peakHour = 0;
        double Duration = 0;
        double avgDuration = 0;
        int[] hourCount = new int[24];

        for (Receipt h: historyList){
            if(h.getReceiptID() > 10000){totalReceipt++; }
            totalRevenue += h.getTotalPayment();
            if(!(h.getTotalPayment() > 0.0)){ zcParking++; }
            
            int hour = h.getVehicle().getEntryTime().getHour();
            hourCount[hour]++;
            Duration += h.getVehicle().parkingDuration();
        }
        //count average revenue
        if(totalReceipt > 0){ avgRevenue = totalRevenue/totalReceipt; }
        //count average duration
        if(Duration > 0){ avgDuration = Duration/totalReceipt; }

        //count the peak hour
        int maxVehicles = hourCount[0];
        for (int h = 1; h < 24; h++) {
            if (hourCount[h] > maxVehicles) {
                maxVehicles = hourCount[h];
                peakHour = h;
            }
        }
        int startHour = peakHour;
        int endHour = peakHour;

        System.out.printf("=====================================%n");
        System.out.printf("         PARKING LOT SUMMARY         %n");
        System.out.printf("=====================================%n");
        System.out.printf("Total Receipts       : %-15d%n", totalReceipt);
        System.out.printf("Total Revenue        : %-15.2f%n", totalRevenue);
        System.out.printf("Total Parking Time   : %-15.2f%n", Duration);
        System.out.printf("Average Revenue      : %-15.2f%n", avgRevenue);
        System.out.printf("Average Parking Time : %-15.2f%n", avgDuration);
        System.out.printf("Zero-Cost Parking Fee: %-15d%n", zcParking);
        System.out.printf("Peak Hour            : %02d:00 - %02d:59%n", startHour, endHour);
        System.out.printf("=====================================%n");
    }
}