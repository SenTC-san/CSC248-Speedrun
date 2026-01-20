
public class Ticket {
    private int ticketID;
    private Vehicle vehicle;
    static private int nextTicketID = 10000;

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

    public void generateTicket(Vehicle v){
        System.out.printf("%n");
        System.out.printf("====================================%n");
        //System.out.printf("        %s%n", parkingName);
        System.out.printf("         PARKING RECEIPT%n");
        System.out.printf("====================================%n");
        System.out.printf("Plate Number   : %-15s%n", v.getPlateNumber());
        //System.out.printf("Vehicle Type   : %-15s%n", vehicleType);
        System.out.printf("Entry Time     : %-15s%n", v.getEntryTime());
        System.out.printf("Exit Time      : %-15s%n", v.getExitTime());
        System.out.printf("Duration       : %-2d hour(s)%n", v.parkingDuration());
    /*    System.out.printf("------------------------------------%n");
        System.out.printf("Rate / Hour    : RM %6.2f%n", ratePerHour);
        System.out.printf("Total Payment  : RM %6.2f%n", totalAmount); */
        System.out.printf("====================================%n");
        System.out.printf(" Thank you & Drive Safely!%n");
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