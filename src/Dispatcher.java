import java.util.*;

public class Dispatcher implements Runnable {
  // the referenced objects table in this remote server
  private Map<String, Object> objectTable;
  
  public static final int port = 55555;
  
  public Dispatcher() {
    this.objectTable = new TreeMap<String, Object>();
  }

  @Override
  public void run() {
    
  }
}
