import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Registry implements Runnable {
  public static String ip;

  public static int port;

  /* the table which maps object id to ror */
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
      /* get a new request and start a new handler thread */
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

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: Registry <registry_port>");
      return;
    }
    new Thread(new Registry(Integer.parseInt(args[0]))).start();
  }
}
