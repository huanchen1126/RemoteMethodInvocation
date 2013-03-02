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
    
    if (rawmsg.getClass().getName().compareTo(InvocationRequestMessage.class.getName()) != 0) {
      // invalid message
      System.err.println("Invalid Message");
      if (Main.DEBUG) {
        System.err.println(rawmsg.getClass().getName() + ", " + InvocationRequestMessage.class.getName());
      }
      return ;
    }
    
    InvocationRequestMessage msg = (InvocationRequestMessage) rawmsg;
    
    Object refObj = this.dispatcher.getReference(msg.getRefObjId());
    Object returnValue = null;
    if (refObj != null) {
      try {
        Class<?> interfaceClass = msg.getInterfaceClass();
        Object[] args = msg.getArguments();

        // generate argument type array
        Class<?>[] argsclasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
          argsclasses[i] = args[i].getClass();
        }
        
        System.out.println(interfaceClass.getName() + " " + msg.getMethodStr());
        for( Class<?> c : argsclasses) {
          System.out.println(c.getName());
        }
        
        for( Method m : interfaceClass.getMethods()) {
          System.out.println(m.getName());
          for (Class c : m.getParameterTypes())
            System.out.println(c.getName());
        }

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
      if (Main.DEBUG) {
        System.out.println("Object not found.");
      }
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
