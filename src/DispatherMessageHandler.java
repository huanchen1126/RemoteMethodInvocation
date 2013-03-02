import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.net.Socket;

public class DispatherMessageHandler extends MessageHandler {
  
  public Dispatcher dispatcher;
  
  public DispatherMessageHandler(Socket s, Dispatcher dp) {
    super(s);
    
    this.dispatcher = dp;
  }

  @Override
  public void handle() {
    Object rawmsg = this.getMessage();
    
    if (rawmsg.getClass().getName().compareTo(InvocationRequestMessage.class.getName()) == 0) {
      // invalid message
      return ;
    }
    
    InvocationRequestMessage msg = (InvocationRequestMessage) rawmsg;
    
    Object refObj = this.dispatcher.getReference(msg.getRefObjId());
    Object returnValue = null;
    if (refObj != null) {
      try {
        Class interfaceClass = Class.forName(msg.getInterfaceStr());
        Object[] args = msg.getArguments();

        // generate argument type array
        Class[] argsclasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
          argsclasses[i] = args[i].getClass();
        }

        Method method = interfaceClass.getMethod(msg.getMethodStr(), argsclasses);
        returnValue = method.invoke(refObj, argsclasses);
      } catch (ClassNotFoundException e) {
        returnValue = new RemoteException(e.getMessage());
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
    
    InvocationResponseMessage resmsg = new InvocationResponseMessage(returnValue);
    
    try {
      new ObjectOutputStream(this.socket.getOutputStream()).writeObject(resmsg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
