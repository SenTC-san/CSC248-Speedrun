import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
//
public class ParkingSystem {
    static String PARKINGNAME = "King Centre parking";
    static int CAPACITY = 10;
    static int LIFTNUM = 3;

    static Scanner sc = new Scanner(System.in);
    static parkingQueue queue = new parkingQueue(CAPACITY);
    static parkingStack stack = new parkingStack(CAPACITY,LIFTNUM);
    //static DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    //static DateTimeFormatter f2 = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static void main(String [] args){
        startSystem();
    }

    public static void startSystem(){
        System.out.println("\tWelcome to" + PARKINGNAME);
        System.out.println("\tParking Lot: \n");
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
        for(int i=0; i<CAPACITY; i++){
            if(i < queue.getQueue().size()){
                Vehicle v = (Vehicle) queue.getQueue().get(i);
                printSlot(v.getPlateNumber(), 10);
            } else
                printSlot("",10);
            System.out.print("  |  ");
        
            //Lifts
            for(int j=0; j<LIFTNUM; j++){
                if(i < stack.getStack().size()){
                    Vehicle v = (Vehicle) stack.getStack().get(i);
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
            clearScreen();
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
        if(!queue.isEmpty()){
            Vehicle v = (Vehicle) queue.dequeue();
            System.out.println("Vehicle '" + v.getPlateNumber() + "' has moved from queue.");
            stack.push(v);
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