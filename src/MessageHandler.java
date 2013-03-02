import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public abstract class MessageHandler implements Runnable {
  protected Socket socket = null;

  public MessageHandler(Socket s) {
    this.socket = s;
  }

  @Override
  public void run() {
    this.handle();
  }

  public Object getMessage() {
    if (this.socket == null) {
      throw new RuntimeException("Socket is invalid. Cannot read a command.");
    }

    // get the content of the message
    ObjectInputStream ois = null;
    Object msg = null;
    try {
      ois = new ObjectInputStream(this.socket.getInputStream());
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

  /**
   * sub classes has to implement this method to use deal with current command
   */
  public abstract void handle();

}
