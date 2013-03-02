import java.io.IOException;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class StubCompiler {

  public static Object compile(String refId, Class objClass) {
    // 1. send object request message to get the ror first
    RemoteReferenceMessage response = StubCompiler.requestRemoteReference(refId,
            Dispatcher.registryIp, Dispatcher.registryPort);

    // 2. create a stub proxy according to the ror
    return StubCompiler.createStub(response, objClass);
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

  public static Object createStub(RemoteReferenceMessage ror, Class objClass) {
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
    Object result = Proxy.newProxyInstance(objClass.getClassLoader(), interfaces,
            new StubInvocationHandler(ror));

    return result;
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

    if (Main.DEBUG) {
      System.out.println("Made a proxy function call: ");
      System.out.println("id: " + ror.getId());
      System.out.println("method: " + method.getName());
      System.out.println("method return type: " + method.getReturnType().getName());
      System.out.println("DeclaringClass: " + method.getDeclaringClass());

      for (Object obj : args) {
        System.out.println("Args : " + obj.getClass().getName());
      }
    }

    Socket socket = new Socket(InetAddress.getByName(ror.getIp()), ror.getPort());
    CommunicationUtil.send(socket, request);
    InvocationResponseMessage response = (InvocationResponseMessage) CommunicationUtil
            .receive(socket);

    if (response == null)
      return null;

    if (response.isException()) {
      throw (RemoteException) response.getReturnObject();
    } else {
      return response.getReturnObject();
    }
  }

}
