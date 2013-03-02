public class InvocationResponseMessage implements Message {
  private Object returnObject;

  public InvocationResponseMessage(Object obj) {
    this.returnObject = obj;
  }

  public Object getReturnObject() {
    return returnObject;
  }

  /**
   * Whether the return value is the actual return value or just an exception.
   * 
   * @return
   */
  public boolean isException() {
    if (returnObject == null)
      return false;

    return (this.returnObject.getClass().getName().compareTo(RemoteException.class.getName()) == 0);
  }
}
