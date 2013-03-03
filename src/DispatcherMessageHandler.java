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
    System.out.println("Handle this InvocationRequestMessage.");
    Object rawmsg = this.getMessage();
    
    if (rawmsg.getClass().getName().compareTo(InvocationRequestMessage.class.getName()) != 0) {
      // invalid message
      System.err.println("Invalid Message");
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
        System.out.println("Invoke the requested method.");
        returnValue = method.invoke(refObj, args);
      } catch (Exception e) {
        // if the method call throw a exception, catch it here;
        returnValue = new RemoteException(e.getMessage());
      }
    }else {
      returnValue = new RemoteException("Object not found.");
    }
    
    // patch the return value into the response message
    InvocationResponseMessage resmsg = new InvocationResponseMessage(returnValue);
    
    try {
      // send back the response message
      System.out.println("Send back the result by an InvocationResponseMessage.\n");
      new ObjectOutputStream(this.socket.getOutputStream()).writeObject(resmsg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
