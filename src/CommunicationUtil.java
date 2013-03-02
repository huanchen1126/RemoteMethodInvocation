import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationUtil {

  /**
   * 
   * @return The IP address of local machine in String format.
   */
  public static String getLocalIPAddress() {
    InetAddress addr = null;
    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    if (addr == null)
      return null;
    else
      return addr.getHostAddress();
  }

  public static Object receive(Socket socket) {
    if (socket == null) {
      throw new RuntimeException("Socket is invalid. Cannot read a command.");
    }
    ObjectInputStream ois = null;
    Object msg = null;
    try {
      ois = new ObjectInputStream(socket.getInputStream());
      msg = ois.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        ois.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return msg;
  }
  
  public static void send(Socket socket, Object obj) {
    if (socket == null) {
      throw new RuntimeException("Socket is invalid. Cannot read a command.");
    }
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      out.writeObject(obj);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * send command
   * 
   * @throws ConnectException
   * */
  public static void send(String ip, int port, Object obj) throws ConnectException {
    Socket socket = null;
    ObjectOutputStream out = null;
    try {
      socket = new Socket(InetAddress.getByName(ip), port);
      out = new ObjectOutputStream(socket.getOutputStream());
      out.writeObject(obj);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (ConnectException e) {
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
