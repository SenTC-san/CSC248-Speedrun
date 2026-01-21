import java.time.format.DateTimeFormatter;

public class Receipt {
    private int receiptID;
    private Vehicle vehicle;
    private double totalPayment;
    static private int nextTicketID = 10000;

    public Receipt(){
        receiptID = 0;
        vehicle = null;
    }
    public Receipt(Vehicle vehicle){
        this.receiptID = ++nextTicketID;
        this.vehicle = vehicle;
    }

    public int getReceiptID(){return receiptID; }
    public void setReceiptID(int ticketID){this.receiptID = ticketID; }

    public void generateReceipt(boolean pay, DataProcessor dp){
        String PARKINGNAME = (String) dp.dataAtIndex(0);
        double RATEPERHOUR = (double) dp.dataAtIndex(3);
        Vehicle v = vehicle;
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.printf("%n");
        System.out.printf("====================================%n");
        System.out.printf("        %s%n", PARKINGNAME);
        System.out.printf("          PARKING RECEIPT           %n");
        System.out.printf("====================================%n");
        System.out.printf("Vehicle ID     : %-15s%n", v.getVehicleID());
        System.out.printf("Plate Number   : %-15s%n", v.getPlateNumber());
        System.out.printf("Entry Time     : %-15s%n", f1.format(v.getEntryTime()));
        System.out.printf("Exit Time      : %-15s%n", f1.format(v.getExitTime()));
        System.out.printf("Duration       : %-5.2f minute(s)%n", v.parkingDuration());
        System.out.printf("------------------------------------%n");
        System.out.println("Rate / Hour    : " + (pay? "RM" + String.format("%.2f",RATEPERHOUR):"N/A"));
        System.out.println("Total Payment  : " + (pay? "RM" + String.format("%.2f",v.calcTotal(RATEPERHOUR)):"N/A"));
        System.out.printf("====================================%n");
        System.out.printf("     Thank you & Drive Safely!      %n");
        System.out.printf("====================================%n");
    }
}