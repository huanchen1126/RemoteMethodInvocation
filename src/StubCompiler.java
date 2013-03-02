import java.io.IOException;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class StubCompiler {

  public static final String registryIp = "127.0.0.1";

  public static final int registryPort = 55556;

  public static Object compile(String refId) {
    // 1. send object request message to get the ror first
    RemoteReferenceMessage response = StubCompiler.requestRemoteReference(refId, registryIp,
            registryPort);

    // 2. create a stub proxy according to the ror
    return StubCompiler.createStub(response);
  }

  public static RemoteReferenceMessage requestRemoteReference(String refId, String ip, int port) {
    ObjectRequestMessage request = new ObjectRequestMessage(refId);

    Socket socket = null;
    try {
      socket = new Socket(InetAddress.getByName(ip), port);

      CommunicationUtil.send(socket, request);
      RemoteReferenceMessage response = (RemoteReferenceMessage) CommunicationUtil.receive(socket);

      return response;
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  public static Object createStub(RemoteReferenceMessage ror) {
    if (ror == null)
      return null;

    Class[] interfaces = ror.getInterfaces();
    if (interfaces == null || interfaces.length == 0) {
      if (Main.DEBUG) {
        System.err.println("No interfaces information form ROR.");
      }
      return null;
    }

    // compile the stub to call remote method
    Object result = Proxy.newProxyInstance(Object.class.getClassLoader(), interfaces,
            new StubInvocationHandler(ror));

    return null;
  }
}

class StubInvocationHandler implements InvocationHandler {

  private RemoteReferenceMessage ror;

  public StubInvocationHandler(RemoteReferenceMessage r) {
    this.ror = r;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    InvocationRequestMessage request = new InvocationRequestMessage(ror.getId(), method.getName(),
            method.getReturnType().getName(), method.getDeclaringClass(), args);

    Socket socket = new Socket(InetAddress.getByName(ror.getIp()), ror.getPort());
    CommunicationUtil.send(socket, request);
    InvocationResponseMessage response = (InvocationResponseMessage) CommunicationUtil.receive(socket);
    
    if (response == null) return null;
    
    if (response.isException()) {
      throw (RemoteException) response.getReturnObject();
    }else {
      return response.getReturnObject();
    }
  }

}
