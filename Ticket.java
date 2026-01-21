import java.time.format.DateTimeFormatter;

public class Ticket {
    private int ticketID;
    private Vehicle vehicle;
    static private int nextTicketID = 10000;
    static DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    //static DateTimeFormatter f2 = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Ticket(){
        ticketID = 0;
        vehicle = null;
    }
    public Ticket(Vehicle vehicle){
        this.ticketID = ++nextTicketID;
        this.vehicle = vehicle;
    }

    public int getTicketID(){return ticketID; }
    public void setTicketID(int ticketID){this.ticketID = ticketID; }

    public void generateTicket(Vehicle v, boolean pay){
        System.out.printf("%n");
        System.out.printf("====================================%n");
        System.out.printf("        %s%n", ParkingSystem.PARKINGNAME);
        System.out.printf("          PARKING RECEIPT           %n");
        System.out.printf("====================================%n");
        System.out.printf("Vehicle ID     : %-15s%n", v.getVehicleID());
        System.out.printf("Plate Number   : %-15s%n", v.getPlateNumber());
        System.out.printf("Entry Time     : %-15s%n", f1.format(v.getEntryTime()));
        System.out.printf("Exit Time      : %-15s%n", f1.format(v.getExitTime()));
        System.out.printf("Duration       : %-5.2f minute(s)%n", v.parkingDuration());
        System.out.printf("------------------------------------%n");
        System.out.println("Rate / Hour    : " + (pay? "RM" + String.format("%.2f",ParkingSystem.RATEPERHOUR):"N/A"));
        System.out.println("Total Payment  : " + (pay? "RM" + String.format("%.2f",v.calcTotal()):"N/A"));
        System.out.printf("====================================%n");
        System.out.printf("     Thank you & Drive Safely!      %n");
        System.out.printf("====================================%n");
    }
}

/*Attributes
○ticketID : String
○entryTime : Time
○vehicle : Vehicle

Methods
○generateTicket()
○getTicketDetails() : String */