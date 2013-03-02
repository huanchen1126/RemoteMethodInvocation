public class InvocationResponseMessage implements Message {
  private Object returnObject;

  public InvocationResponseMessage(Object obj) {
    this.returnObject = obj;
  }

  public Object getReturnObject() {
    return returnObject;
  }

  public boolean isException() {
    if (returnObject == null) return false;
    
    return (this.returnObject.getClass().getName().compareTo(RemoteException.class.getName()) == 0);
  }
}
