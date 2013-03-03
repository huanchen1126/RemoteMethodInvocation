import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * This class is in charge of accepting remote method invocation request, executing the method and
 * sending back the return value.
 */
public class Dispatcher implements Runnable {
  // the referenced objects table
  private Map<String, Object> objectTable;

  // listening port for remote method invocation request
  public static int port = 0;

  // the ip address of the registry server
  public static String registryIp = "";

  // the port of the registry server
  public static int registryPort = 0;

  public Dispatcher() {
    this.objectTable = new TreeMap<String, Object>();

    this.init();
  }

  /**
   * initialize several object for remove method invocation
   */
  private void init() {
    TestClass testobj = new TestClass();

    if (this.registerObject("testobj", testobj)) {
      this.addObject("testobj", testobj);
    } else {
      System.out.println("register failed");
    }
  }

  @Override
  public void run() {
    ServerSocket serverSocket = null;
    boolean listening = true;

    try {
      serverSocket = new ServerSocket(this.port);

      // listen for new collection
      while (listening) {
        Socket socket = serverSocket.accept();
        
        System.out.println("Receive an InvocationRequestMessage.");
        
        (new Thread(new DispatcherMessageHandler(socket, this))).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // clean up the server socket listener if necessary
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * find the object reference in the table
   * 
   * @param id
   * @return object, if the reference id exist in the table; null, otherwise.
   */
  public Object getReference(String id) {
    if (id == null)
      return null;

    return this.objectTable.get(id);
  }

  /**
   * Register an object in the registry.
   * 
   * @param objId
   *          the id for the object
   * @param interfaces
   *          the interfaces this object implements
   * @return whether the object has been successfully registered.
   */
  private boolean registerObject(String objId, Object obj) {
    if (objId == null)
      return false;

    Class[] interfaces = obj.getClass().getInterfaces();
    boolean isRemote = false;

    // if the object does not
    for (Class c : interfaces) {
      if (c.equals(RemoteObject.class)) {
        isRemote = true;
      }
    }

    // if the object does not implement the RemoteObject interface,
    // it can not be registered.
    if (!isRemote) {
      return false;
    }

    try {
      Socket socket = new Socket(InetAddress.getByName(Dispatcher.registryIp),
              Dispatcher.registryPort);

      // generate a request message
      RemoteReferenceMessage request = new RemoteReferenceMessage(objId, socket.getLocalAddress()
              .getHostAddress(), this.port, interfaces);

      // send this request
      CommunicationUtil.send(socket, request);

      // block waiting for the response message
      ObjectRegisterAckMessage response = (ObjectRegisterAckMessage) CommunicationUtil
              .receive(socket);

      if (response == null)
        return false;
      else
        return response.isSuccess();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Add an object into the object reference table
   * 
   * @param id
   *          the object reference id
   * @param obj
   *          the object
   */
  private void addObject(String id, Object obj) {
    if (id == null || obj == null)
      return;

    this.objectTable.put(id, obj);
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      System.err.println("Usage: Dispatcher <dispatcher_port> <registry_ip> <registery_port>");
      return;
    }

    Dispatcher.port = Integer.parseInt(args[0]);
    Dispatcher.registryIp = args[1];
    Dispatcher.registryPort = Integer.parseInt(args[2]);

    // start the listening thread
    new Thread(new Dispatcher()).start();

  }
}
