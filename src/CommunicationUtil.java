import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationUtil {

  /**
   * Get the IP address of local machine
   * 
   * @return The IP address in String format.
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

  /**
   * Read an object form the given socket. Will block until finishing reading
   * 
   * @param socket
   *          the socket connection
   * @return the deserialized object
   */
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
    }

    return msg;
  }

  /**
   * Send an object through a socket connection
   * 
   * @param socket
   *          the socket connection
   * @param obj
   *          the object to be send
   */
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
    }
  }
}
