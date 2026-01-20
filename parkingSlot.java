public class parkingSlot{
    
    private int slotID;
    private boolean isOccupied;
    private Vehicle vehicle;
    static private int nextSlotID = 1000;

    public parkingSlot(){
        slotID = 0;
        isOccupied = false;
        vehicle = null;
    }

    public parkingSlot(boolean isOccupied, Vehicle vehicle){
        this.slotID = ++nextSlotID;
        this.isOccupied = isOccupied;
        this.vehicle = vehicle;
    }

    public int getSlotID(){return slotID; }
    public boolean getIsOccupied(){return isOccupied; }
    public Vehicle getVehicle(){return vehicle; }

    public void setSlotID(int slotID){this.slotID = slotID; }
    public void setIsOccupied(boolean isOccupied){this.isOccupied = isOccupied; }
    public void setVehicle(Vehicle vehicle){this.vehicle = vehicle; }

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
        System.out.println("]");
    }
}
