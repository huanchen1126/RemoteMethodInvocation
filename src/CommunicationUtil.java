

import java.io.IOException;
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
    InetAddress addr = null;;
    
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
   * send command
   * @throws ConnectException 
   * */
  public static void sendCommand(String ip, int port, byte[] content) throws ConnectException {
    Socket socket = null;
    try {
      socket = new Socket(InetAddress.getByName(ip),port);
      OutputStream out = socket.getOutputStream(); 
      out.write(content);
      out.close();
      socket.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch(ConnectException e){
      throw e;
    }catch (IOException e) {
      e.printStackTrace();
    }
  }
}
