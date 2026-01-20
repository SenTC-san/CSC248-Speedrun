import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class ParkingSystem {
    static String PARKINGNAME = "King Centre parking";
    static int CAPACITY = 3;
    static int LIFTNUM = 3;

    static Scanner sc = new Scanner(System.in);
    static parkingQueue queue = new parkingQueue(CAPACITY);
    static parkingStack stack = new parkingStack(CAPACITY,LIFTNUM);
    static parkingStack<Vehicle>[] lifts;
    //static DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    //static DateTimeFormatter f2 = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static void main(String [] args){
        startSystem();
    }

    public static void startSystem(){
        lifts = new parkingStack[LIFTNUM];
        for (int i = 0; i < LIFTNUM; i++) {
            lifts[i] = new parkingStack<>(CAPACITY, i + 1);
        }
        menu();
    }

    public static void visualiser(){
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

    public static void menu(){
        int selection;
        boolean valid;
        do{ 
            System.out.println("\tWelcome to " + PARKINGNAME);
            //System.out.println("Parking Lot:");
            visualiser();
            String menu = """
                    \n========== MAIN MENU ==========
                    1. Register Vehicle
                    2. Load Vehicle
                    3. Unload Vehicle
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
                        RegisterVehicle();
                        break;
                    case 2:
                        LoadVehicle();
                        break;
                    case 3:
                        //UnloadVehicle();
                        break;
                    case 0:
                        System.out.println("Thank you & Drive Safely!");
                        valid = false;
                        break;
                    default:    
                       System.out.println("Invalid Option!");
                       break;
            }
            waitForKey();
            clearScreen();
        }while (valid);
    }

    public static void RegisterVehicle(){
        System.out.print("Enter vehicle plate number: ");
        String plateNum = sc.nextLine();
        Vehicle v = new Vehicle(plateNum, LocalDateTime.now() ,null);
        if(queue.enqueue(v)){
            System.out.println("Vehicle is in queue.");
        } else {
            System.out.println("Queue is full!");
        }
    }
    public static void LoadVehicle(){
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
/*        if(!queue.isEmpty()){
            Vehicle v = (Vehicle) queue.dequeue();
            System.out.println("Vehicle '" + v.getPlateNumber() + "' has moved from queue.");
            stack.push(v);
        } */
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

    public static void waitForKey(){
        System.out.println("\nPress ENTER to continue...");
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