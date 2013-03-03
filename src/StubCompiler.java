import java.io.IOException;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;
import java.util.*;

public class StubCompiler {

  public static Map<String, Object> objTableCache = new TreeMap<String, Object>();

  /**
   * compile a local object proxy stub
   * 
   * @param refId
   *          the id of the remote object
   * @param objClass
   *          the request interface of the object
   * @return a object proxy stub for remote method invocation; null, if any error occur
   */
  public static Object compile(String refId, Class objClass, String rip, int rport) {
    RemoteReferenceMessage response = null;

    if (objTableCache.containsKey(refId)) {
      // if the object has already been cached
      return objTableCache.get(refId);
    } else {
      // 1. send object request message to get the ror first
      response = StubCompiler.requestRemoteReference(refId, rip, rport);
      
      // 2. create a stub proxy according to the ror
      Object obj = StubCompiler.createStub(response, objClass);
      
      objTableCache.put(refId, obj);
      return obj;
    }
  }

  /**
   * Make a remote object reference request
   * 
   * @param refId
   *          the id for that object
   * @param ip
   *          the ip address of the registry
   * @param port
   *          the port number of the registry
   * @return
   */
  public static RemoteReferenceMessage requestRemoteReference(String refId, String ip, int port) {
    ObjectRequestMessage request = new ObjectRequestMessage(refId);

    Socket socket = null;
    try {
      socket = new Socket(InetAddress.getByName(ip), port);

      // send remote object reference request
      CommunicationUtil.send(socket, request);

      // block wait for ROR message
      RemoteReferenceMessage response = (RemoteReferenceMessage) CommunicationUtil.receive(socket);

      // return the ROR message
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

  /**
   * Create the local object stub according to the ROR message
   * 
   * @param ror
   *          the ROR message
   * @param objClass
   *          the request interface of the object
   * @return the local object stub; null, if any error occur
   */
  public static Object createStub(RemoteReferenceMessage ror, Class objClass) {
    if (ror == null)
      return null;

    Class[] interfaces = ror.getInterfaces();
    if (interfaces == null || interfaces.length == 0) {
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

    // wrap the remote method information for reflection into a request message
    InvocationRequestMessage request = new InvocationRequestMessage(ror.getId(), method.getName(),
            method.getReturnType().getName(), method.getDeclaringClass(), args);

    Socket socket = new Socket(InetAddress.getByName(ror.getIp()), ror.getPort());

    // send the request message
    CommunicationUtil.send(socket, request);

    // block wait for the return value of the remote method call
    InvocationResponseMessage response = (InvocationResponseMessage) CommunicationUtil
            .receive(socket);

    if (response == null)
      return null;

    if (response.isException()) {
      // if the return value is an exception, throw this exception
      throw (RemoteException) response.getReturnObject();
    } else {
      // otherwise, return the return value
      return response.getReturnObject();
    }
  }

}
