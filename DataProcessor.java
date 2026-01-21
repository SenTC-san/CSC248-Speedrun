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
}
