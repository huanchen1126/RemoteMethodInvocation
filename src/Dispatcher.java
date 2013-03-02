import java.io.IOException;
import java.net.*;
import java.util.*;

public class Dispatcher implements Runnable {
  // the referenced objects table in this remote server
  private Map<String, Object> objectTable;

  public static final int port = 55555;

  public static final String registryIp = "128.237.123.158";

  public static final int registryPort = 1234;

  public Dispatcher() {
    this.objectTable = new TreeMap<String, Object>();

    this.init();
  }

  private void init() {
    TestInterface testobj = new TestInterface() {

      @Override
      public void functionA(Integer a, Integer b) {
        System.out.println(a + "-" + b);
      }

      @Override
      public void functionB(Integer a) {
        System.out.println(a);
      }

    };

    Class[] inters = { TestInterface.class };
    if (this.registerObject("testobj", inters)) {
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

        if (Main.DEBUG) {
          System.err.println("Accept a new invocation request socket.");
        }

        (new Thread(new DispatherMessageHandler(socket, this))).start();
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

  public Object getReference(String id) {
    if (id == null)
      return null;

    return this.objectTable.get(id);
  }

  private boolean registerObject(String objId, Class[] interfaces) {
    if (objId == null)
      return false;

    try {
      Socket socket = new Socket(InetAddress.getByName(Dispatcher.registryIp),
              Dispatcher.registryPort);

      RemoteReferenceMessage request = new RemoteReferenceMessage(objId, socket.getLocalAddress()
              .getHostAddress(), this.port, interfaces);
      CommunicationUtil.send(socket, request);
      ObjectRegisterAckMessage response = (ObjectRegisterAckMessage) CommunicationUtil
              .receive(socket);

      if (Main.DEBUG) {
        System.out.println("Register Status: " + response.isSuccess());
      }

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

  private void addObject(String id, Object obj) {
    if (id == null || obj == null)
      return;

    this.objectTable.put(id, obj);
  }

  public static void main(String[] args) {
    new Thread(new Dispatcher()).start();

    // RemoteReferenceMessage res = StubCompiler.requestRemoteReference("testobj",
    // "128.237.123.158", 1234);
    // if (res == null) {
    // System.out.println("response null");
    // }else {
    // System.out.println("response not null");
    // }

    TestInterface a = (TestInterface) StubCompiler.compile("testobj", TestInterface.class);

    if (a == null) {
      System.out.println("a null");
    } else {
      a.functionA(1, 2);
    }
  }
}
