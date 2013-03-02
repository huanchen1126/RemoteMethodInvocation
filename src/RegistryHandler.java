import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegistryHandler extends MessageHandler {

  private Registry context = null;

  public RegistryHandler(Socket s, Registry c) {
    super(s);
    context = c;
  }

  public void handle() {
    /* read the message from socket */
    Object msg = this.getMessage();
    /* get the message type */
    String msgType = msg.getClass().getName();
    /* handle the message */
    if (msgType.equals("RemoteReferenceMessage")) {
      handleObjectRegisterMessage((RemoteReferenceMessage) msg);
    } else if (msgType.equals("ObjectRequestMessage")) {
      handleObjectRequestMessage((ObjectRequestMessage) msg);
    } else {
      throw new RuntimeException("Illegal message time for registry");
    }
    /* handler job done, close the socket */
    try {
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void handleObjectRegisterMessage(RemoteReferenceMessage msg) {
    if (context.table.containsKey(msg.getId())) {
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(false));
    } else {
      context.table.put(msg.getId(), msg);
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(true));
    }
  }

  public void handleObjectRequestMessage(ObjectRequestMessage msg) {
    String id = msg.getId();
    /* TODO: what if not exist in the table */
    CommunicationUtil.send(this.socket, this.context.table.get(id));
  }

}
