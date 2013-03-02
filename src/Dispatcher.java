import java.io.IOException;
import java.net.*;
import java.util.*;

public class Dispatcher implements Runnable {
  // the referenced objects table in this remote server
  private Map<String, Object> objectTable;

  public static final int port = 55555;

  public Dispatcher() {
    this.objectTable = new TreeMap<String, Object>();

    this.init();
  }

  private void init() {
    
  }

  @Override
  public void run() {
    ServerSocket serverSocket = null;
    boolean listening = true;

    try {
      serverSocket = new ServerSocket(this.port);

      // listen for new collection
      while (listening) {
        Socket socket = serverSocket.accept();

        if (Main.DEBUG) {
          System.err.println("Accept a new invocation request socket.");
        }

        (new Thread(new DispatherMessageHandler(socket, this))).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // clean up the server socket listener if necessary
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public Object getReference(String id) {
    if (id == null) return null;
    
    return this.objectTable.get(id);
  }
  
  private boolean registerObject(String objId, String ip, int port, Class[] interfaces) {
    if (objId == null) return false;
    
    try {
      Socket socket = new Socket(InetAddress.getByName(ip), port);
     
      RemoteReferenceMessage request = new RemoteReferenceMessage(ip, port, interfaces);
      CommunicationUtil.send(socket, request);
      ObjectRegisterAckMessage response = (ObjectRegisterAckMessage) CommunicationUtil.receive(socket);
      
      if (response == null)
        return false;
      else
        return response.isSuccess();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  private void addObject(String id, Object obj) {
    if (id == null || obj == null) return ;
    
    this.objectTable.put(id, obj);
  }
}
