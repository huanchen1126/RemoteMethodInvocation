import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry implements Runnable {
  private String ip;

  private int port;

  public Registry(int port) {
    this.ip = CommunicationUtil.getLocalIPAddress();
    this.port = port;
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
