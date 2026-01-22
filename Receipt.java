import java.time.format.DateTimeFormatter;

public class Receipt {
    private int receiptID;
    private Vehicle vehicle;
    private double totalPayment;
    private boolean pay;
    
    public Receipt(){
        receiptID = 10000;
        vehicle = null;
        totalPayment = 0;
        pay = false;
    }
    public Receipt(Vehicle vehicle){
        this.receiptID = 10000;
        this.vehicle = vehicle;
        this.totalPayment = 0;
        this.pay = false;
    }
    public Receipt(int receiptID, Vehicle vehicle, double totalPayment, boolean pay){
        this.receiptID = receiptID;
        this.vehicle = vehicle;
        this.totalPayment = totalPayment;
        this.pay = pay;
    }

    public int getReceiptID(){return receiptID; }
    public Vehicle getVehicle(){return vehicle; }
    public double getTotalPayment(){return totalPayment; }
    public boolean getPay(){return pay; }

    public void setReceiptID(int receiptID){this.receiptID = receiptID; }
    public void setVehicle(Vehicle vehicle){this.vehicle = vehicle; }
    public void setTotalPayment(double totalPayment){this.totalPayment = totalPayment; }
    public void setPay(boolean pay){this.pay = pay; }

    public void generateReceipt(DataProcessor dp){
        String PARKINGNAME = (String) dp.dataAtIndex(0);
        double RATEPERHOUR = (double) dp.dataAtIndex(3);
        Vehicle v = vehicle;
        int receipt = getReceiptID();

        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.printf("%n");
        System.out.printf("====================================%n");
        System.out.printf("        %s%n", PARKINGNAME);
        System.out.printf("          PARKING RECEIPT           %n");
        System.out.printf("====================================%n");
        System.out.printf("Receipt ID     : %-15d%n", receipt);
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