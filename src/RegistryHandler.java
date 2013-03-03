import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class RegistryHandler extends MessageHandler {

  private Registry registry = null;

  public RegistryHandler(Socket s, Registry r) {
    super(s);
    registry = r;
  }

  public void handle() {
    /* read the message from socket */
    Object msg = this.getMessage();
    /* get the message type */
    String msgType = msg.getClass().getName();
    /* handle the message */
    if (msgType.equals("RemoteReferenceMessage")) {
      if (Main.DEBUG) {
        System.out.println("Received message RemoteReferenceMessage from dispatcher");
      }
      handleObjectRegisterMessage((RemoteReferenceMessage) msg);
    } else if (msgType.equals("ObjectRequestMessage")) {
      if (Main.DEBUG) {
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

  /**
   *  handler register request from dispatcher, send back ack 
   *  
   *  */
  public void handleObjectRegisterMessage(RemoteReferenceMessage msg) {
    if (Main.DEBUG) {
      System.out.println("received ror : " + msg.id + " " + msg.ip + " " + msg.port + " "
              + Arrays.toString(msg.interfaces));
    }
    /* if already have a object with the same id in table */
    if (registry.table.containsKey(msg.getId())) {
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(false));
    } else {
      registry.table.put(msg.getId(), msg);
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(true));
    }
  }

  /**
   *  handle object request from client, send back ror 
   *  */
  public void handleObjectRequestMessage(ObjectRequestMessage msg) {
    String id = msg.getId();
    CommunicationUtil.send(this.socket, this.registry.table.get(id));
  }

}
