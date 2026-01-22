import java.io.IOException;
import java.util.Scanner;

public class DataProcessor {
    private Object[] sharedData;
    
    public DataProcessor(Object [] data){
        sharedData = data;
    }
    public Object[] getData(){return sharedData;}
    public void setData(Object[] data){sharedData = data;}

    public Object dataAtIndex(int index){
        return sharedData[index];
    }

    public void waitForKey(Scanner sc){
        System.out.print("\nPress ENTER to continue...");
        sc.nextLine();
    }

    // Sadia. (2011, February 2). How to clear the console using Java? Stack Overflow. https://stackoverflow.com/questions/2979383/how-to-clear-the-console-using-java/
    public void clearScreen() { //apparently the previous clear screen does not work with windows cmd prompt, this one works with any
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
