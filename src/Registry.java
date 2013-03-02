import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Registry implements Runnable {
  private String ip;

  private int port;
  
  public HashMap<String, RemoteReferenceMessage> table = null;

  public Registry(int port) {
    this.ip = CommunicationUtil.getLocalIPAddress();
    this.port = port;
    this.table = new HashMap<String, RemoteReferenceMessage>();
  }

  @Override
  public void run() {
    /* start listener */
    ServerSocket serverSocket = null;
    boolean listening = true;
    try {
      serverSocket = new ServerSocket(this.port);
      while (listening) {
        Socket socket = serverSocket.accept();
        new Thread(new RegistryHandler(socket, this)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
