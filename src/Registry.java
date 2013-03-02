import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cmu.ds2013s.ManagerContext;
import org.cmu.ds2013s.ProcessManager;

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
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
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
