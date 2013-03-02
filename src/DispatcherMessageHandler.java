import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.net.Socket;

/**
 * The class handles each remote method invocation call request.
 */
public class DispatcherMessageHandler extends MessageHandler {
  
  public Dispatcher dispatcher;
  
  public DispatcherMessageHandler(Socket s, Dispatcher dp) {
    super(s);
    
    this.dispatcher = dp;
  }

  @Override
  public void handle() {
    Object rawmsg = this.getMessage();
    
    if (rawmsg.getClass().getName().compareTo(InvocationRequestMessage.class.getName()) != 0) {
      // invalid message
      System.err.println("Invalid Message");
      if (Main.DEBUG) {
        System.err.println(rawmsg.getClass().getName() + ", " + InvocationRequestMessage.class.getName());
      }
      return ;
    }
    
    InvocationRequestMessage msg = (InvocationRequestMessage) rawmsg;
    
    // get the actual reference of the request object
    Object refObj = this.dispatcher.getReference(msg.getRefObjId());
    
    Object returnValue = null;
    
    if (refObj != null) {
      try {
        // get the class of the request interface
        Class<?> interfaceClass = msg.getInterfaceClass();
        
        // get the arguments for the method invocation
        Object[] args = msg.getArguments();

        // generate argument type array
        Class<?>[] argsclasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
          argsclasses[i] = args[i].getClass();
        }
        
        // invoke this method
        Method method = interfaceClass.getMethod(msg.getMethodStr(), argsclasses);
        returnValue = method.invoke(refObj, args);
      } catch (SecurityException e) {
        returnValue = new RemoteException(e.getMessage());
      } catch (NoSuchMethodException e) {
        returnValue = new RemoteException(e.getMessage());
      } catch (IllegalArgumentException e) {
        returnValue = new RemoteException(e.getMessage());
      } catch (IllegalAccessException e) {
        returnValue = new RemoteException(e.getMessage());
      } catch (InvocationTargetException e) {
        returnValue = new RemoteException(e.getMessage());
      }
    }else {
      returnValue = new RemoteException("Object not found.");
    }
    
    // patch the return value into the response message
    InvocationResponseMessage resmsg = new InvocationResponseMessage(returnValue);
    
    try {
      // send back the response message
      new ObjectOutputStream(this.socket.getOutputStream()).writeObject(resmsg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
