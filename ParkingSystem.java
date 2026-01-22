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
            System.out.println("=== Welcome to " + PARKINGNAME + " ===\n");
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

    //Operation 1: Register Vehicle - Adds a new vehicle to the queue waiting to be parked
    public static void RegisterVehicle(Scanner sc, LinkedList<Receipt> historyList, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts){ 
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine().toUpperCase().trim();
        
        // Find the highest vehicle ID in history to generate the next unique ID
        int highestID = 1000;
        for(Receipt r :historyList){
            if(r.getVehicle().getVehicleID() > highestID){
                highestID = r.getVehicle().getVehicleID();
            }
        }
        highestID++;// Increment to create new ID
        // Create new vehicle with incremented ID, plate number, and current entry time
        Vehicle v = new Vehicle(highestID, plateNum, LocalDateTime.now() ,null);

        if(queue.enqueue(v)){
            System.out.println("Vehicle has entered queue.");
        } else {
            System.out.println("Queue is full!");
        }
    }

    //Operation 2: Park Vehicle - Moves a vehicle from queue to an available lift
    public static void LoadVehicle(parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        
        // Check if queue has any vehicles
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        
        // Find the first available (not full) lift and move vehicle to it
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

    //Operation 3: Exit Vehicle - Removes a vehicle from parking and generates receipt
    public static void UnloadVehicle(Scanner sc, LinkedList<Receipt> historyList, parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        int CAPACITY = (int) dp.dataAtIndex(1);
        double RATEPERHOUR = (double) dp.dataAtIndex(3);
        
        // Find highest receipt ID to generate unique receipt ID for this unpark operation
        int highestID = 10000;
        for(Receipt r : historyList){
            if(r.getReceiptID() > highestID){
                highestID = r.getReceiptID();
            }
        }
    
        highestID++; // Increment for new receipt
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine().toUpperCase().trim();
        boolean found = false; // Track if vehicle was found in parking
        
        // Search for vehicle in all lifts
        for(int i=0; i<LIFTNUM; i++){
            parkingStack<Vehicle> currentLift = lifts[i];
            // Temporary stack to preserve lift order while searching
            parkingStack<Vehicle> tempS = new parkingStack<>(CAPACITY, i);
            
            // Pop vehicles from lift until we find the target or exhaust the lift
            while(!currentLift.isEmpty()){
                Vehicle v = (Vehicle) currentLift.pop();

                if(v.getPlateNumber().trim().equalsIgnoreCase(plateNum)){
                    // Found the vehicle - set exit time and create receipt
                    v.setExitTime(LocalDateTime.now());
                    System.out.println("Vehicle [" + plateNum + "]found in Lift" + (i+1));
                    Receipt r = new Receipt(highestID, v, v.calcTotal(RATEPERHOUR), true);
                    r.generateReceipt(dp); // Display receipt to user
                    found = true;

                    v.toFileWrite(r); // Write transaction to history file

                    break;
                }else{ tempS.push(v); } // Save non-matching vehicles to restore later
            }
            // Restore all vehicles back to the lift in original order
            while(!tempS.isEmpty()){
                currentLift.push(tempS.pop());
            }
            if(found){break; }
        }
        // If not found in lifts, search in queue
        if(!found){
            parkingQueue<Vehicle> tempQ = new parkingQueue<>(CAPACITY);
            while(!queue.getQueue().isEmpty()){
                Vehicle v = (Vehicle) queue.dequeue();
                
                if(v.getPlateNumber().trim().equalsIgnoreCase(plateNum)){
                    // Found in queue - set exit time and create receipt (no payment since vehicle never parked)
                    v.setExitTime(LocalDateTime.now());
                    System.out.println("Vehicle [" + plateNum + "]found in Queue" );
                    Receipt r = new Receipt(highestID, v, v.calcTotal(RATEPERHOUR), false);
                    r.generateReceipt(dp);
                    found = true;
                    
                    v.toFileWrite(r); // Write transaction to history file
                    break;
                }else{ tempQ.enqueue(v); } // Save non-matching vehicles
            }
            // Restore all vehicles back to queue in original order
            while(!tempQ.isEmpty()){
                queue.enqueue(tempQ.dequeue());
            }
        }
        // Display error message if vehicle not found anywhere
        if(!found){
            System.out.println("Vehicle not found.");
        }
    }

    //Operation 4: Display Vehicle History - Reads and displays all parking transactions from file
    public static void VehicleHistory(LinkedList <Receipt> historyList ){
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // Format for displaying date/time in DD/MM/YYYY HH:MM:SS format
        Vehicle v = new Vehicle();
        historyList = v.toFileRead(); // Load all history records from file
        // Display each transaction in formatted table(LinkedList <Receipt> historyList ){
        for (Receipt h: historyList){
            System.out.printf("| RECEIPT ID | Vehicle ID | Plate Number |     Entry Time       |      Exit Time       | Total Fee |%n");
            System.out.printf("| %-10d |%-11d | %-12s | %-20s | %-20s | %7.2f   |%n%n" , (h.getReceiptID()-1), (h.getVehicle().getVehicleID()-1), h.getVehicle().getPlateNumber(), f1.format(h.getVehicle().getEntryTime()), f1.format(h.getVehicle().getExitTime()), h.getTotalPayment());
        } 
    }

    //Operation 5: View Lot Details - Displays parking statistics and analysis
    public static void LotDetails(LinkedList <Receipt> historyList){
        Vehicle v = new Vehicle();
        historyList = v.toFileRead();// Load all history records
        
        // Initialize variables to track parking statistics
        int totalReceipt = 0; // Count of completed transactions
        double totalRevenue = 0.0; // Total money earned
        double avgRevenue = 0.0; // Average revenue per transaction
        int zcParking = 0; // Count of zero-cost parkings (time < 1 minute)
        int peakHour = 0; // Hour with most vehicle entries
        double Duration = 0; // Total parking duration in minutes
        double avgDuration = 0; // Average parking duration
        int[] hourCount = new int[24]; // Count vehicles entering each hour
        
        // Calculate statistics from all transactions
        for (Receipt h: historyList){
            if(h.getReceiptID() > 10000){totalReceipt++; } // Only count paid transactions
            totalRevenue += h.getTotalPayment(); // Sum all payments
            if(!(h.getTotalPayment() > 0.0)){ zcParking++; } // Count free parkings
            
            // Track which hour this vehicle entered parking
            int hour = h.getVehicle().getEntryTime().getHour();
            hourCount[hour]++;
            Duration += h.getVehicle().parkingDuration(); // Sum total duration
        }
        // Calculate average revenue per transaction
        if(totalReceipt > 0){ avgRevenue = totalRevenue/totalReceipt; }
        // Calculate average parking duration per vehicle
        if(Duration > 0){ avgDuration = Duration/totalReceipt; }


        // Find the hour with the most vehicle entries (peak hour)
        int maxVehicles = hourCount[0];
        for (int h = 1; h < 24; h++) {
            if (hourCount[h] > maxVehicles) {
                maxVehicles = hourCount[h];
                peakHour = h; // Store the peak hour> maxVehicles) {
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