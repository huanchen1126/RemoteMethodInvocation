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
      RemoteReferenceMessage rrm = (RemoteReferenceMessage) msg;
      System.out.println("Received object registration from dispatcher ip:" + rrm.ip + ",port:"
              + rrm.port);
      handleObjectRegisterMessage(rrm);
    } else if (msgType.equals("ObjectRequestMessage")) {
      ObjectRequestMessage orm = (ObjectRequestMessage) msg;
      System.out.println("Received object registration from client for object:" + orm.getId());
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
   * handler register request from dispatcher, send back ack
   * 
   * */
  public void handleObjectRegisterMessage(RemoteReferenceMessage msg) {
    /* if already have a object with the same id in table */
    if (registry.table.containsKey(msg.getId())) {
      System.out.println("Obejct id:" + msg.getId() + " already exists");
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(false));
    } else {
      registry.table.put(msg.getId(), msg);
      CommunicationUtil.send(this.socket, new ObjectRegisterAckMessage(true));
    }
  }

  /**
   * handle object request from client, send back ror
   * */
  public void handleObjectRequestMessage(ObjectRequestMessage msg) {
    String id = msg.getId();
    CommunicationUtil.send(this.socket, this.registry.table.get(id));
  }

}
