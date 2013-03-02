import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

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
      if(Main.DEBUG){
        System.out.println("Received message RemoteReferenceMessage from dispatcher");
      }
      handleObjectRegisterMessage((RemoteReferenceMessage) msg);
    } else if (msgType.equals("ObjectRequestMessage")) {
      if(Main.DEBUG){
        System.out.println("Received message ObjectRequestMessage from client");
      }
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
    if(Main.DEBUG){
      System.out.println("received ror : "+ msg.id+" "+msg.ip+" "+ msg.port+" "+Arrays.toString(msg.interfaces));
    }
    if (context.table.containsKey(msg.getId())) {
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(false));
    } else {
      context.table.put(msg.getId(), msg);
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(true));
    }
  }

  public void handleObjectRequestMessage(ObjectRequestMessage msg) {
    String id = msg.getId();
    CommunicationUtil.send(this.socket, this.context.table.get(id));
  }

}
