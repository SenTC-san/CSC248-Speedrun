import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class Vehicle {
    
    private int vehicleID;
    private String plateNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Vehicle(){
        vehicleID = 1000;
        plateNumber = "";
        entryTime = null;
        exitTime = null;
    }

    public Vehicle(int vehicleID, String plateNumber, LocalDateTime entryTime, LocalDateTime exitTime){
        this.vehicleID = vehicleID;
        this.plateNumber = plateNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    public int getVehicleID(){return vehicleID; }
    public String getPlateNumber(){return plateNumber; }
    public LocalDateTime getEntryTime(){return entryTime; }
    public LocalDateTime getExitTime(){return exitTime; }

    public void setVehicleID(int vehicleID){this.vehicleID = vehicleID; }
    public void setPlateNumber(String plateNumber){this.plateNumber = plateNumber; }
    public void setEntryTime(LocalDateTime entryTime){this.entryTime = entryTime; }
    public void setExitTime(LocalDateTime exitTime){this.exitTime = exitTime; }
    
    // Calculate how long the vehicle was parked in minutes
    public double parkingDuration(){
        if (getEntryTime() == null || getExitTime() == null) {
            return 0.0;
        }
        Duration dur = Duration.between(getEntryTime(), getExitTime());
        return dur.getSeconds() / 60; // Converts seconds to minutes
    }
    
    // Calculate total parking fee based on duration and hourly rate
    public double calcTotal(double RATEPERHOUR){
        double total = (parkingDuration() / 60) * RATEPERHOUR; // Convert minutes to hours and multiply by rate
        return total;
    }
    
    // Write vehicle and receipt information to history file
    public void toFileWrite(Receipt r){
        File f = fileChecker("history.txt"); // Ensure file exists
        
        // Format: receiptID;vehicleID;plate;entryTime;exitTime;fee;\n
        String write =  r.getReceiptID() + ";" +
                        vehicleID + ";" +
                        plateNumber + ";" +
                        (entryTime != null? entryTime: "N/A") + ";" +
                        (exitTime != null? exitTime: "N/A") + ";" +
                        (r.getPay()? r.getTotalPayment(): "N/A") + ";" + "\n";

        try(FileWriter fr = new FileWriter(f, true)){
                fr.write(write);
            } catch(IOException e){
                System.out.println("An error occurred writing to file.");
                e.printStackTrace();
            }
    }
    
    // Check if file exists, create it if it doesn't
    public File fileChecker(String fileName){
        File f = new File(fileName);
        if(!f.exists()){ //check if file exists
            try{
                f.createNewFile(); //create new file if it doesnt
            } catch (IOException e){
                System.out.println("Error creating file!");
            }
        }
        return f;
    }
    
    // Read all parking history records from file and return as LinkedList of Receipts
    public LinkedList<Receipt> toFileRead(){
        LinkedList<Receipt> reader = new LinkedList<>();
        File f = fileChecker("history.txt");

        try(Scanner file = new Scanner(f)){
            while(file.hasNextLine()){
                String data[] = file.nextLine().split(";"); // Parse semicolon-delimited data from file
                
                // Extract individual fields from parsed data
                int receiptID = Integer.parseInt(data[0]);
                int vehicleID = Integer.parseInt(data[1]);
                String plateNumber = data[2];
                LocalDateTime entryTime = LocalDateTime.parse(data[3]);
                LocalDateTime exitTime = LocalDateTime.parse(data[4]);
                boolean pay = !data[5].equals("N/A") && Boolean.parseBoolean(data[5]); // if parking needs to be paid
                double totalPrice = data[5].equals("N/A") ? 0.0 : Double.parseDouble(data[5]);
                
                // Create Vehicle and Receipt objects and add to list
                Vehicle v = new Vehicle(vehicleID, plateNumber, entryTime, exitTime);
                reader.add(new Receipt(receiptID, v, totalPrice, pay));
            }
        } catch(FileNotFoundException e){
            System.out.println("File not found!");
        } catch(Exception e){
            System.out.println("Error reading file ");
            e.printStackTrace();
        }
        return reader;
    }
    
    // Display visual representation of parking lot showing queue and all lifts
    public void visualiser(parkingQueue<Vehicle> queue, parkingStack<Vehicle>[] lifts, DataProcessor dp){
        int LIFTNUM = (int) dp.dataAtIndex(2);
        int CAPACITY = (int) dp.dataAtIndex(1);
        
        // Print header row with column labels
        System.out.print("   Queue      |     ");
        for (int l = 1; l <= LIFTNUM; l++){
            System.out.printf("Lift %-7d ", l);
        }
        System.out.println();

        // Print rows showing vehicles in queue and each lift level
        for(int r=0; r<CAPACITY; r++){

            // Display queue vehicles from top position
            if(r < queue.getQueue().size()){
                Vehicle v = (Vehicle) queue.getQueue().get(r);
                printSlot(v.getPlateNumber(), 10);
            } else
                printSlot("",10);
            System.out.print("  |  ");
        
            // Display vehicles in each lift (top-to-bottom representation)
            for(int l=0; l<LIFTNUM; l++){
                LinkedList<Vehicle> slist = lifts[l].getStack();
                int indexFromTop = slist.size() - 1 - r; // Calculate position from top
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
    
    // Print text centered in a visual slot [like this]
    public void printSlot(String text, int slotWidth){
        // Truncate text if longer than slot width
        if(text.length() > slotWidth){
            text = text.substring(0, slotWidth);
        }
        
        // Calculate padding for centered alignment
        int totalSpace = slotWidth - text.length();
        int leftSpace = totalSpace / 2;
        int rightSpace = totalSpace - leftSpace;

        System.out.print("[");
        for (int i = 0; i < leftSpace; i++) {
            System.out.print(" ");
        }
        
        // Print formatted slot with centered text
        System.out.print(text);

        for (int i = 0; i < rightSpace; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
        }
    }
