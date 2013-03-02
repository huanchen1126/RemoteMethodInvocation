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
    Object msg = CommunicationUtil.receive(this.socket);
    return msg;
  }

  /**
   * sub classes has to implement this method to use deal with current command
   */
  public abstract void handle();

}
