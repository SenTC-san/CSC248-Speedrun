import java.time.Duration;
import java.time.LocalDateTime;

public class Vehicle {
    
    private int vehicleID;
    private String plateNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    static private int nextVehicleID = 1000;

    public Vehicle(){
        vehicleID = 0;
        plateNumber = "";
        entryTime = null;
        exitTime = null;
    }

    public Vehicle(String plateNumber, LocalDateTime entryTime, LocalDateTime exitTime){
        this.vehicleID = ++nextVehicleID;
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

    public double parkingDuration(){
        if (getEntryTime() == null || getExitTime() == null) {
            return 0.0;
        }
        Duration dur = Duration.between(getEntryTime(), getExitTime());
        return dur.getSeconds() / 60; //coverts to minutes
    }
    public double calcTotal(double RATEPERHOUR){
        double total = (parkingDuration() / 60) * RATEPERHOUR;
        return total;
    }
    public static String toFileWrite(Vehicle v){
        return  v.getVehicleID() + ";" +
                v.getPlateNumber() + ";" +
                v.getEntryTime() + ";" +
                v.getExitTime() + ";" + "\n" ;
    } 
}

